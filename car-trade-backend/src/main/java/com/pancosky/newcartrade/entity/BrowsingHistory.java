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
 * 浏览历史实体类
 * 描述：记录用户浏览过的车源信息，用于个人中心"浏览记录"模块、个性化推荐等。
 * 关联：通过 userId 关联 users 表，通过 carId 关联 car_sources 表。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("browsing_history")
public class BrowsingHistory {

    /** 主键ID（全局唯一，用于业务表关联） */
    @TableId
    private Long id;

    /** 用户ID（关联 users.id，表示执行浏览操作的用户） */
    private Long userId;

    /** 车源ID（关联 car_sources.id，表示被浏览的车源） */
    private Long carId;

    /** 创建时间（浏览时间，由系统在插入时自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
