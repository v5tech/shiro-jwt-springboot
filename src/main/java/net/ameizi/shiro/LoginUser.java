package net.ameizi.shiro;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginUser {
    private String username;
    private String password;
    private String vcode;
    String vcodeKey;
}
