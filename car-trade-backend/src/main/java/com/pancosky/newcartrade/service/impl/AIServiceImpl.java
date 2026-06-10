package com.pancosky.newcartrade.service.impl;

import com.pancosky.newcartrade.service.AIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class AIServiceImpl implements AIService {

    @Override
    public Map<String, Object> chat(Map<String, Object> params) {
        String message = (String) params.get("message");
        List<Map<String, String>> history = (List<Map<String, String>>) params.get("history");
        
        log.info("AI对话请求 - message: {}, history size: {}", message, history != null ? history.size() : 0);
        
        Map<String, Object> result = new HashMap<>();
        
        // 简单的对话回复逻辑
        String reply = generateSmartReply(message);
        
        result.put("content", reply);
        result.put("type", "text");
        return result;
    }

    @Override
    public Map<String, Object> search(Map<String, Object> params) {
        log.info("智能找车请求 - params: {}", params);
        
        Map<String, Object> result = new HashMap<>();
        
        // 预算
        String budget = (String) params.getOrDefault("budget", "");
        // 车型
        String carType = (String) params.getOrDefault("carType", "");
        // 品牌
        String brand = (String) params.getOrDefault("brand", "");
        // 年份
        String year = (String) params.getOrDefault("year", "");
        // 其他需求
        String requirements = (String) params.getOrDefault("requirements", "");
        
        // 生成智能推荐回复
        StringBuilder reply = new StringBuilder();
        reply.append("根据您的需求，我为您找到以下推荐：\n\n");
        reply.append("🚗 推荐车型：");
        
        if (brand != null && !brand.isEmpty()) {
            reply.append(brand).append("\n");
        } else {
            reply.append("宝马X5、大众途昂、本田CR-V\n");
        }
        
        if (budget != null && !budget.isEmpty()) {
            reply.append("💰 预算范围：").append(budget).append("\n");
        }
        
        if (carType != null && !carType.isEmpty()) {
            reply.append("🚙 车型：").append(carType).append("\n");
        }
        
        reply.append("\n📋 具体推荐：\n");
        reply.append("1. 宝马X5 2023款 xDrive40i M运动套装 - 约65万\n");
        reply.append("2. 大众途昂 2024款 380TSI 四驱尊崇豪华版 - 约35万\n");
        reply.append("3. 本田CR-V 2024款 240TURBO CVT两驱活力版 - 约18万\n");
        reply.append("\n如需查看更多详情，请告诉我您的具体需求！");
        
        result.put("content", reply.toString());
        result.put("cars", new ArrayList<>());
        result.put("type", "search");
        return result;
    }

    @Override
    public Map<String, Object> marketAnalysis(Map<String, Object> params) {
        log.info("行情分析请求 - params: {}", params);
        
        Map<String, Object> result = new HashMap<>();
        
        String brand = (String) params.getOrDefault("brand", "");
        String carType = (String) params.getOrDefault("carType", "");
        
        StringBuilder reply = new StringBuilder();
        reply.append("📊 二手车市场行情分析\n\n");
        
        if (brand != null && !brand.isEmpty()) {
            reply.append("品牌：").append(brand).append("\n");
        }
        
        reply.append("【市场概览】\n");
        reply.append("• 2024年上半年二手车市场整体稳中有升\n");
        reply.append("• 新能源二手车保值率持续提升\n");
        reply.append("• 豪华品牌价格趋于理性\n\n");
        
        reply.append("【热门车型】\n");
        reply.append("1. 宝马X5 - 平均成交价55-70万，需求稳定\n");
        reply.append("2. 奔驰E级 - 平均成交价40-55万，年轻人首选\n");
        reply.append("3. 奥迪A6L - 平均成交价35-48万，商务首选\n");
        reply.append("4. 特斯拉Model Y - 平均成交价22-30万，增长迅速\n\n");
        
        reply.append("【价格走势】\n");
        reply.append("• 中高端车型价格下降约5%\n");
        reply.append("• 经济型车型价格基本持平\n");
        reply.append("• 准新车价格溢价明显\n\n");
        
        reply.append("【建议】\n");
        reply.append("• 购买时机：建议在季度末购买，优惠更大\n");
        reply.append("• 关注车况：重点检查底盘和发动机\n");
        reply.append("• 手续核查：确保过户手续齐全");
        
        result.put("content", reply.toString());
        result.put("type", "market_analysis");
        return result;
    }

    @Override
    public Map<String, Object> generateCopywriting(Map<String, Object> params) {
        log.info("获客文案生成请求 - params: {}", params);
        
        Map<String, Object> result = new HashMap<>();
        
        String carInfo = (String) params.getOrDefault("carInfo", "");
        String style = (String) params.getOrDefault("style", "吸引人");
        
        StringBuilder reply = new StringBuilder();
        reply.append("📝 获客推广文案\n\n");
        
        if (carInfo != null && !carInfo.isEmpty()) {
            reply.append("【").append(carInfo).append("】\n\n");
        }
        
        // 生成多种风格的文案
        reply.append("✨ 方案一（吸引人型）：\n");
        reply.append("🔥 限时秒杀！错过等一年！\n");
        reply.append("精品二手车来袭，这车太香了！\n");
        reply.append("车况精品，价格美丽，欢迎来撩~ 🚗💨\n\n");
        
        reply.append("📋 方案二（专业型）：\n");
        reply.append("【车辆推荐】\n");
        reply.append("• 车况：精品车况，无事故泡水\n");
        reply.append("• 配置：顶配车型，配置丰富\n");
        reply.append("• 价格：同价位性价比最高\n");
        reply.append("• 服务：提供过户、上牌一站式服务\n\n");
        
        reply.append("💬 方案三（亲和型）：\n");
        reply.append("哈喽朋友们～\n");
        reply.append("给家里老人寻个靠谱的代步车，欢迎咨询我呀！\n");
        reply.append("做二手车多年，靠谱是我的底线 😊\n");
        reply.append("有需要的朋友可以私信我～");
        
        result.put("content", reply.toString());
        result.put("type", "copywriting");
        return result;
    }

    @Override
    public Map<String, Object> autoOutreach(Map<String, Object> params) {
        log.info("自动外联请求 - params: {}", params);
        
        Map<String, Object> result = new HashMap<>();
        
        String customerInfo = (String) params.getOrDefault("customerInfo", "");
        String message = (String) params.getOrDefault("message", "");
        
        StringBuilder reply = new StringBuilder();
        reply.append("🤖 智能外联方案\n\n");
        
        reply.append("【客户画像分析】\n");
        reply.append("• 意向车型：紧凑型SUV\n");
        reply.append("• 预算范围：15-20万\n");
        reply.append("• 购车用途：家用代步\n");
        reply.append("• 关注点：油耗、保值率\n\n");
        
        reply.append("【推荐话术】\n");
        reply.append("1️⃣ 首次接触：\n");
        reply.append("\"您好，我是5D好车的销售顾问，看到您对我们的大众途岳比较感兴趣...\n");
        reply.append("这款车目前有现车，颜色齐全，请问您方便来店里看看吗？\"\n\n");
        
        reply.append("2️⃣ 跟进话术：\n");
        reply.append("\"您好，上次跟您聊的途岳我们店刚到了一批新现车...\n");
        reply.append("现在订车还可以享受5000元优惠，请问您考虑得怎么样了？\"\n\n");
        
        reply.append("3️⃣ 促成话术：\n");
        reply.append("\"先生/女士，我们这周有个团购活动...\n");
        reply.append("价格可以再优惠2000元，而且送您一年保养套餐...\n");
        reply.append("您看这周末有时间吗？我帮您预留个好车牌？\"");
        
        result.put("content", reply.toString());
        result.put("type", "outreach");
        return result;
    }

    @Override
    public Map<String, Object> distributeCar(Map<String, Object> params) {
        log.info("车源分配请求 - params: {}", params);
        
        Map<String, Object> result = new HashMap<>();
        
        String carId = (String) params.getOrDefault("carId", "");
        String city = (String) params.getOrDefault("city", "");
        
        StringBuilder reply = new StringBuilder();
        reply.append("🚗 车源智能分配方案\n\n");
        
        if (carId != null && !carId.isEmpty()) {
            reply.append("【车源信息】\n");
            reply.append("• 车源ID：").append(carId).append("\n");
        }
        
        reply.append("【分配建议】\n");
        reply.append("1️⃣ 门店A（主城区店）\n");
        reply.append("   • 预计销售周期：7-15天\n");
        reply.append("   • 匹配客户数：15组\n");
        reply.append("   • 销售概率：85%\n\n");
        
        reply.append("2️⃣ 门店B（郊区店）\n");
        reply.append("   • 预计销售周期：15-30天\n");
        reply.append("   • 匹配客户数：8组\n");
        reply.append("   • 销售概率：60%\n\n");
        
        reply.append("【推荐门店】：主城区店\n");
        reply.append("推荐理由：客户资源丰富，曝光率高，成交概率大");
        
        result.put("content", reply.toString());
        result.put("type", "distribute");
        return result;
    }

    @Override
    public Map<String, Object> carAnalysis(Map<String, Object> params) {
        log.info("车源分析请求 - params: {}", params);
        
        Map<String, Object> result = new HashMap<>();
        
        String brand = (String) params.getOrDefault("brand", "");
        String model = (String) params.getOrDefault("model", "");
        String year = (String) params.getOrDefault("year", "");
        String mileage = (String) params.getOrDefault("mileage", "");
        
        StringBuilder reply = new StringBuilder();
        reply.append("📋 车源价值评估报告\n\n");
        
        if (brand != null && !brand.isEmpty()) {
            reply.append("【车辆信息】\n");
            reply.append("• 品牌：").append(brand).append("\n");
        }
        if (model != null && !model.isEmpty()) {
            reply.append("• 车型：").append(model).append("\n");
        }
        if (year != null && !year.isEmpty()) {
            reply.append("• 年份：").append(year).append("年\n");
        }
        if (mileage != null && !mileage.isEmpty()) {
            reply.append("• 里程：").append(mileage).append("公里\n");
        }
        
        reply.append("\n【综合评分】⭐⭐⭐⭐☆ (4.5/5)\n\n");
        
        reply.append("【优势分析】✅\n");
        reply.append("• 品牌口碑良好，市场认可度高\n");
        reply.append("• 外观保持良好，无明显划痕\n");
        reply.append("• 内饰整洁，保养记录完整\n");
        reply.append("• 动力系统表现正常\n\n");
        
        reply.append("【需要注意】⚠️\n");
        reply.append("• 前保险杠有轻微剐蹭\n");
        reply.append("• 右后门有划痕，建议抛光处理\n");
        reply.append("• 刹车片需要关注，建议检查更换周期\n\n");
        
        reply.append("【市场对比】\n");
        reply.append("• 同款车型市场价：22-28万\n");
        reply.append("• 本车评估价：24.5万\n");
        reply.append("• 性价比评分：优秀\n\n");
        
        reply.append("【销售建议】\n");
        reply.append("• 建议零售价：24-26万\n");
        reply.append("• 销售周期预计：7-15天\n");
        reply.append("• 重点推广对象：家用购车群体");
        
        result.put("content", reply.toString());
        result.put("type", "car_analysis");
        result.put("score", 4.5);
        result.put("marketPrice", "22-28万");
        result.put("evalPrice", "24.5万");
        return result;
    }

    @Override
    public Map<String, Object> priceEstimate(Map<String, Object> params) {
        log.info("估价助手请求 - params: {}", params);
        
        Map<String, Object> result = new HashMap<>();
        
        String brand = (String) params.getOrDefault("brand", "");
        String model = (String) params.getOrDefault("model", "");
        String year = (String) params.getOrDefault("year", "");
        String mileage = (String) params.getOrDefault("mileage", "");
        String condition = (String) params.getOrDefault("condition", "");
        
        StringBuilder reply = new StringBuilder();
        reply.append("💰 车辆估价报告\n\n");
        
        if (brand != null && !brand.isEmpty()) {
            reply.append("【车辆信息】\n");
            reply.append("• 品牌：").append(brand).append("\n");
        }
        if (model != null && !model.isEmpty()) {
            reply.append("• 车型：").append(model).append("\n");
        }
        if (year != null && !year.isEmpty()) {
            reply.append("• 年份：").append(year).append("款\n");
        }
        if (mileage != null && !mileage.isEmpty()) {
            reply.append("• 里程：约").append(mileage).append("公里\n");
        }
        if (condition != null && !condition.isEmpty()) {
            reply.append("• 车况：").append(condition).append("\n");
        }
        
        reply.append("\n【估价结果】\n");
        reply.append("┌─────────────────────┐\n");
        reply.append("│  市场估价区间       │\n");
        reply.append("│  ¥180,000 - ¥220,000│\n");
        reply.append("│  建议售价：¥200,000 │\n");
        reply.append("└─────────────────────┘\n\n");
        
        reply.append("【价格影响因素】\n");
        reply.append("📈 增值因素：\n");
        reply.append("• 外观成色较好 +2%\n");
        reply.append("• 保养记录完整 +3%\n");
        reply.append("• 选配丰富 +1%\n\n");
        
        reply.append("📉 减值因素：\n");
        reply.append("• 里程略高 -3%\n");
        reply.append("• 有轻微剐蹭 -1%\n\n");
        
        reply.append("【历史成交参考】\n");
        reply.append("• 2022年同款成交价：21-24万\n");
        reply.append("• 2023年同款成交价：19-22万\n");
        reply.append("• 2024年同款成交价：18-21万\n\n");
        
        reply.append("【估价时间】：2024年\n");
        reply.append("⚠️ 实际成交价受市场供需、车况等因素影响，仅供参考");
        
        result.put("content", reply.toString());
        result.put("type", "price_estimate");
        result.put("minPrice", 180000);
        result.put("maxPrice", 220000);
        result.put("suggestPrice", 200000);
        return result;
    }

    /**
     * 生成智能对话回复
     */
    private String generateSmartReply(String message) {
        if (message == null || message.isEmpty()) {
            return "您好，有什么可以帮您的？";
        }
        
        String lowerMessage = message.toLowerCase();
        
        if (lowerMessage.contains("宝马") || lowerMessage.contains("x5")) {
            return "宝马X5是当前热门的中大型SUV，目前行情价在55-70万之间。具体价格取决于车龄、里程和车况。如果您有具体的购车需求，可以告诉我，我会为您做详细的分析和推荐。";
        }
        
        if (lowerMessage.contains("奔驰") || lowerMessage.contains("e级")) {
            return "奔驰E级是商务人士的首选车型，目前行情价在40-55万之间。新车优惠后和二手车的价差不大，建议关注准新车源，性价比更高。";
        }
        
        if (lowerMessage.contains("丰田") || lowerMessage.contains("本田") || lowerMessage.contains("日系")) {
            return "日系车以省油、耐用、保值著称。推荐车型有：\n• 丰田凯美瑞 - 15-25万\n• 丰田RAV4 - 18-28万\n• 本田CR-V - 15-24万\n• 本田雅阁 - 14-23万\n请问您更关注哪款车呢？";
        }
        
        if (lowerMessage.contains("电动车") || lowerMessage.contains("新能源") || lowerMessage.contains("特斯拉")) {
            return "新能源二手车近年来热度很高！热门车型包括：\n• 特斯拉Model 3 - 20-28万\n• 特斯拉Model Y - 22-32万\n• 比亚迪汉EV - 15-25万\n• 小鹏P7 - 18-28万\n新能源车建议重点关注电池健康状况和续航里程哦！";
        }
        
        if (lowerMessage.contains("suv") || lowerMessage.contains("越野") || lowerMessage.contains("空间")) {
            return "如果您需要大空间SUV，我推荐：\n• 宝马X5 - 55-70万，豪华品牌\n• 大众途昂 - 25-35万，性价比高\n• 丰田汉兰达 - 23-32万，保值神器\n• 本田CR-V - 15-24万，经济实用\n请问您的预算是多少呢？";
        }
        
        if (lowerMessage.contains("行情") || lowerMessage.contains("价格") || lowerMessage.contains("多少钱")) {
            return "目前二手车市场整体趋稳，不同品牌和车型的价格差异较大。总体来说：\n• 豪华品牌：价格下降5%左右\n• 合资品牌：价格基本持平\n• 国产经济型：部分车型略有下降\n想了解具体车型的行情，可以告诉我品牌和车型哦！";
        }
        
        if (lowerMessage.contains("保值") || lowerMessage.contains("划算")) {
            return "要说保值率，以下几款车表现突出：\n1️⃣ 丰田汉兰达 - 3年保值率约80%\n2️⃣ 本田CR-V - 3年保值率约75%\n3️⃣ 宝马X5 - 3年保值率约70%\n4️⃣ 奔驰E级 - 3年保值率约68%\n5️⃣ 大众途观 - 3年保值率约72%\n保值率高的车型后期换车损失小，但也要结合实际需求来选择哦！";
        }
        
        // 默认回复
        return "感谢您的提问！我是5D好车的AI助理。\n\n我可以帮您：\n• 🚗 智能找车 - 根据需求推荐车型\n• 📊 行情分析 - 了解市场价格走势\n• 📝 获客文案 - 生成推广宣传语\n• 🤖 自动外联 - 智能客户触达\n\n请告诉我您的具体需求，我会为您提供专业建议！";
    }
}