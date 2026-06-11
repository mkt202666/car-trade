package com.pancosky.newcartrade.service.impl;

import com.pancosky.newcartrade.service.AIService;
import com.pancosky.newcartrade.util.AiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Consumer;

@Service
@Slf4j
@RequiredArgsConstructor
public class AIServiceImpl implements AIService {

    private final AiClient aiClient;
    private static final String SYSTEM_PROMPT = "你是5D好车的AI助理，专注于二手车交易领域。你可以帮助用户：找车、行情分析、估价、获客文案生成等。回复要专业、简洁、有帮助。";

    // ==================== 消息构建（统一处理 history 的 role 规范化） ====================

    private List<Map<String, String>> buildMessages(String userMessage, List<Map<String, String>> history) {
        return buildMessages(SYSTEM_PROMPT, userMessage, history);
    }

    private List<Map<String, String>> buildMessages(String systemPrompt, String userMessage, List<Map<String, String>> history) {
        List<Map<String, String>> messages = new ArrayList<>();
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            messages.add(Map.of("role", "system", "content", systemPrompt));
        }
        if (history != null) {
            for (Map<String, String> msg : history) {
                String role = msg.get("role");
                String content = msg.get("content");
                if (role == null || content == null) continue;
                // role 规范化：ai/assistant -> assistant, 其他非法角色 -> user
                String normalizedRole = switch (role) {
                    case "ai", "assistant" -> "assistant";
                    case "user", "human" -> "user";
                    case "system" -> "system";
                    default -> "user";
                };
                messages.add(Map.of("role", normalizedRole, "content", content));
            }
        }
        if (userMessage != null && !userMessage.isEmpty()) {
            messages.add(Map.of("role", "user", "content", userMessage));
        }
        return messages;
    }

    // ==================== chat - 一次性返回 ====================

    @Override
    public Map<String, Object> chat(Map<String, Object> params) {
        String message = (String) params.get("message");
        List<Map<String, String>> history = (List<Map<String, String>>) params.get("history");

        List<Map<String, String>> messages = buildMessages(message, history);
        String reply = aiClient.chat(messages);
        if (reply == null) {
            reply = generateSmartReply(message);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("content", reply);
        result.put("type", "text");
        return result;
    }

    // ==================== chatStream - 流式输出（SSE） ====================

    @Override
    public void chatStream(Map<String, Object> params,
                           Consumer<String> onNext,
                           Consumer<String> onComplete,
                           Consumer<Exception> onError) {
        String message = (String) params.get("message");
        List<Map<String, String>> history = (List<Map<String, String>>) params.get("history");

        List<Map<String, String>> messages = buildMessages(message, history);

        // 如果 AI 未启用，返回模拟的"流式"回复（一个字一个字打出来）
        if (!aiClient.isEnabled()) {
            final String fallbackText = generateSmartReply(message);
            new Thread(() -> {
                try {
                    // 模拟打字机效果：每个字间隔 30ms
                    for (int i = 0; i < fallbackText.length(); i++) {
                        onNext.accept(String.valueOf(fallbackText.charAt(i)));
                        Thread.sleep(30);
                    }
                    onComplete.accept(fallbackText);
                } catch (InterruptedException e) {
                    onError.accept(e);
                }
            }, "ai-stream-fallback").start();
            return;
        }

        // 真正调用大模型流式接口
        aiClient.chatStream(messages,
                onNext,
                onComplete,
                e -> {
                    log.error("[AI] 流式调用失败: {}", e.getMessage());
                    // 失败时 fallback：把 mock 内容流式输出给用户
                    String fallbackText = generateSmartReply(message);
                    new Thread(() -> {
                        try {
                            for (int i = 0; i < fallbackText.length(); i++) {
                                onNext.accept(String.valueOf(fallbackText.charAt(i)));
                                Thread.sleep(30);
                            }
                            onComplete.accept(fallbackText);
                        } catch (InterruptedException ie) {
                            // ignore
                        }
                    }, "ai-stream-error-fallback").start();
                });
    }

    // ==================== 其他 AI 能力（均支持多轮会话：message + history） ====================

    private static final String SYSTEM_SEARCH = "你是一名有10年经验的二手车购车顾问。请基于用户的全部对话上下文，理解其真实购车需求（预算、车型、品牌、年份、用途等），给出精准、可落地的车型推荐。如果用户信息不完整，请明确询问缺少的要素。回复结构：1)需求分析 2)车型推荐（3-5款，含价格区间、优缺点、推荐理由）3)购买建议。";
    private static final String SYSTEM_MARKET = "你是一名二手车市场分析师。请基于对话上下文理解用户关注的品牌/车型/细分市场，用数据说话。回复结构：1)市场概览 2)热门车型及参考价格 3)近期价格走势 4)购买/出手时机建议。";
    private static final String SYSTEM_COPY = "你是一名资深二手车营销文案策划。请基于用户提供的车辆信息和目标平台，创作有感染力的推广文案。回复结构：3个不同风格的文案方案，每个方案含标题+正文，适合直接复制发布。";
    private static final String SYSTEM_OUTREACH = "你是一名B端二手车销售顾问。请基于对话上下文理解目标客户画像和车辆信息，制定分阶段的智能外联话术。回复结构：1)首次接触话术 2)跟进培育话术 3)临门促成话术。";
    private static final String SYSTEM_ANALYZE = "你是一名资深二手车评估师。请基于对话上下文理解目标车辆信息（品牌、车型、年份、里程、车况等），给出专业的价值分析。回复结构：1)综合评分 2)优势亮点 3)风险/注意点 4)市场同级别对比 5)销售建议 6)估价区间。";
    private static final String SYSTEM_PRICE = "你是一名二手车估价师。请基于对话上下文中的车辆信息（品牌、车型、年份、里程、配置、车况），给出专业的价格评估。如果关键信息缺失，请明确询问。回复结构：1)参考成交价区间 2)建议零售价 3)车商收购价 4)价格影响因素分析 5)同级别车型参考。";
    private static final String SYSTEM_DISTRIBUTE = "你是一名二手车运营专家。请基于车源信息制定智能分发策略。回复结构：1)推荐投放渠道 2)目标客户画像 3)定价建议 4)推广优先级。";

    @Override
    public Map<String, Object> search(Map<String, Object> params) {
        String message = (String) params.getOrDefault("message", "");
        @SuppressWarnings("unchecked")
        List<Map<String, String>> history = (List<Map<String, String>>) params.getOrDefault("history", null);

        List<Map<String, String>> messages = buildMessages(SYSTEM_SEARCH, message, history);
        String reply = aiClient.chat(messages);
        if (reply == null) {
            reply = buildFallbackSearch("", "", "");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("content", reply);
        result.put("cars", new ArrayList<>());
        result.put("type", "search");
        return result;
    }

    @Override
    public Map<String, Object> marketAnalysis(Map<String, Object> params) {
        String message = (String) params.getOrDefault("message", "");
        @SuppressWarnings("unchecked")
        List<Map<String, String>> history = (List<Map<String, String>>) params.getOrDefault("history", null);

        List<Map<String, String>> messages = buildMessages(SYSTEM_MARKET, message, history);
        String reply = aiClient.chat(messages);
        if (reply == null) {
            reply = buildFallbackMarketAnalysis("");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("content", reply);
        result.put("type", "market_analysis");
        return result;
    }

    @Override
    public Map<String, Object> generateCopywriting(Map<String, Object> params) {
        String message = (String) params.getOrDefault("message", "");
        @SuppressWarnings("unchecked")
        List<Map<String, String>> history = (List<Map<String, String>>) params.getOrDefault("history", null);

        List<Map<String, String>> messages = buildMessages(SYSTEM_COPY, message, history);
        String reply = aiClient.chat(messages);
        if (reply == null) {
            reply = buildFallbackCopywriting("");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("content", reply);
        result.put("type", "copywriting");
        return result;
    }

    @Override
    public Map<String, Object> autoOutreach(Map<String, Object> params) {
        String message = (String) params.getOrDefault("message", "");
        @SuppressWarnings("unchecked")
        List<Map<String, String>> history = (List<Map<String, String>>) params.getOrDefault("history", null);

        List<Map<String, String>> messages = buildMessages(SYSTEM_OUTREACH, message, history);
        String reply = aiClient.chat(messages);
        if (reply == null) {
            reply = buildFallbackOutreach();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("content", reply);
        result.put("type", "outreach");
        return result;
    }

    @Override
    public Map<String, Object> distributeCar(Map<String, Object> params) {
        String message = (String) params.getOrDefault("message", "");
        @SuppressWarnings("unchecked")
        List<Map<String, String>> history = (List<Map<String, String>>) params.getOrDefault("history", null);

        List<Map<String, String>> messages = buildMessages(SYSTEM_DISTRIBUTE, message, history);
        String reply = aiClient.chat(messages);
        if (reply == null) {
            reply = buildFallbackDistribute("");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("content", reply);
        result.put("type", "distribute");
        return result;
    }

    @Override
    public Map<String, Object> carAnalysis(Map<String, Object> params) {
        String message = (String) params.getOrDefault("message", "");
        @SuppressWarnings("unchecked")
        List<Map<String, String>> history = (List<Map<String, String>>) params.getOrDefault("history", null);

        List<Map<String, String>> messages = buildMessages(SYSTEM_ANALYZE, message, history);
        String reply = aiClient.chat(messages);
        if (reply == null) {
            reply = buildFallbackCarAnalysis("", "", "", "");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("content", reply);
        result.put("type", "car_analysis");
        return result;
    }

    @Override
    public Map<String, Object> priceEstimate(Map<String, Object> params) {
        String message = (String) params.getOrDefault("message", "");
        @SuppressWarnings("unchecked")
        List<Map<String, String>> history = (List<Map<String, String>>) params.getOrDefault("history", null);

        List<Map<String, String>> messages = buildMessages(SYSTEM_PRICE, message, history);
        String reply = aiClient.chat(messages);
        if (reply == null) {
            reply = buildFallbackPriceEstimate("", "", "");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("content", reply);
        result.put("type", "price_estimate");
        return result;
    }

    // ==================== Fallback 方法（AI 不可用时的兜底回复） ====================

    private String generateSmartReply(String message) {
        if (message == null || message.isEmpty()) return "您好，有什么可以帮您的？";
        String lower = message.toLowerCase();
        if (lower.contains("宝马") || lower.contains("x5")) {
            return "宝马X5是热门中大型SUV，行情价55-70万。具体取决于车龄、里程和车况。如有购车需求，我可以为您做详细分析。";
        }
        if (lower.contains("奔驰") || lower.contains("e级")) {
            return "奔驰E级是商务首选，行情价40-55万。建议关注准新车源，性价比更高。";
        }
        if (lower.contains("新能源") || lower.contains("特斯拉")) {
            return "新能源二手车热度很高！特斯拉Model Y 22-32万，比亚迪汉EV 15-25万。重点关注电池健康和续航。";
        }
        return "感谢提问！我是5D好车AI助理，可以帮您：\n• 智能找车\n• 行情分析\n• 获客文案\n• 车辆估价\n\n请告诉我您的需求！";
    }

    private String buildFallbackSearch(String budget, String carType, String brand) {
        return String.format("根据您的需求（预算：%s，车型：%s，品牌：%s），推荐以下车型：\n" +
                "1. 宝马X5 - 约65万，豪华品牌\n2. 大众途昂 - 约35万，性价比高\n3. 本田CR-V - 约18万，经济实用\n" +
                "如需更精准推荐，请提供更多需求细节。", budget, carType, brand);
    }

    private String buildFallbackMarketAnalysis(String brand) {
        return "📊 二手车市场行情分析\n\n" +
                "【市场概览】市场整体稳中有升，新能源保值率持续提升。\n" +
                "【热门车型】宝马X5 55-70万，奔驰E级 40-55万，特斯拉Model Y 22-30万。\n" +
                "【建议】季度末购车优惠更大，重点关注车况和手续。";
    }

    private String buildFallbackCopywriting(String carInfo) {
        return "📝 获客推广文案\n\n" +
                "✨ 方案一：🔥 限时秒杀！精品二手车，车况精品价格美丽！\n" +
                "📋 方案二：【车辆推荐】无事故泡水，配置丰富，同价位性价比最高。\n" +
                "💬 方案三：朋友们～做二手车多年，靠谱是底线，有需要私信我～";
    }

    private String buildFallbackOutreach() {
        return "🤖 智能外联方案\n\n" +
                "1️⃣ 首次接触：您好，看到您对这款车感兴趣，目前有现车...\n" +
                "2️⃣ 跟进：上次聊的车刚到新现车，现在订车有优惠...\n" +
                "3️⃣ 促成：这周有团购活动，价格再优惠，您看周末方便吗？";
    }

    private String buildFallbackDistribute(String carId) {
        return "🚗 车源分发建议\n\n" +
                "推荐投放：5D好车首页、二手车之家、瓜子二手车\n" +
                "目标客户：25-45岁，家用购车群体\n" +
                "定价策略：参考同款车型均价，适当让利促成交";
    }

    private String buildFallbackCarAnalysis(String brand, String model, String year, String mileage) {
        return String.format("📋 车源评估报告\n\n车辆：%s %s %s款 %s公里\n\n" +
                "综合评分：⭐⭐⭐⭐☆ (4.5/5)\n" +
                "优势：品牌口碑好，市场认可度高\n" +
                "注意：建议检查底盘和发动机\n" +
                "市场价：参考同款车型近期成交价", brand, model, year, mileage);
    }

    private String buildFallbackPriceEstimate(String brand, String model, String year) {
        return String.format("💰 估价报告\n\n车辆：%s %s %s款\n\n" +
                "估价需综合考虑车况、里程、市场供需等因素。\n" +
                "建议参考同款车型近期成交价，或到专业机构进行检测评估。", brand, model, year);
    }
}
