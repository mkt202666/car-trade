package com.pancosky.newcartrade.service;

import java.util.Map;

public interface AIService {
    
    /**
     * AI对话
     */
    Map<String, Object> chat(Map<String, Object> params);
    
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