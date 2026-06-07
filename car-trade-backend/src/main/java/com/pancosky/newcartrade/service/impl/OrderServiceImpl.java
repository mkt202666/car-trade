package com.pancosky.newcartrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.hutool.core.util.IdUtil;
import com.pancosky.newcartrade.converter.OrderConverter;
import com.pancosky.newcartrade.dto.OrderCreateDTO;
import com.pancosky.newcartrade.entity.Order;
import com.pancosky.newcartrade.entity.OrderLog;
import com.pancosky.newcartrade.exception.BusinessException;
import com.pancosky.newcartrade.mapper.DepositAccountMapper;
import com.pancosky.newcartrade.mapper.OrderLogMapper;
import com.pancosky.newcartrade.mapper.OrderMapper;
import com.pancosky.newcartrade.service.ContractService;
import com.pancosky.newcartrade.service.OrderService;
import com.pancosky.newcartrade.util.SecurityUtils;
import com.pancosky.newcartrade.vo.OrderDetailVO;
import com.pancosky.newcartrade.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderLogMapper orderLogMapper;
    private final DepositAccountMapper depositAccountMapper;
    private final ContractService contractService;
    private final OrderConverter orderConverter;

    private String generateOrderId() {
        return "DJ" + System.currentTimeMillis() + new Random().nextInt(1000);
    }

    @Override
    public List<OrderVO> list(String type, String status) {
        Long userId = SecurityUtils.getCurrentUserId();
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if ("sold".equals(type)) {
            wrapper.eq(Order::getSellerId, userId);
        } else if ("bought".equals(type)) {
            wrapper.eq(Order::getBuyerId, userId);
        } else {
            wrapper.and(w -> w.eq(Order::getBuyerId, userId).or().eq(Order::getSellerId, userId));
        }
        if (status != null && !"ALL".equals(status)) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreatedAt);
        List<Order> orders = orderMapper.selectList(wrapper);
        return orders.stream().map(orderConverter::toVO).collect(Collectors.toList());
    }

    @Override
    public OrderDetailVO detail(String id) {
        Order order = orderMapper.selectById(id);
        if (order == null) throw new BusinessException("Order not found");
        return orderConverter.toDetailVO(order);
    }

    @Override
    @Transactional
    public OrderVO create(OrderCreateDTO dto) {
        Order order = new Order();
        order.setId(generateOrderId());
        order.setCarId(dto.getCarId());
        order.setBuyerId(SecurityUtils.getCurrentUserId());
        order.setDepositAmount(dto.getDepositAmount());
        order.setRemark(dto.getRemark());
        order.setStatus("PENDING_CONFIRM");
        orderMapper.insert(order);
        return orderConverter.toVO(order);
    }

    @Override
    @Transactional
    public void confirm(String id) {
        Order order = orderMapper.selectById(id);
        if (order == null) throw new BusinessException("Order not found");
        order.setStatus("TRADING");
        orderMapper.updateById(order);
        contractService.create(id);
        addLog(id, "confirm", "Order confirmed");
    }

    @Override
    @Transactional
    public void cancel(String id) {
        Order order = orderMapper.selectById(id);
        if (order == null) throw new BusinessException("Order not found");
        order.setStatus("CANCELLED");
        order.setCancelledAt(LocalDateTime.now());
        orderMapper.updateById(order);
        addLog(id, "cancel", "Order cancelled");
    }

    @Override
    @Transactional
    public void payDeposit(String id) {
        Order order = orderMapper.selectById(id);
        if (order == null) throw new BusinessException("Order not found");
        order.setBuyerDepositPaid(true);
        order.setBuyerDepositPaidAt(LocalDateTime.now());
        orderMapper.updateById(order);
        addLog(id, "pay_deposit", "Deposit paid");
    }

    @Override
    @Transactional
    public void complete(String id) {
        Order order = orderMapper.selectById(id);
        if (order == null) throw new BusinessException("Order not found");
        order.setStatus("COMPLETED");
        order.setCompletedAt(LocalDateTime.now());
        orderMapper.updateById(order);
        addLog(id, "complete", "Order completed");
    }

    @Override
    @Transactional
    public void dispute(String id) {
        Order order = orderMapper.selectById(id);
        if (order == null) throw new BusinessException("Order not found");
        order.setStatus("DISPUTE");
        orderMapper.updateById(order);
        addLog(id, "dispute", "Dispute raised");
    }

    @Override
    public List<Object> logs(String id) {
        LambdaQueryWrapper<OrderLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderLog::getOrderId, id);
        wrapper.orderByDesc(OrderLog::getCreatedAt);
        return orderLogMapper.selectList(wrapper).stream()
                .map(log -> (Object) log)
                .collect(Collectors.toList());
    }

    private void addLog(String orderId, String action, String remark) {
        OrderLog logEntry = new OrderLog();
        logEntry.setOrderId(orderId);
        logEntry.setAction(action);
        logEntry.setOperatorId(SecurityUtils.getCurrentUserId());
        logEntry.setRemark(remark);
        orderLogMapper.insert(logEntry);
    }
}
