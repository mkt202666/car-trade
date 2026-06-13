package com.pancosky.newcartrade.security;

import com.pancosky.newcartrade.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Owner 断言 — 单测
 */
class OwnerAssertTest {

    @Test
    @DisplayName("assertOwner：当前用户 == owner 通过")
    void assertOwnerPass() {
        assertDoesNotThrow(() -> OwnerAssert.assertOwner(100L, 100L));
    }

    @Test
    @DisplayName("assertOwner：当前用户 != owner 抛 403")
    void assertOwnerFail() {
        BusinessException e = assertThrows(BusinessException.class,
            () -> OwnerAssert.assertOwner(100L, 200L));
        assertEquals(403, e.getCode());
    }

    @Test
    @DisplayName("assertOwner：currentUserId 为 null 抛 401")
    void assertOwnerNullCurrent() {
        assertThrows(BusinessException.class,
            () -> OwnerAssert.assertOwner(null, 100L));
    }

    @Test
    @DisplayName("assertOwnerOrAdmin：管理员通过")
    void assertOwnerOrAdminAdmin() {
        assertDoesNotThrow(() -> OwnerAssert.assertOwnerOrAdmin(1L, 200L, true));
    }

    @Test
    @DisplayName("assertOwnerOrAdmin：非管理员且非 owner 拒绝")
    void assertOwnerOrAdminNotAdmin() {
        assertThrows(BusinessException.class,
            () -> OwnerAssert.assertOwnerOrAdmin(1L, 200L, false));
    }

    @Test
    @DisplayName("assertBuyerOrSeller：买家通过")
    void assertBuyerOrSellerAsBuyer() {
        assertDoesNotThrow(() -> OwnerAssert.assertBuyerOrSeller(100L, 100L, 200L));
    }

    @Test
    @DisplayName("assertBuyerOrSeller：卖家通过")
    void assertBuyerOrSellerAsSeller() {
        assertDoesNotThrow(() -> OwnerAssert.assertBuyerOrSeller(200L, 100L, 200L));
    }

    @Test
    @DisplayName("assertBuyerOrSeller：第三方拒绝")
    void assertBuyerOrSellerThirdParty() {
        BusinessException e = assertThrows(BusinessException.class,
            () -> OwnerAssert.assertBuyerOrSeller(300L, 100L, 200L));
        assertEquals(403, e.getCode());
    }

    @Test
    @DisplayName("assertValidId：null 与 ≤0 拒绝")
    void assertValidId() {
        assertThrows(BusinessException.class, () -> OwnerAssert.assertValidId(null));
        assertThrows(BusinessException.class, () -> OwnerAssert.assertValidId(0L));
        assertThrows(BusinessException.class, () -> OwnerAssert.assertValidId(-1L));
        assertDoesNotThrow(() -> OwnerAssert.assertValidId(1L));
    }
}
