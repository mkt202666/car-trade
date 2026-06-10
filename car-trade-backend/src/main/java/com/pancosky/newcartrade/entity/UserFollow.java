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
 * 用户关注/粉丝关联实体类
 * 描述：记录用户之间的关注关系，用于构建粉丝数、动态、关注卖家的车源推送等。
 * 关联：userId/followUserId 均指向 users.id；(userId, followUserId) 唯一。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("user_follows")
public class UserFollow {

    /** 记录ID（主键） */
    @TableId
    private Long id;

    /** 关注者用户ID（发起关注的用户） */
    private Long userId;

    /** 被关注用户ID（被关注的卖家/用户） */
    private Long followUserId;

    /** 关注时间（由 MyBatis-Plus 自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
