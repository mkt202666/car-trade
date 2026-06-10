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
 * 车辆检测报告实体类
 * 描述：卖家或平台对车辆进行的第三方/内部车况检测记录，用于展示车况细节。
 * 关联：外键 carId → car_sources；一车可有多份检测报告。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("car_inspections")
public class CarInspection {

    /** 检测报告ID（主键） */
    @TableId
    private Long id;

    /** 所属车源ID（关联 car_sources.id） */
    private Long carId;

    /** 整体车况（如"优秀/良好/一般/较差"等评级） */
    private String overallCondition;

    /** 漆面情况（描述喷漆部位、损伤情况，可 JSON 结构化存储） */
    private String paint;

    /** 结构情况（车身结构是否有焊接、变形、事故等） */
    private String structure;

    /** 发动机情况（发动机运行状态、保养情况、故障码等） */
    private String engine;

    /** 变速箱情况（自动/手动、换挡是否平顺、有无故障等） */
    private String transmission;

    /** 过户次数（登记证上的过户次数；0 表示一手车） */
    private Integer transferCount;

    /** 里程表状态（NORMAL=正常；SUSPECTED_ROLLBACK=疑似调表；UNKNOWN=未知） */
    private String mileageType;

    /** 详细描述（检测人员的综合文字描述） */
    private String description;

    /** 异常部位照片URL（逗号分隔，存储问题部位照片） */
    private String abnormalPhotos;

    /** 记录创建时间（由 MyBatis-Plus 自动填充；即检测提交时间） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 记录最后更新时间（由 MyBatis-Plus 自动填充；便于后期修改/补充） */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
