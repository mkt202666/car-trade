package com.pancosky.cartradeadmin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ShopMemberRoleUpdateDTO {

    @NotBlank(message = "角色不能为空")
    private String role;
}
