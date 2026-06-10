package com.pancosky.newcartrade.service;

import com.pancosky.newcartrade.dto.OrderCreateDTO;
import com.pancosky.newcartrade.vo.OrderDetailVO;
import com.pancosky.newcartrade.vo.OrderVO;

import java.util.List;
import java.util.Map;

/**
 * 订单服务接口
 */
public interface OrderService {

    /**
     * 获取订单列表
     * @param type 类型：sold-卖出，bought-买入
     * @param status 状态筛选
     * @return 订单列表
     */
    List<OrderVO> list(String type, String status);

    /**
     * 获取订单详情
     * @param id 订单ID
     * @return 订单详情
     */
    OrderDetailVO detail(String id);

    /**
     * 创建订单
     * @param dto 订单创建DTO
     * @return 订单VO
     */
    OrderVO create(OrderCreateDTO dto);

    /**
     * 确认订单
     * @param id 订单ID
     */
    void confirm(String id);

    /**
     * 取消订单
     * @param id 订单ID
     */
    void cancel(String id);

    /**
     * 支付保证金
     * @param id 订单ID
     */
    void payDeposit(String id);

    /**
     * 完成订单
     * @param id 订单ID
     */
    void complete(String id);

    /**
     * 创建争议
     * @param id 订单ID
     */
    void dispute(String id);

    /**
     * 提交合同
     * @param id 订单ID
     * @param content 合同内容
     */
    void submitContract(String id, String content);

    /**
     * 更新合同
     * @param id 订单ID
     * @param content 合同内容
     */
    void updateContract(String id, String content);

    /**
     * 确认合同
     * @param id 订单ID
     */
    void confirmContract(String id);

    /**
     * 获取合同
     * @param id 订单ID
     * @return 合同内容
     */
    Map<String, Object> getContract(String id);

    /**
     * 终止交易
     * @param id 订单ID
     * @param reason 终止原因
     */
    void terminate(String id, String reason);

    /**
     * 获取终止交易次数
     * @param id 订单ID
     * @return 剩余次数
     */
    Map<String, Integer> getTerminateCount(String id);

    /**
     * 获取订单日志
     * @param id 订单ID
     * @return 日志列表
     */
    List<Object> logs(String id);
}
