package com.pancosky.newcartrade.security;

import com.pancosky.newcartrade.exception.BusinessException;

/**
 * ============================================
 *  Owner 断言（防 IDOR / 横向越权）
 * ============================================
 *
 * 规则：
 *   - 操作资源时，必须验证当前登录用户是 owner 或者 admin
 *   - 失败抛 BusinessException → 由 GlobalExceptionHandler 统一返回 403
 *   - 数值型 id 必须 &gt; 0（防空指针与非法 id）
 *   - 涉及"买家或卖家"双角色时使用 assertBuyerOrSeller
 * ============================================
 */
public final class OwnerAssert {

    private OwnerAssert() { }

    /** 要求当前用户 == ownerUserId，否则 403 */
    public static void assertOwner(Long currentUserId, Long ownerUserId) {
        if (currentUserId == null) {
            throw new BusinessException(401, "未登录");
        }
        if (ownerUserId == null) {
            throw new BusinessException(403, "资源所有者不存在");
        }
        if (!currentUserId.equals(ownerUserId)) {
            throw new BusinessException(403, "无权操作他人资源");
        }
    }

    /** 要求当前用户是 owner 或 admin（admin 角色判定走 isAdmin） */
    public static void assertOwnerOrAdmin(Long currentUserId, Long ownerUserId, boolean isAdmin) {
        if (isAdmin) return;
        assertOwner(currentUserId, ownerUserId);
    }

    /** 要求当前用户是 buyer 或 seller（订单类场景） */
    public static void assertBuyerOrSeller(Long currentUserId, Long buyerId, Long sellerId) {
        if (currentUserId == null) {
            throw new BusinessException(401, "未登录");
        }
        if (buyerId == null && sellerId == null) {
            throw new BusinessException(403, "订单双方均不存在");
        }
        if (currentUserId.equals(buyerId) || currentUserId.equals(sellerId)) {
            return;
        }
        throw new BusinessException(403, "无权操作他人订单");
    }

    /** 校验资源 id 合法（> 0） */
    public static void assertValidId(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(400, "资源 id 不合法");
        }
    }
}
