package com.pancosky.cartradeadmin.vo;

import com.pancosky.cartradeadmin.entity.AppCarImage;
import com.pancosky.cartradeadmin.entity.AppCarInspection;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class CarDetailVO extends CarVO {
    private String sellerAvatarUrl;
    private List<AppCarImage> images;
    private AppCarInspection inspection;
    private String description;
}
