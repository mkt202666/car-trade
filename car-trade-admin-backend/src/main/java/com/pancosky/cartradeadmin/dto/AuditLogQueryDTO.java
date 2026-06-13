package com.pancosky.cartradeadmin.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Query parameters for audit log list.
 */
@Data
public class AuditLogQueryDTO {

    private Integer page = 1;

    private Integer size = 20;

    /** Filter by module: auth, user, shop, car, order, etc. */
    private String module;

    /** Filter by admin name (fuzzy match) */
    private String adminName;

    /** Filter by target type */
    private String targetType;

    /** Filter by result: SUCCESS or FAILED */
    private String result;

    /** Filter by start time */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String startTime;

    /** Filter by end time */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String endTime;
}
