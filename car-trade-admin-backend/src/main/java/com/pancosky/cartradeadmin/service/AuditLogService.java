package com.pancosky.cartradeadmin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pancosky.cartradeadmin.common.PageResult;
import com.pancosky.cartradeadmin.dto.AuditLogQueryDTO;
import com.pancosky.cartradeadmin.entity.AuditLog;
import com.pancosky.cartradeadmin.mapper.AuditLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Audit log query service.
 */
@Service
public class AuditLogService {

    @Autowired
    private AuditLogMapper auditLogMapper;

    /**
     * Paginated query of audit logs.
     */
    public PageResult<AuditLog> queryLogs(AuditLogQueryDTO query) {
        Page<AuditLog> page = new Page<>(query.getPage(), query.getSize());
        LambdaQueryWrapper<AuditLog> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(query.getModule())) {
            wrapper.eq(AuditLog::getModule, query.getModule());
        }
        if (StringUtils.hasText(query.getAdminName())) {
            wrapper.like(AuditLog::getAdminName, query.getAdminName());
        }
        if (StringUtils.hasText(query.getTargetType())) {
            wrapper.eq(AuditLog::getTargetType, query.getTargetType());
        }
        if (StringUtils.hasText(query.getResult())) {
            wrapper.like(AuditLog::getResult, query.getResult());
        }
        if (StringUtils.hasText(query.getStartTime())) {
            wrapper.ge(AuditLog::getCreatedAt, query.getStartTime());
        }
        if (StringUtils.hasText(query.getEndTime())) {
            wrapper.le(AuditLog::getCreatedAt, query.getEndTime());
        }

        wrapper.orderByDesc(AuditLog::getCreatedAt);

        Page<AuditLog> result = auditLogMapper.selectPage(page, wrapper);

        return new PageResult<>(result.getRecords(), result.getTotal(),
                query.getPage(), query.getSize());
    }
}
