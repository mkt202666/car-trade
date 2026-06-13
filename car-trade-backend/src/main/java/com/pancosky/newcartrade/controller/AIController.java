package com.pancosky.newcartrade.controller;
import com.pancosky.newcartrade.common.RequiresAuth;
import com.pancosky.newcartrade.common.AuthLevel;

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
@RequiresAuth(AuthLevel.PROTECTED)
public class AIController {

    private final AIService aiService;

    // 活跃的 SSE 连接（用于可追踪和优雅关闭）
    private final Map<String, SseEmitter> activeEmitters = new ConcurrentHashMap<>();

    // SSE 超时：5 分钟，覆盖长回复 / 慢网络场景
    private static final long SSE_TIMEOUT_MS = 5L * 60 * 1000;

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
     *   event: message  data: {"content":"多字文本chunk"}
     *   event: done     data: {"content":"完整内容"}
     *   event: error    data: {"message":"错误信息"}
     */
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatStream(@RequestBody Map<String, Object> params) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);
        final String emitterId = "emitter-" + System.currentTimeMillis() + "-" + Thread.currentThread().getId();

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
                    String msg = (error != null && error.getMessage() != null) ? error.getMessage() : "AI 服务异常";
                    sendMessage(emitter, emitterId, "error", Map.of("message", msg));
                    safeComplete(emitter, emitterId);
                });

        return emitter;
    }

    /**
     * 发送一条 SSE 事件。
     * 关键优化：直接把 Map 作为 data 传给 Spring，让其用内部 Jackson 做一次性序列化写出，
     * 避免我们手动 writeValueAsString 再包一层字符串所带来的额外编码开销。
     */
    private void sendMessage(SseEmitter emitter, String emitterId, String eventName, Map<String, Object> payload) {
        try {
            emitter.send(SseEmitter.event().name(eventName).data(payload, MediaType.APPLICATION_JSON));
        } catch (Exception e) {
            log.debug("[AI-SSE] {} 发送 {} 事件失败/连接已关闭: {}", emitterId, eventName, e.getMessage());
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
     * 车源文案生成（兼容前端调用）
     * HTTP: POST /api/v1/ai/copywriting
     */
    @PostMapping("/copywriting")
    public ApiResponse<Map<String, Object>> copywriting(@RequestBody Map<String, Object> params) {
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

    /**
     * 竞品分析
     * HTTP: GET /api/v1/ai/competitors
     */
    @GetMapping("/competitors")
    public ApiResponse<Map<String, Object>> getCompetitors(@RequestParam Map<String, String> params) {
        return ApiResponse.success(aiService.chat(Map.of("action", "competitor_analysis", "params", params)));
    }

    /**
     * 经营建议
     * HTTP: GET /api/v1/ai/suggestions
     */
    @GetMapping("/suggestions")
    public ApiResponse<Map<String, Object>> getSuggestions(@RequestParam Map<String, String> params) {
        return ApiResponse.success(aiService.chat(Map.of("action", "business_suggestions", "params", params)));
    }
}
