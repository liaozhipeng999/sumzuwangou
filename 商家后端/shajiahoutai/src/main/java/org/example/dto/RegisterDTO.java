package org.example.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class RegisterDTO {
    @NotBlank(message = "店铺名称不能为空")
    private String merchantName;
    
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    private String password;
    
    @NotBlank(message = "店主姓名不能为空")
    private String contactName;
    
    @NotBlank(message = "联系电话不能为空")
    private String contactPhone;
    
    private String email;
    
    private String merchantLogo;
    
    private String merchantBrief;
    
    private Long mainCategoryId;
}