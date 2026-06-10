package com.pancosky.newcartrade.dto;

import lombok.Data;

/**
 * 车辆检测报告信息 DTO
 * 描述：作为 CarCreateDTO/CarUpdateDTO 中的嵌套对象，承载车况检测详细信息。
 * 也被 CarInspection 实体对应的前端表单使用。
 */
@Data
public class CarInspectionDTO {

    /** 整体车况评级（如"优秀/良好/一般/较差"） */
    private String overallCondition;

    /** 漆面情况（描述喷漆修复部位、损伤等） */
    private String paint;

    /** 车身结构情况（是否有事故、焊接、变形等） */
    private String structure;

    /** 发动机情况（运行状态、保养情况、故障码等） */
    private String engine;

    /** 变速箱情况（自动/手动、换挡平顺性等） */
    private String transmission;

    /** 过户次数（登记证上的过户次数） */
    private Integer transferCount;

    /** 里程表状态（NORMAL=正常；SUSPECTED_ROLLBACK=疑似调表；UNKNOWN=未知） */
    private String mileageType;
}
