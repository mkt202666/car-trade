package com.pancosky.newcartrade.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 车源标签/特征实体类
 * 描述：用于给车源打"准新车""无事故""原厂质保""低里程"等特性标签，便于搜索与推荐。
 * 关联：外键 carId → car_sources；一车多标签。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("tc_car_tags")
public class CarTag {

    /** 标签记录ID（主键） */
    @TableId
    private Long id;

    /** 所属车源ID（关联 car_sources.id） */
    private Long carId;

    /** 标签类型（FEATURE=车辆特性；SERVICE=服务标签；CERTIFICATION=认证标签；PROMOTION=营销标签） */
    private String tagType;

    /** 标签值（如"准新车""全程4S保养""一手车源"等具体文本） */
    private String tagValue;

    /** 记录创建时间（由 MyBatis-Plus 自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
