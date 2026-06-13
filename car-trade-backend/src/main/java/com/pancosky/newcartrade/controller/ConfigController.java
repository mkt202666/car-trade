package com.pancosky.newcartrade.controller;

import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.common.AuthLevel;
import com.pancosky.newcartrade.common.RequiresAuth;
import com.pancosky.newcartrade.entity.Config;
import com.pancosky.newcartrade.mapper.ConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 系统配置文本控制器（移动端只读）
 * 描述：提供用户协议、隐私条款、交易规范、合同模板等配置文本的查询接口
 * 基础路径：/api/v1/configs
 * 认证要求：公开（未登录可访问）
 */
@RestController
@RequestMapping("/api/v1/configs")
@RequiredArgsConstructor
@RequiresAuth(AuthLevel.PUBLIC)
public class ConfigController {

    private final ConfigMapper configMapper;

    /**
     * 获取指定配置文本
     * GET /api/v1/configs/{key}
     * 支持的 key：trade-rules, user-agreement, privacy-policy, contract-template
     */
    @GetMapping("/{key}")
    public ApiResponse<Config> getConfig(@PathVariable String key) {
        Config config = configMapper.selectById(key);
        return ApiResponse.success(config);
    }
}
