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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("users")
public class User {
    @TableId
    private Long id;
    private String phone;
    private String nickname;
    private String realName;
    private String avatarUrl;
    private String shopName;
    private String creditGrade;
    private Integer creditScore;
    private Integer dealCount;
    private Integer onSaleCount;
    private Long viewCount;
    private Integer viewCountToday;
    private Long messageCount;
    private Integer messageCountToday;
    private Integer followerCount;
    private Integer followerCountToday;
    private LocalDateTime memberExpireAt;
    private String certificationStatus;
    private String status;
    @TableField(exist = false)
    private String description;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
