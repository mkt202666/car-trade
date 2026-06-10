package com.pancosky.newcartrade.controller;

import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
public class AIController {

    private final AIService aiService;

    /**
     * AI 对话（车源买卖咨询/通用问答）
     * HTTP: POST /api/v1/ai/chat
     * 请求参数：body（JSON，常见字段如 message、contextId、history）
     * 响应：ApiResponse&lt;Map&lt;String, Object&gt;&gt; —— AI 回复内容、推荐相关车源等。
     * 认证要求：必须登录。
     * 限流：单用户每分钟最多 30 次请求。
     */
    @PostMapping("/chat")
    public ApiResponse<Map<String, Object>> chat(@RequestBody Map<String, Object> params) {
        return ApiResponse.success(aiService.chat(params));
    }

    /**
     * 智能找车（基于用户偏好、条件的个性化推荐）
     * HTTP: POST /api/v1/ai/search
     * 请求参数：body（JSON，含品牌、预算、里程、能源类型、用途等条件）。
     * 响应：ApiResponse&lt;Map&lt;String, Object&gt;&gt; —— 推荐车源列表及评分说明。
     * 认证要求：必须登录。
     */
    @PostMapping("/search")
    public ApiResponse<Map<String, Object>> search(@RequestBody Map<String, Object> params) {
        return ApiResponse.success(aiService.search(params));
    }

    /**
     * 行情分析（按品牌/车系/地区的价格走势与供需评估）
     * HTTP: POST /api/v1/ai/market-analysis
     * 请求参数：body（JSON，含 brandId、seriesId、region 等）。
     * 响应：ApiResponse&lt;Map&lt;String, Object&gt;&gt; —— 价格走势、均价、保值率、供需指数等。
     * 认证要求：必须登录。
     */
    @PostMapping("/market-analysis")
    public ApiResponse<Map<String, Object>> marketAnalysis(@RequestBody Map<String, Object> params) {
        return ApiResponse.success(aiService.marketAnalysis(params));
    }

    /**
     * 获客文案生成（卖家用于车源描述、营销文案生成）
     * HTTP: POST /api/v1/ai/customer-generation
     * 请求参数：body（JSON，含 carId、tone、length 等）。
     * 响应：ApiResponse&lt;Map&lt;String, Object&gt;&gt; —— 生成的文案内容及可选的图片提示词。
     * 认证要求：必须登录；建议仅向已认证卖家开放。
     */
    @PostMapping("/customer-generation")
    public ApiResponse<Map<String, Object>> generateCopywriting(@RequestBody Map<String, Object> params) {
        return ApiResponse.success(aiService.generateCopywriting(params));
    }

    /**
     * 自动外联（系统自动向潜在买家推送车源信息）
     * HTTP: POST /api/v1/ai/auto-outreach
     * 请求参数：body（JSON，含 carId、targetUsers、渠道等）。
     * 响应：ApiResponse&lt;Map&lt;String, Object&gt;&gt; —— 执行结果统计、发送数、到达数。
     * 认证要求：必须登录；建议仅向已认证卖家/管理员开放。
     */
    @PostMapping("/auto-outreach")
    public ApiResponse<Map<String, Object>> autoOutreach(@RequestBody Map<String, Object> params) {
        return ApiResponse.success(aiService.autoOutreach(params));
    }

    /**
     * 车源分配（平台根据线索将车源分配给销售或店铺）
     * HTTP: POST /api/v1/ai/distribute
     * 请求参数：body（JSON，含 carId、店铺/销售信息等）。
     * 响应：ApiResponse&lt;Map&lt;String, Object&gt;&gt; —— 分配结果。
     * 认证要求：必须登录；管理员或运营角色可用。
     */
    @PostMapping("/distribute")
    public ApiResponse<Map<String, Object>> distributeCar(@RequestBody Map<String, Object> params) {
        return ApiResponse.success(aiService.distributeCar(params));
    }

    /**
     * 车源分析（基于车源信息分析车况、性价比、市场热度等）
     * HTTP: POST /api/v1/ai/car-analysis
     * 请求参数：body（JSON，含 carId 或车辆特征信息）。
     * 响应：ApiResponse&lt;Map&lt;String, Object&gt;&gt; —— 分析结果，含车况评分、性价比、风险提示。
     * 认证要求：必须登录。
     */
    @PostMapping("/car-analysis")
    public ApiResponse<Map<String, Object>> carAnalysis(@RequestBody Map<String, Object> params) {
        return ApiResponse.success(aiService.carAnalysis(params));
    }

    /**
     * 估价助手（根据品牌、车系、年份、里程、地区等估算二手车参考价）
     * HTTP: POST /api/v1/ai/price-estimate
     * 请求参数：body（JSON，含 brandId、seriesId、year、mileage、city 等）。
     * 响应：ApiResponse&lt;Map&lt;String, Object&gt;&gt; —— 估价结果、最低价/最高价、置信度。
     * 认证要求：必须登录。
     */
    @PostMapping("/price-estimate")
    public ApiResponse<Map<String, Object>> priceEstimate(@RequestBody Map<String, Object> params) {
        return ApiResponse.success(aiService.priceEstimate(params));
    }
}
