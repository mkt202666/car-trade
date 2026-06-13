package com.pancosky.newcartrade.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * AI LLM 客户端 - 支持 OpenAI 兼容 API（火山方舟/DeepSeek/ChatGPT 等）
 */
@Slf4j
@Component
public class AiClient {

    @Value("${ai.api-key:}")
    private String apiKey;

    @Value("${ai.base-url:https://ark.cn-beijing.volces.com/api/v3}")
    private String baseUrl;

    @Value("${ai.model:deepseek-v4-pro}")
    private String model;

    @Value("${ai.enabled:false}")
    private boolean enabled;

    private int timeoutSeconds = 120;

    @Value("${ai.timeout-seconds:120}")
    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
        // 同步更新 RestTemplate 的超时
        if (this.restTemplate != null) {
            int ms = (int) TimeUnit.SECONDS.toMillis(timeoutSeconds);
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(ms);
            factory.setReadTimeout(ms);
            this.restTemplate.setRequestFactory(factory);
        }
    }

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(60))
            .build();

    // ========== 普通请求（一次返回） ==========

    public String chat(String systemPrompt, String userMessage) {
        List<Map<String, String>> messages = new ArrayList<>();
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            messages.add(Map.of("role", "system", "content", systemPrompt));
        }
        messages.add(Map.of("role", "user", "content", userMessage));
        return callLLM(messages);
    }

    public String chat(List<Map<String, String>> messages) {
        return callLLM(messages);
    }

    private String callLLM(List<Map<String, String>> messages) {
        if (!enabled || apiKey == null || apiKey.isEmpty()) {
            log.warn("[AI] 调用失败 - 未启用或缺少 API Key");
            return null;
        }

        try {
            String url = buildUrl();

            Map<String, Object> body = buildRequestBody(messages, false);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(body), headers);

            log.info("[AI] POST {}", url);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode choices = root.get("choices");
                if (choices != null && choices.isArray() && !choices.isEmpty()) {
                    JsonNode messageNode = choices.get(0).get("message");
                    if (messageNode != null) {
                        JsonNode content = messageNode.get("content");
                        if (content != null && !content.isNull()) {
                            return content.asText();
                        }
                    }
                }
            }
            log.warn("[AI] 响应格式异常: status={}, body={}", response.getStatusCode(), response.getBody());
        } catch (Exception e) {
            log.error("[AI] 调用失败: url={}, error={}, msg={}",
                    buildUrl(), e.getClass().getSimpleName(), e.getMessage());
            if (e instanceof org.springframework.web.client.HttpClientErrorException httpEx) {
                log.error("[AI] HTTP错误详情: status={}, body={}", httpEx.getStatusCode(), httpEx.getResponseBodyAsString());
            }
        }
        return null;
    }

    // ========== 流式请求（SSE 输出） ==========

    /**
     * 流式调用 LLM - 合并多个小 token 为较大 chunk 后回调 onNext，减少 SSE 事件数
     * @param messages 对话历史
     * @param onNext 增量内容回调（参数是一批新增的文本，一般 >= 4 字或在延迟到期时触发）
     * @param onComplete 完成回调（参数是最终拼接的完整文本）
     * @param onError 错误回调
     */
    public void chatStream(List<Map<String, String>> messages,
                           Consumer<String> onNext,
                           Consumer<String> onComplete,
                           Consumer<Exception> onError) {
        if (!enabled || apiKey == null || apiKey.isEmpty()) {
            onError.accept(new RuntimeException("AI 服务未启用或缺少 API Key"));
            return;
        }

        try {
            String url = buildUrl();
            Map<String, Object> body = buildRequestBody(messages, true);
            String requestBody = objectMapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .timeout(Duration.ofSeconds(timeoutSeconds))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .build();

            log.info("[AI] 流式请求 POST {}", url);
            final StringBuilder fullText = new StringBuilder();

            // ---- 缓冲合并逻辑：按 字符数 / 最大延迟 双阈值触发 flush ----
            // 目标：把每秒几十个 token 的流式输出打包为每秒几次事件，降低 SSE 开销
            final int MIN_CHUNK_CHARS = 4;              // 累计到 4 字以上触发
            final long MAX_FLUSH_MS = 120;               // 即使没凑齐，120ms 内也至少 flush 一次
            final StringBuilder pending = new StringBuilder();
            final long[] lastFlushMs = { System.currentTimeMillis() };
            final Object lock = new Object();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofLines())
                    .thenApply(HttpResponse::body)
                    .thenAccept(lines -> {
                        try {
                            lines.forEach(line -> {
                                if (line == null) return;
                                String trimmed = line.trim();
                                if (trimmed.isEmpty()) return;
                                if (!trimmed.startsWith("data:")) return;

                                String data = trimmed.substring(5).trim();
                                if (data.equals("[DONE]")) return;

                                try {
                                    JsonNode root = objectMapper.readTree(data);
                                    JsonNode choices = root.get("choices");
                                    if (choices != null && choices.isArray() && !choices.isEmpty()) {
                                        JsonNode delta = choices.get(0).get("delta");
                                        if (delta != null) {
                                            JsonNode content = delta.get("content");
                                            if (content != null && !content.isNull()) {
                                                String piece = content.asText();
                                                if (piece != null && !piece.isEmpty()) {
                                                    synchronized (lock) {
                                                        pending.append(piece);
                                                        long now = System.currentTimeMillis();
                                                        // 满足字符阈值，或超过最大延迟 → 立即 flush
                                                        if (pending.length() >= MIN_CHUNK_CHARS
                                                                || (now - lastFlushMs[0]) >= MAX_FLUSH_MS) {
                                                            String combined = pending.toString();
                                                            pending.setLength(0);
                                                            lastFlushMs[0] = now;
                                                            fullText.append(combined);
                                                            onNext.accept(combined);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    // 跳过解析失败的 chunk，不中断整个流
                                    log.debug("[AI] 解析 chunk 失败: {}", data);
                                }
                            });
                            // 读完流后：把残留未 flush 的内容推一次
                            synchronized (lock) {
                                if (pending.length() > 0) {
                                    String combined = pending.toString();
                                    pending.setLength(0);
                                    fullText.append(combined);
                                    onNext.accept(combined);
                                }
                            }
                            onComplete.accept(fullText.toString());
                        } catch (Exception e) {
                            onError.accept(e);
                        }
                    })
                    .exceptionally(throwable -> {
                        onError.accept(new RuntimeException(throwable.getCause() != null ? throwable.getCause() : throwable));
                        return null;
                    });

        } catch (Exception e) {
            onError.accept(e);
        }
    }

    // ========== 工具方法 ==========

    private String buildUrl() {
        String url = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        return url + "chat/completions";
    }

    private Map<String, Object> buildRequestBody(List<Map<String, String>> messages, boolean stream) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", model);
        body.put("messages", messages);
        body.put("temperature", 0.7);
        body.put("max_tokens", 2048);
        body.put("stream", stream);
        return body;
    }

    /**
     * 返回是否启用 - 用于前端判断是否走模拟回复
     */
    public boolean isEnabled() {
        return enabled && apiKey != null && !apiKey.isEmpty();
    }

    public String getModel() {
        return model;
    }
}
