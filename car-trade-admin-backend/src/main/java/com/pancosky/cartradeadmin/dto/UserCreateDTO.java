package com.pancosky.cartradeadmin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserCreateDTO {
    @NotBlank(message = "手机号不能为空")
    private String phone;
    private String nickname;
    private String realName;
    private String userRole;  // PERSONAL / SHOP / ADMIN / DEVELOPER
    private String password;  // if null, use default "123456"
    private String shopName;  // only if userRole is SHOP
}
