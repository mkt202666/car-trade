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
 * 用户收藏车源关联实体类
 * 描述：记录用户收藏的车源，用于用户"我的收藏"页面与推荐系统。
 * 关联：外键 userId → users；carId → car_sources；(userId, carId) 唯一。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("tc_user_favorites")
public class UserFavorite {

    /** 记录ID（主键） */
    @TableId
    private Long id;

    /** 用户ID（关联 users.id，收藏者） */
    private Long userId;

    /** 车源ID（关联 car_sources.id，被收藏的车源） */
    private Long carId;

    /** 记录创建时间（由 MyBatis-Plus 自动填充；即收藏时间） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
