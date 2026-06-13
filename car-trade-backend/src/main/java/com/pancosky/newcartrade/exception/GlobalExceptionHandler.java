package com.pancosky.newcartrade.exception;

import com.pancosky.newcartrade.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * 关键点：
 *   - BusinessException：业务异常，message 是设计好的对外文案，原样返回（生产也保留）
 *   - 校验异常 (BindException / MethodArgumentNotValidException)：返回字段错误，prod 模式也保留（不涉及内部）
 *   - 兜底 Exception：可能包含敏感信息（SQL、堆栈、文件路径）
 *       - local/test profile：返回原始 message，便于开发
 *       - prod profile：返回通用文案 "服务器内部错误"，避免泄露
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Environment environment;

    public GlobalExceptionHandler(Environment environment) {
        this.environment = environment;
    }

    /**
     * 是否为生产环境（含 prod profile）。
     * local / test 视为开发环境，原样返回；prod 才屏蔽原始 message。
     */
    private boolean isProdProfile() {
        return Arrays.asList(environment.getActiveProfiles()).contains("prod");
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("业务异常 [{}] {}: {}", request.getRequestURI(), e.getCode(), e.getMessage());
        // 业务异常是设计好的对外文案，原样返回
        return ApiResponse.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数校验失败: {}", message);
        return ApiResponse.error(400, message);
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数绑定失败: {}", message);
        return ApiResponse.error(400, message);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        String rawMessage = e.getMessage() == null ? "参数错误" : e.getMessage();
        String userMessage = isProdProfile() ? "参数错误" : rawMessage;
        log.warn("参数异常 [{}]: {}", request.getRequestURI(), rawMessage);
        return ApiResponse.error(400, userMessage);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleException(Exception e, HttpServletRequest request) {
        String uri = request.getRequestURI();
        String rawMessage = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
        // 完整记录日志（含堆栈），便于运维排查
        log.error("系统异常 [{}] {}: {}", uri, rawMessage, e);
        if (isProdProfile()) {
            // 生产：返回通用文案，不暴露内部错误信息（SQL / 堆栈 / 文件路径等）
            return ApiResponse.error(500, "服务器内部错误");
        }
        // 开发：返回真实异常信息，便于调试
        return ApiResponse.error(500, "服务器异常: " + rawMessage);
    }
}
