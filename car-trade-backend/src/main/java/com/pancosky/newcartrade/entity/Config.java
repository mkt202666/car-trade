package com.pancosky.newcartrade.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 系统配置文本实体（移动端只读）
 * 描述：用户协议、隐私条款、交易规范、合同模板等运营端维护的配置文本。
 * 关联：configs 表，主键为 VARCHAR key。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("tc_configs")
public class Config {

    /** 配置键（如 trade-rules, user-agreement, privacy-policy, contract-template） */
    @TableId(type = IdType.INPUT)
    private String key;

    /** 配置内容（富文本） */
    private String content;

    /** 最后更新时间 */
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
