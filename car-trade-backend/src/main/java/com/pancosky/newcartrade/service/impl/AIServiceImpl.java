package com.pancosky.newcartrade.service.impl;

import com.pancosky.newcartrade.service.AIService;
import com.pancosky.newcartrade.util.AiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AIServiceImpl implements AIService {

    private final AiClient aiClient;

    @Override
    public Map<String, Object> chat(Map<String, Object> params) {
        String message = (String) params.get("message");
        List<Map<String, String>> history = (List<Map<String, String>>) params.get("history");

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content",
                "你是5D好车的AI助理，专注于二手车交易领域。你可以帮助用户：找车、行情分析、估价、获客文案生成等。回复要专业、简洁、有帮助。"));
        if (history != null) {
            messages.addAll(history);
        }
        messages.add(Map.of("role", "user", "content", message));

        String reply = aiClient.chat(messages);
        if (reply == null) {
            reply = generateSmartReply(message);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("content", reply);
        result.put("type", "text");
        return result;
    }

    @Override
    public Map<String, Object> search(Map<String, Object> params) {
        String budget = (String) params.getOrDefault("budget", "");
        String carType = (String) params.getOrDefault("carType", "");
        String brand = (String) params.getOrDefault("brand", "");
        String year = (String) params.getOrDefault("year", "");
        String requirements = (String) params.getOrDefault("requirements", "");

        String prompt = String.format(
                "你是二手车专家。用户想找车，需求如下：预算=%s, 车型=%s, 品牌=%s, 年份=%s, 其他需求=%s。" +
                "请推荐3-5款车型，每款说明推荐理由、参考价格区间、优缺点。格式清晰。",
                budget, carType, brand, year, requirements);

        String reply = aiClient.chat(null, prompt);
        if (reply == null) {
            reply = buildFallbackSearch(budget, carType, brand);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("content", reply);
        result.put("cars", new ArrayList<>());
        result.put("type", "search");
        return result;
    }

    @Override
    public Map<String, Object> marketAnalysis(Map<String, Object> params) {
        String brand = (String) params.getOrDefault("brand", "");
        String carType = (String) params.getOrDefault("carType", "");

        String prompt = String.format(
                "你是二手车市场分析师。请分析当前二手车市场行情，品牌=%s, 车型类型=%s。" +
                "包含：市场概览、热门车型及价格、价格走势、购买建议。用数据说话。",
                brand, carType);

        String reply = aiClient.chat(null, prompt);
        if (reply == null) {
            reply = buildFallbackMarketAnalysis(brand);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("content", reply);
        result.put("type", "market_analysis");
        return result;
    }

    @Override
    public Map<String, Object> generateCopywriting(Map<String, Object> params) {
        String carInfo = (String) params.getOrDefault("carInfo", "");
        String platform = (String) params.getOrDefault("platform", "朋友圈");
        String style = (String) params.getOrDefault("style", "吸引人");

        String prompt = String.format(
                "你是营销文案专家。为以下二手车生成推广文案：\n车辆信息：%s\n发布平台：%s\n风格：%s\n" +
                "生成3个不同风格的文案方案，每个方案有标题和正文，适合直接复制发布。",
                carInfo, platform, style);

        String reply = aiClient.chat(null, prompt);
        if (reply == null) {
            reply = buildFallbackCopywriting(carInfo);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("content", reply);
        result.put("type", "copywriting");
        return result;
    }

    @Override
    public Map<String, Object> autoOutreach(Map<String, Object> params) {
        String customerInfo = (String) params.getOrDefault("customerInfo", "");
        String carInfo = (String) params.getOrDefault("carInfo", "");

        String prompt = String.format(
                "你是汽车销售顾问。根据客户信息生成智能外联话术：\n客户信息：%s\n车辆信息：%s\n" +
                "生成首次接触、跟进、促成三个阶段的话术。",
                customerInfo, carInfo);

        String reply = aiClient.chat(null, prompt);
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
        String carId = (String) params.getOrDefault("carId", "");
        String city = (String) params.getOrDefault("city", "");

        String prompt = String.format(
                "你是二手车运营专家。为车源ID=%s（所在城市=%s）制定智能分发策略。" +
                "包含：推荐投放渠道、目标客户画像、定价建议、推广策略。",
                carId, city);

        String reply = aiClient.chat(null, prompt);
        if (reply == null) {
            reply = buildFallbackDistribute(carId);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("content", reply);
        result.put("type", "distribute");
        return result;
    }

    @Override
    public Map<String, Object> carAnalysis(Map<String, Object> params) {
        String brand = (String) params.getOrDefault("brand", "");
        String model = (String) params.getOrDefault("model", "");
        String year = (String) params.getOrDefault("year", "");
        String mileage = (String) params.getOrDefault("mileage", "");

        String prompt = String.format(
                "你是二手车评估师。对以下车辆进行价值评估分析：品牌=%s, 车型=%s, 年份=%s, 里程=%s公里。" +
                "包含：综合评分、优势分析、注意事项、市场对比、销售建议、估价区间。",
                brand, model, year, mileage);

        String reply = aiClient.chat(null, prompt);
        if (reply == null) {
            reply = buildFallbackCarAnalysis(brand, model, year, mileage);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("content", reply);
        result.put("type", "car_analysis");
        return result;
    }

    @Override
    public Map<String, Object> priceEstimate(Map<String, Object> params) {
        String brand = (String) params.getOrDefault("brand", "");
        String model = (String) params.getOrDefault("model", "");
        String year = (String) params.getOrDefault("year", "");
        String mileage = (String) params.getOrDefault("mileage", "");
        String condition = (String) params.getOrDefault("condition", "");

        String prompt = String.format(
                "你是二手车估价师。对以下车辆进行估价：品牌=%s, 车型=%s, 年份=%s, 里程=%s公里, 车况=%s。" +
                "给出估价区间、建议售价、价格影响因素分析、历史成交参考。",
                brand, model, year, mileage, condition);

        String reply = aiClient.chat(null, prompt);
        if (reply == null) {
            reply = buildFallbackPriceEstimate(brand, model, year);
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
