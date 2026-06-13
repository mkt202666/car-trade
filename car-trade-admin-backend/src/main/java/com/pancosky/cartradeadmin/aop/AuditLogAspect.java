package com.pancosky.cartradeadmin.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pancosky.cartradeadmin.annotation.AuditLog;
import com.pancosky.cartradeadmin.mapper.AuditLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * AOP aspect that intercepts methods annotated with @AuditLog
 * and automatically records the operation to the audit_logs table.
 */
@Slf4j
@Aspect
@Component
public class AuditLogAspect {

    @Autowired
    private AuditLogMapper auditLogMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Around("@annotation(auditLog)")
    public Object around(ProceedingJoinPoint joinPoint, AuditLog auditLog) throws Throwable {
        long startTime = System.currentTimeMillis();

        // Extract admin info from request attribute (set by AdminAuthInterceptor)
        Long adminId = null;
        String adminName = "system";
        String ipAddress = "";
        String userAgent = "";

        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            Object idAttr = request.getAttribute("ADMIN_ID");
            Object nameAttr = request.getAttribute("ADMIN_USERNAME");
            if (idAttr != null) {
                adminId = (Long) idAttr;
            }
            if (nameAttr != null) {
                adminName = (String) nameAttr;
            }
            ipAddress = getClientIp(request);
            userAgent = truncate(request.getHeader("User-Agent"), 500);
        }

        // Serialize request parameters
        String requestParams = "";
        if (auditLog.recordParams()) {
            requestParams = serializeParams(joinPoint);
        }

        // Extract target ID from path variable or method argument
        String targetId = extractTargetId(joinPoint);

        String result = "SUCCESS";
        Object returnValue;
        try {
            returnValue = joinPoint.proceed();
            return returnValue;
        } catch (Throwable e) {
            result = "FAILED: " + truncate(e.getMessage(), 200);
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            // Async save to avoid blocking the main thread on error
            try {
                com.pancosky.cartradeadmin.entity.AuditLog logEntity = new com.pancosky.cartradeadmin.entity.AuditLog();
                logEntity.setAdminId(adminId);
                logEntity.setAdminName(adminName);
                logEntity.setModule(auditLog.module());
                logEntity.setAction(auditLog.action());
                logEntity.setTargetType(auditLog.targetType());
                logEntity.setTargetId(targetId);
                logEntity.setDescription(auditLog.action());
                logEntity.setRequestParams(truncate(requestParams, 2000));
                logEntity.setIpAddress(ipAddress);
                logEntity.setUserAgent(userAgent);
                logEntity.setDurationMs((int) duration);
                logEntity.setResult(result);
                logEntity.setCreatedAt(LocalDateTime.now());

                auditLogMapper.insert(logEntity);
            } catch (Exception e) {
                log.error("[AuditLog] Failed to save audit log for module={}, action={}",
                        auditLog.module(), auditLog.action(), e);
            }
        }
    }

    private String serializeParams(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        if (paramNames == null || paramNames.length == 0) {
            return "{}";
        }

        Map<String, Object> params = new HashMap<>();
        for (int i = 0; i < paramNames.length; i++) {
            Object arg = args[i];
            // Skip HttpServletRequest, HttpServletResponse, MultipartFile
            if (arg instanceof HttpServletRequest || arg instanceof jakarta.servlet.http.HttpServletResponse
                    || arg instanceof MultipartFile) {
                params.put(paramNames[i], "[" + arg.getClass().getSimpleName() + "]");
            } else {
                params.put(paramNames[i], arg);
            }
        }

        try {
            return objectMapper.writeValueAsString(params);
        } catch (Exception e) {
            log.warn("[AuditLog] Failed to serialize params", e);
            return "{}";
        }
    }

    private String extractTargetId(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        if (paramNames == null) return "";

        // Look for common ID parameter names
        for (int i = 0; i < paramNames.length; i++) {
            String name = paramNames[i].toLowerCase();
            if (name.equals("id") || name.endsWith("id") && args[i] != null) {
                return String.valueOf(args[i]);
            }
        }
        return "";
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // X-Forwarded-For may contain multiple IPs, take the first one
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private String truncate(String str, int maxLen) {
        if (str == null) return "";
        return str.length() > maxLen ? str.substring(0, maxLen) : str;
    }
}
