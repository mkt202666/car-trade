package com.pancosky.cartradeadmin.dto;

import lombok.Data;

/**
 * 用户查询 DTO
 * 
 * 支持的用户类型（对应原型截图中的筛选条件）：
 * - ALL: 全部业务角色
 * - SHOP_USER: 车行用户（shop_name 非空）
 * - PERSONAL_USER: 个人用户（shop_name 为空）
 * - ADMIN_USER: 系统管理员（来自 admin_users 表）
 * - DELETED: 已注销（deleted_at 非空）
 */
@Data
public class UserQueryDTO {
    /** 搜索关键字（用户名/昵称/手机号） */
    private String keyword;
    
    /** 
     * 用户类型筛选
     * 可选值: ALL, SHOP_USER, PERSONAL_USER, ADMIN_USER, DELETED
     */
    private String userType;
    
    /** 账号状态（ACTIVE/FROZEN/DELETED） */
    private String status;
    
    /** 认证状态（NONE/PENDING/CERTIFIED/REJECTED） */
    private String certificationStatus;
    
    /** 页码 */
    private int page = 1;
    
    /** 每页大小 */
    private int size = 20;
}
