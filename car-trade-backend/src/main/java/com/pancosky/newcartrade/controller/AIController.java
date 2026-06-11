package com.pancosky.newcartrade.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.service.AIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * AI 智能服务控制器
 * 描述：提供 AI 对话、智能找车、行情分析、获客文案生成、自动外联、车源分配、车源分析、估价助手等 AI 能力接口。
 * 基础路径：/api/v1/ai
 * 认证要求：全部接口必须登录。
 * 注意：所有接口参数均以 Map&lt;String,Object&gt; 承载，便于灵活扩展；业务层按约定字段解析。
 */
@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@Slf4j
public class AIController {

    private final AIService aiService;

    // 使用 ObjectMapper 构造合法的 JSON（避免手工拼接 JSON 出现转义错误）
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 活跃的 SSE 连接（用于可追踪和优雅关闭）
    private final Map<String, SseEmitter> activeEmitters = new ConcurrentHashMap<>();

    /**
     * AI 对话（一次性返回）
     * HTTP: POST /api/v1/ai/chat
     */
    @PostMapping("/chat")
    public ApiResponse<Map<String, Object>> chat(@RequestBody Map<String, Object> params) {
        return ApiResponse.success(aiService.chat(params));
    }

    /**
     * AI 对话（流式输出 - SSE）
     * HTTP: POST /api/v1/ai/chat/stream
     * Content-Type: text/event-stream
     * 事件格式：
     *   event: message
     *   data: {"content": "字"}
     *
     *   event: done
     *   data: {"content": "完整内容"}
     *
     *   event: error
     *   data: {"message": "错误信息"}
     */
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatStream(@RequestBody Map<String, Object> params) {
        // 创建 SSE 连接，超时 2 分钟
        SseEmitter emitter = new SseEmitter(TimeUnit.MINUTES.toMillis(2));
        String emitterId = "emitter-" + System.currentTimeMillis() + "-" + Thread.currentThread().getId();

        emitter.onCompletion(() -> {
            activeEmitters.remove(emitterId);
            log.debug("[AI-SSE] {} 连接完成", emitterId);
        });
        emitter.onTimeout(() -> {
            activeEmitters.remove(emitterId);
            log.warn("[AI-SSE] {} 连接超时", emitterId);
        });
        emitter.onError(e -> {
            activeEmitters.remove(emitterId);
            log.warn("[AI-SSE] {} 连接异常: {}", emitterId, e.getMessage());
        });

        activeEmitters.put(emitterId, emitter);
        log.info("[AI-SSE] {} 建立流式连接", emitterId);

        aiService.chatStream(params,
                piece -> sendMessage(emitter, emitterId, "message", Map.of("content", piece)),
                fullText -> {
                    sendMessage(emitter, emitterId, "done", Map.of("content", fullText));
                    safeComplete(emitter, emitterId);
                },
                error -> {
                    sendMessage(emitter, emitterId, "error", Map.of("message", error.getMessage() != null ? error.getMessage() : "AI 服务异常"));
                    safeComplete(emitter, emitterId);
                });

        return emitter;
    }

    /**
     * 发送一条 SSE 事件，使用 Jackson 构造合法 JSON，避免特殊字符转义错误
     */
    private void sendMessage(SseEmitter emitter, String emitterId, String eventName, Map<String, Object> payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            emitter.send(SseEmitter.event().name(eventName).data(json));
        } catch (IOException e) {
            log.warn("[AI-SSE] {} 发送 {} 事件失败: {}", emitterId, eventName, e.getMessage());
        } catch (Exception e) {
            log.warn("[AI-SSE] {} 发送 {} 事件异常: {}", emitterId, eventName, e.getMessage());
        }
    }

    /**
     * 优雅地关闭 emitter（吞掉已关闭的异常）
     */
    private void safeComplete(SseEmitter emitter, String emitterId) {
        try {
            emitter.complete();
        } catch (Exception e) {
            log.debug("[AI-SSE] {} complete 失败: {}", emitterId, e.getMessage());
        }
    }

    /**
     * 智能找车
     * HTTP: POST /api/v1/ai/search
     */
    @PostMapping("/search")
    public ApiResponse<Map<String, Object>> search(@RequestBody Map<String, Object> params) {
        return ApiResponse.success(aiService.search(params));
    }

    /**
     * 行情分析
     * HTTP: POST /api/v1/ai/market-analysis
     */
    @PostMapping("/market-analysis")
    public ApiResponse<Map<String, Object>> marketAnalysis(@RequestBody Map<String, Object> params) {
        return ApiResponse.success(aiService.marketAnalysis(params));
    }

    /**
     * 获客文案生成
     * HTTP: POST /api/v1/ai/customer-generation
     */
    @PostMapping("/customer-generation")
    public ApiResponse<Map<String, Object>> generateCopywriting(@RequestBody Map<String, Object> params) {
        return ApiResponse.success(aiService.generateCopywriting(params));
    }

    /**
     * 自动外联
     * HTTP: POST /api/v1/ai/auto-outreach
     */
    @PostMapping("/auto-outreach")
    public ApiResponse<Map<String, Object>> autoOutreach(@RequestBody Map<String, Object> params) {
        return ApiResponse.success(aiService.autoOutreach(params));
    }

    /**
     * 车源分配
     * HTTP: POST /api/v1/ai/distribute
     */
    @PostMapping("/distribute")
    public ApiResponse<Map<String, Object>> distributeCar(@RequestBody Map<String, Object> params) {
        return ApiResponse.success(aiService.distributeCar(params));
    }

    /**
     * 车源分析
     * HTTP: POST /api/v1/ai/car-analysis
     */
    @PostMapping("/car-analysis")
    public ApiResponse<Map<String, Object>> carAnalysis(@RequestBody Map<String, Object> params) {
        return ApiResponse.success(aiService.carAnalysis(params));
    }

    /**
     * 估价助手
     * HTTP: POST /api/v1/ai/price-estimate
     */
    @PostMapping("/price-estimate")
    public ApiResponse<Map<String, Object>> priceEstimate(@RequestBody Map<String, Object> params) {
        return ApiResponse.success(aiService.priceEstimate(params));
    }
}
