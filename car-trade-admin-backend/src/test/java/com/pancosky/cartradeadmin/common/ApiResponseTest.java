package com.pancosky.cartradeadmin.common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ApiResponse 单元测试
 */
class ApiResponseTest {

    @Test
    void testSuccessWithData() {
        ApiResponse<String> response = ApiResponse.success("hello");
        assertEquals(200, response.getCode());
        assertEquals("success", response.getMessage());
        assertEquals("hello", response.getData());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void testSuccessWithoutData() {
        ApiResponse<Void> response = ApiResponse.success();
        assertEquals(200, response.getCode());
        assertNull(response.getData());
    }

    @Test
    void testError() {
        ApiResponse<Void> response = ApiResponse.error(400, "参数错误");
        assertEquals(400, response.getCode());
        assertEquals("参数错误", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testSuccessWithObject() {
        var obj = new Object() {
            String name = "test";
            int value = 42;
        };
        ApiResponse<Object> response = ApiResponse.success(obj);
        assertEquals(200, response.getCode());
        assertNotNull(response.getData());
    }
}
