package com.pancosky.newcartrade.service;

import java.util.Map;

public interface AIService {

    /**
     * AI对话（一次性返回）
     */
    Map<String, Object> chat(Map<String, Object> params);

    /**
     * AI对话（流式输出，SSE）
     * 每收到一段内容会触发 onNext，完成后触发 onComplete
     */
    void chatStream(Map<String, Object> params,
                    java.util.function.Consumer<String> onNext,
                    java.util.function.Consumer<String> onComplete,
                    java.util.function.Consumer<Exception> onError);

    /**
     * 智能找车
     */
    Map<String, Object> search(Map<String, Object> params);

    /**
     * 行情分析
     */
    Map<String, Object> marketAnalysis(Map<String, Object> params);

    /**
     * 获客文案生成
     */
    Map<String, Object> generateCopywriting(Map<String, Object> params);

    /**
     * 自动外联
     */
    Map<String, Object> autoOutreach(Map<String, Object> params);

    /**
     * 车源分配
     */
    Map<String, Object> distributeCar(Map<String, Object> params);

    /**
     * 车源分析
     */
    Map<String, Object> carAnalysis(Map<String, Object> params);

    /**
     * 估价助手
     */
    Map<String, Object> priceEstimate(Map<String, Object> params);
}
