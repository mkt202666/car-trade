package com.pancosky.cartradeadmin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ShopCreateDTO {

    @NotBlank(message = "车行名称不能为空")
    private String shopName;

    private String shopLogo;

    private String shopDescription;

    @NotBlank(message = "联系电话不能为空")
    private String phone;

    private String contactName;

    private String creditCode;

    private String province;

    private String city;

    private String address;

    private String licenseUrl;

    private String idCardNumber;

    private String idCardImageUrl;

    private String storeImageUrl;
}
