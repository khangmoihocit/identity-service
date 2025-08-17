package com.devteria.identityservice.service;

import com.devteria.identityservice.dto.request.AuthenticationRequest;
import com.devteria.identityservice.dto.request.IntrospectRequest;
import com.devteria.identityservice.dto.request.LogoutRequest;
import com.devteria.identityservice.dto.request.RefreshRequest;
import com.devteria.identityservice.dto.response.AuthenticationResponse;
import com.devteria.identityservice.dto.response.IntrospectResponse;
import com.devteria.identityservice.entity.InvalidatedToken;
import com.devteria.identityservice.entity.User;
import com.devteria.identityservice.exception.AppException;
import com.devteria.identityservice.exception.ErrorCode;
import com.devteria.identityservice.repository.InvalidatedTokenRepository;
import com.devteria.identityservice.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor //cac thuoc tinh final se tu tao constructor nen khong can annotation @Autowired
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true) //makeFinal khoi tao cac thuoc tinh la final
public class AuthenticationService {
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;

    //chữ ký rất quan trọng, vì chỉ cần chữ ký sẽ có thể thay đổi token
    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;


    //xac thuc token tu client gui len
    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();

        boolean isValid = true;
        try {
            verifyToken(token, false);
        } catch (AppException ex) {
            isValid = false;
        }

        return IntrospectResponse.builder()
                .valid(isValid) //token có đúng và ngày hạn sau ngày hôm nay không
                .build();
    }


    //đăng nhập thành công sẽ trả về token
    public AuthenticationResponse authentication(AuthenticationRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        //ham matches dung de so sanh 2 bcrypt, tra ve true false.
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!authenticated)
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }


    //khi logout sẽ lưu id token và expiry vào database
    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try{
            //logout theo thời hạn của refresh
            var signToken = verifyToken(request.getToken(), true);

            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jit)
                    .expiryTime(expiryTime)
                    .build();
            invalidatedTokenRepository.save(invalidatedToken);
        }catch (AppException ex){
            System.out.println(ex.getMessage());
        }
    }

    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        //xác thực token
        var signedJwt = verifyToken(request.getToken(), true);

        //lưu token cũ vào bảng invalid
        var jit = signedJwt.getJWTClaimsSet().getJWTID();
        var expiryTime = signedJwt.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jit)
                .expiryTime(expiryTime)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);

        //lấy thông tin user để tạo token mới
        var username = signedJwt.getJWTClaimsSet().getSubject();
        var user = userRepository.findByUsername(username).orElseThrow(
                () -> new AppException(ErrorCode.UNAUTHENTICATED));

        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    //trả về token được parse và xác thực token còn hạn và đúng không
    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expityTime = (isRefresh) ?
                new Date(signedJWT.getJWTClaimsSet().getIssueTime()
                        .toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        //xac thuc token tu client
        var verified = signedJWT.verify(verifier); //tra ve true/false

        if (!(verified && expityTime.after(new Date()))) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        //kiểm tra token id có nằm trong bảng database không, nếu có => token không còn hợp lệ
        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }

    private String generateToken(User user) {
        //thuat toan hs512
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())  //dai dien user dang dang nhap
                .issuer("khangmoihocit.com")  //de xac dinh cai token nay dc issuer tu ai
                .issueTime(new Date())  //thoi gian bat dau ap dung 
                .expirationTime(new Date
                        (Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))  //thoi han token
                .jwtID(UUID.randomUUID().toString()) //token id
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        //gom header and payload
        JWSObject jwsObject = new JWSObject(header, payload);

        //ky token
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
            });
        }
        return stringJoiner.toString();
    }

}

