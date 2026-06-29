package org.example.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class LoginDTO {
    @NotBlank(message = "登录账号不能为空")
    private String loginId;
    
    @NotBlank(message = "密码不能为空")
    private String password;
}