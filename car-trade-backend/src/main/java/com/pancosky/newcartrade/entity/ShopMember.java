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
 * 车行店铺成员关联实体类
 * 描述：若车行账号支持多名成员协作（店长/销售等），此表记录用户与所属车行店铺之间的成员关系。
 * 关联：shopUserId/memberUserId 均指向 users.id；组成员-车行的多对多。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("tc_shop_members")
public class ShopMember {

    /** 记录ID（主键） */
    @TableId
    private Long id;

    /** 车行用户ID（关联 users.id，代表店铺主账号） */
    private Long shopUserId;

    /** 成员用户ID（关联 users.id，店铺下的协作成员） */
    private Long memberUserId;

    /** 成员角色（OWNER=店长/所有者；SALES=销售；MANAGER=管理员） */
    private String role;

    /** 关系状态（PENDING=待批准；ACTIVE=正常；REMOVED=已移除） */
    private String status;

    /** 申请加入时间（成员发起加入申请的时间） */
    private LocalDateTime appliedAt;

    /** 批准时间（店铺方确认通过的时间） */
    private LocalDateTime approvedAt;

    /** 记录创建时间（由 MyBatis-Plus 自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 记录最后更新时间（由 MyBatis-Plus 自动填充） */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
