package com.pancosky.cartradeadmin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ShopMemberAddDTO {

    @NotBlank(message = "手机号不能为空")
    private String phone;

    private String nickname;

    private String role;
}
