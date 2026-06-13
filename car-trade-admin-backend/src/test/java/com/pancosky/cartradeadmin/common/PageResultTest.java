package com.pancosky.cartradeadmin.common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * PageResult 单元测试
 */
class PageResultTest {

    @Test
    void testPageResultCalculation() {
        PageResult<String> result = new PageResult<>(
                java.util.List.of("a", "b", "c"), 10, 1, 3
        );
        assertEquals(3, result.getList().size());
        assertEquals(10, result.getTotal());
        assertEquals(1, result.getPage());
        assertEquals(3, result.getSize());
        assertEquals(4, result.getTotalPages()); // ceil(10/3) = 4
    }

    @Test
    void testEmptyPage() {
        PageResult<Void> result = new PageResult<>(
                java.util.List.of(), 0, 1, 10
        );
        assertTrue(result.getList().isEmpty());
        assertEquals(0, result.getTotal());
        assertEquals(0, result.getTotalPages());
    }

    @Test
    void testExactFullPage() {
        PageResult<String> result = new PageResult<>(
                java.util.List.of("a", "b", "c", "c", "d"), 5, 1, 5
        );
        assertEquals(5, result.getList().size());
        assertEquals(1, result.getTotalPages()); // ceil(5/5) = 1
    }

    @Test
    void testLastPagePartial() {
        PageResult<String> result = new PageResult<>(
                java.util.List.of("a", "b"), 7, 2, 5
        );
        assertEquals(2, result.getList().size());
        assertEquals(7, result.getTotal());
        assertEquals(2, result.getTotalPages()); // ceil(7/5) = 2
    }
}
