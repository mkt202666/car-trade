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

    private int timeoutSeconds = 30;

    @Value("${ai.timeout-seconds:30}")
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
     * 流式调用 LLM - 每收到一个 chunk 调用 onNext 回调
     * @param messages 对话历史
     * @param onNext 增量内容回调（每次收到新内容会被调用，参数是这一批新增的文本）
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
            StringBuilder fullText = new StringBuilder();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofLines())
                    .thenApply(HttpResponse::body)
                    .thenAccept(lines -> {
                        try {
                            lines.forEach(line -> {
                                // SSE 格式: "data: {...json...}"
                                // 每一行可能是一条消息，以 "data:" 开头
                                // "[DONE]" 表示结束
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
                                                    fullText.append(piece);
                                                    onNext.accept(piece);
                                                }
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    // 跳过解析失败的 chunk，不中断整个流
                                    log.debug("[AI] 解析 chunk 失败: {}", data);
                                }
                            });
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
