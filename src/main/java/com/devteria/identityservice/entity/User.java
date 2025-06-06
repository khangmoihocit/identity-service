package com.devteria.identityservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
//spring:  web, jpa, mysql
/*

@tathanh203
1 tháng trước
ví dụ thế này, có user và userDTO,
mà user có field password nhưng mình k muốn response password cho client thì trong DTO mình xóa field này đi là dc,
Client tương tác với server qua các đối tượng dto này, các entity thì để tương tác với jpa, csdl,
1 ví dụ nữa là trong entity user có fiel : private Role role nhưng ta chỉ muốn trả về cho
client là tên của role này thôi thì lúc đó dto sẽ hữu dụng,
 chúng ta có thể thêm các field khác để có thể trả về nhiều thông tin nhất trong 1 request

Dto và entity không giống nhau đâu.
 Controller sử dụng DTO, repository sử dụng entity để giao tiếp với database.
 Các field trong 2 thèn này có trường hợp trùng nhau, có trường hợp khác nhau… ng mới học sẽ bảo như bạn vậy…
 nhưng dùng nó thời gian, bạn sẽ thấy khác.
 */

@Entity
public class User {
    @Id //annotation
    @GeneratedValue(strategy = GenerationType.UUID) // UUID: tạo các id ngẫu nhiên không bị trùng lặp
    private String id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private LocalDate dob;

}
