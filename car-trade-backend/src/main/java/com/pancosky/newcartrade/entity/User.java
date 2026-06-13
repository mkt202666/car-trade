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
 * 用户实体类
 * 描述：系统用户（买家/卖家/车行）信息，包含个人资料、信用等级、统计数据等。
 * 关联：通过 userId 关联 orders, car_sources, messages 等表。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("users")
public class User {

    /** 主键ID（全局唯一，用于所有业务表关联） */
    @TableId
    private Long id;

    /** 手机号（用户登录账号，全表唯一，用于登录、验证码、消息通知） */
    private String phone;

    /** 登录密码（加密存储，实际生产环境使用 BCrypt/SHA-256 等哈希算法） */
    private String password;

    /** 用户昵称（展示名称，用于订单、消息等场景） */
    private String nickname;

    /** 真实姓名（实名认证后填充，用于合同签署） */
    private String realName;

    /** 头像URL（用户头像图片地址） */
    private String avatarUrl;

    /** 车行名称（若用户为车行/商家账号，展示其店铺名称） */
    private String shopName;

    /** 车行Logo地址（车行品牌Logo图片URL） */
    private String shopLogo;

    /** 车行简介（车行的文字介绍，用于卖家主页展示） */
    private String shopDescription;

    /** 信用等级（如 S / A / B / C 等，由信用分动态计算得到） */
    private String creditGrade;

    /** 信用分数（0~1000 分，系统根据成交/违约等行为累计） */
    private Integer creditScore;

    /** 累计成交次数（作为卖家成功完成交易的订单数） */
    private Integer dealCount;

    /** 在售车源数量（当前发布中且状态有效的车源统计） */
    private Integer onSaleCount;

    /** 累计被浏览次数（用户主页被其他用户访问的总数） */
    private Long viewCount;

    /** 今日浏览次数（用于展示用户活跃度，支持数据看板） */
    private Integer viewCountToday;

    /** 累计消息数（系统给用户发送的消息/通知总数） */
    private Long messageCount;

    /** 今日消息数（用户当日收到的消息统计） */
    private Integer messageCountToday;

    /** 粉丝数（关注该用户的用户数） */
    private Integer followerCount;

    /** 今日新增粉丝数（用于首页数据展示） */
    private Integer followerCountToday;

    /** 会员到期时间（会员身份的有效截止日期） */
    private LocalDateTime memberExpireAt;

    /** 用户角色（PERSONAL-个人用户 / SHOP-车行用户 / ADMIN-系统管理员 / DEVELOPER-开发人员） */
    private String userRole;

    /** 实名认证状态（NONE-未认证 / PENDING-审核中 / CERTIFIED-已认证 / REJECTED-已驳回） */
    private String certificationStatus;

    /** 账号状态（ACTIVE-正常 / FROZEN-冻结 / DELETED-已注销） */
    private String status;

    /** 连续登录失败次数（达到上限后锁定账户，默认 5 次） */
    private Integer loginFailCount;

    /** 账户锁定到期时间（NULL=未锁定；已锁定则在此时间前禁止登录） */
    private LocalDateTime lockedUntil;

    /** 通知订阅设置（JSON格式：{"system":true,"auto_promotion":true,"order":true,"contract":true,"deposit":true,"shop":true}） */
    @TableField(typeHandler = com.pancosky.newcartrade.handler.JsonbStringTypeHandler.class)
    private String notificationSettings;

    /** 非持久化字段：用户简介/简介（用于详情页展示，不入库） */
    @TableField(exist = false)
    private String description;

    /** 记录创建时间（由 MyBatis-Plus 自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 记录最后更新时间（由 MyBatis-Plus 自动填充） */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /** 软删除时间（标记逻辑删除，非空表示已删除） */
    private LocalDateTime deletedAt;
}
