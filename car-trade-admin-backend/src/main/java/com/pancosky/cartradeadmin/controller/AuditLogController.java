package com.pancosky.cartradeadmin.controller;

import com.pancosky.cartradeadmin.annotation.RequiresAdmin;
import com.pancosky.cartradeadmin.common.ApiResponse;
import com.pancosky.cartradeadmin.common.PageResult;
import com.pancosky.cartradeadmin.dto.AuditLogQueryDTO;
import com.pancosky.cartradeadmin.entity.AuditLog;
import com.pancosky.cartradeadmin.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Audit log query endpoints.
 * Only accessible to admin users with login.
 */
@RestController
@RequestMapping("${api.prefix}/audit-logs")
public class AuditLogController {

    @Autowired
    private AuditLogService auditLogService;

    /**
     * Paginated audit log list with filters.
     */
    @RequiresAdmin
    @GetMapping
    public ApiResponse<PageResult<AuditLog>> listLogs(@ModelAttribute AuditLogQueryDTO query) {
        return ApiResponse.success(auditLogService.queryLogs(query));
    }
}
