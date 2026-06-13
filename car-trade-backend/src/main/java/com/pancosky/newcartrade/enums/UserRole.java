package com.pancosky.newcartrade.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户角色枚举
 * 
 * 用于区分不同类型的用户，替代仅通过 shop_name 判断的方式
 */
@Getter
@AllArgsConstructor
public enum UserRole {
    
    /**
     * 个人用户
     * - 普通C端用户
     * - 可以浏览、发布车源、参与交易
     * - shop_name 为空
     */
    PERSONAL("PERSONAL", "个人用户"),
    
    /**
     * 车行用户（商家）
     * - B端认证商家
     * - 有车行名称和认证状态
     * - 可以享受更多权益（如批量发布、优先展示等）
     */
    SHOP("SHOP", "车行用户"),
    
    /**
     * 系统管理员
     * - 运营端后台管理员
     * - 拥有管理权限
     * - 存储在 admin_users 表中，此值仅用于统一视图展示
     */
    ADMIN("ADMIN", "系统管理员"),
    
    /**
     * 开发人员/测试账号
     * - 内部测试使用
     * - 可能拥有特殊权限或数据隔离
     */
    DEVELOPER("DEVELOPER", "开发人员");
    
    /**
     * 角色代码（存储到数据库的值）
     */
    private final String code;
    
    /**
     * 角色描述（用于前端展示）
     */
    private final String description;
    
    /**
     * 根据代码获取枚举
     */
    public static UserRole fromCode(String code) {
        if (code == null || code.isEmpty()) {
            return PERSONAL; // 默认个人用户
        }
        
        for (UserRole role : values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        
        throw new IllegalArgumentException("Unknown user role: " + code);
    }
    
    /**
     * 判断是否为车行用户
     */
    public boolean isShop() {
        return this == SHOP;
    }
    
    /**
     * 判断是否为个人用户
     */
    public boolean isPersonal() {
        return this == PERSONAL;
    }
    
    /**
     * 判断是否为管理员
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }
}
