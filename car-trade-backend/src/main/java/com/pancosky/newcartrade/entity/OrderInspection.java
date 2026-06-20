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
 * 订单验车报告实体类
 * 描述：交易过程中对车辆进行独立第三方验车所生成的报告，用于记录车况、支撑交易决策与纠纷处理。
 * 关联：通过 orderId 关联 orders 表。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("tc_order_inspections")
public class OrderInspection {

    /** 主键ID（全局唯一，用于业务表关联） */
    @TableId
    private Long id;

    /** 订单ID（关联 orders.id，表示本验车报告所属的订单） */
    private String orderId;

    /** 整体车况（如 EXCELLENT / GOOD / FAIR / POOR 等综合评级） */
    private String overallCondition;

    /** 车漆状况（描述漆面是否有划痕、补漆、色差等，以 JSON 或文本形式记录） */
    private String paint;

    /** 车身结构（描述车身结构件是否完整、有无钣金、焊接等） */
    private String structure;

    /** 发动机状况（描述发动机运行状态、有无漏油、异响、烧机油等） */
    private String engine;

    /** 变速箱状况（描述换挡是否顺畅、有无顿挫、漏油等） */
    private String transmission;

    /** 过户次数（车辆历史过户的累计次数，0 表示新车/一手车） */
    private Integer transferCount;

    /** 里程表类型（如 REAL 真实里程 / TAMPERED 疑似调表 / UNKNOWN 未知） */
    private String mileageType;

    /** 综合描述（对验车结果的详细文字说明，包含异常项、建议等） */
    private String description;

    /** 异常照片（以逗号分隔的图片 URL 列表，记录外观/内饰/结构异常） */
    private String abnormalPhotos;

    /** 相关材料（验车过程中产生的报告、文档等材料，以逗号分隔的文件 URL 列表） */
    private String materials;

    /** 创建时间（验车报告录入时间，由系统在插入时自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间（验车报告最近修改时间，由系统在插入与更新时自动填充） */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
