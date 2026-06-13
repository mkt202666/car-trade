package com.pancosky.newcartrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.hutool.core.util.IdUtil;
import com.pancosky.newcartrade.converter.OrderConverter;
import com.pancosky.newcartrade.dto.OrderCreateDTO;
import com.pancosky.newcartrade.entity.Order;
import com.pancosky.newcartrade.entity.OrderLog;
import com.pancosky.newcartrade.exception.BusinessException;
import com.pancosky.newcartrade.mapper.DepositAccountMapper;
import com.pancosky.newcartrade.security.OwnerAssert;
import com.pancosky.newcartrade.mapper.OrderLogMapper;
import com.pancosky.newcartrade.mapper.OrderMapper;
import com.pancosky.newcartrade.service.ContractService;
import com.pancosky.newcartrade.service.OrderService;
import com.pancosky.newcartrade.util.SecurityUtils;
import com.pancosky.newcartrade.vo.OrderDetailVO;
import com.pancosky.newcartrade.vo.OrderStatsVO;
import com.pancosky.newcartrade.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.math.BigDecimal;
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
        Order order = loadAndAssert(id);
        order.setStatus("TRADING");
        orderMapper.updateById(order);
        contractService.create(id);
        addLog(id, "confirm", "Order confirmed");
    }

    @Override
    @Transactional
    public void cancel(String id, String reason) {
        Order order = loadAndAssert(id);
        order.setStatus("CANCELLED");
        order.setCancelledAt(LocalDateTime.now());
        if (reason != null) {
            order.setCancelReason(reason);
        }
        orderMapper.updateById(order);
        addLog(id, "cancel", reason != null ? "Order cancelled: " + reason : "Order cancelled");
    }

    @Override
    @Transactional
    public void payDeposit(String id) {
        Order order = loadAndAssert(id);
        order.setBuyerDepositPaid(true);
        order.setBuyerDepositPaidAt(LocalDateTime.now());
        orderMapper.updateById(order);
        addLog(id, "pay_deposit", "Deposit paid");
    }

    @Override
    @Transactional
    public void complete(String id) {
        Order order = loadAndAssert(id);
        order.setStatus("COMPLETED");
        order.setCompletedAt(LocalDateTime.now());
        orderMapper.updateById(order);
        addLog(id, "complete", "Order completed");
    }

    @Override
    @Transactional
    public void dispute(String id) {
        Order order = loadAndAssert(id);
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

    @Override
    @Transactional
    public void submitContract(String id, String content) {
        Order order = loadAndAssert(id);
        if (!"DEPOSIT_PAID".equals(order.getStatus())) {
            throw new BusinessException("Cannot submit contract in current status");
        }
        order.setContractContent(content);
        order.setContractSubmitted(true);
        order.setContractSubmittedAt(LocalDateTime.now());
        order.setStatus("CONTRACT_SUBMITTED");
        orderMapper.updateById(order);
        addLog(id, "submit_contract", "Contract submitted");
    }

    @Override
    @Transactional
    public void updateContract(String id, String content) {
        Order order = loadAndAssert(id);
        if (!"CONTRACT_SUBMITTED".equals(order.getStatus())) {
            throw new BusinessException("Cannot update contract in current status");
        }
        order.setContractContent(content);
        orderMapper.updateById(order);
        addLog(id, "update_contract", "Contract updated");
    }

    @Override
    @Transactional
    public void confirmContract(String id) {
        Order order = loadAndAssert(id);
        if (!"CONTRACT_SUBMITTED".equals(order.getStatus())) {
            throw new BusinessException("Cannot confirm contract in current status");
        }
        order.setContractConfirmed(true);
        order.setContractConfirmedAt(LocalDateTime.now());
        order.setStatus("CONTRACT_CONFIRMED");
        orderMapper.updateById(order);
        addLog(id, "confirm_contract", "Contract confirmed");
    }

    @Override
    public Map<String, Object> getContract(String id) {
        Order order = loadAndAssert(id);
        Map<String, Object> contract = new java.util.HashMap<>();
        contract.put("content", order.getContractContent());
        contract.put("submitted", order.getContractSubmitted());
        contract.put("submittedAt", order.getContractSubmittedAt());
        contract.put("confirmed", order.getContractConfirmed());
        contract.put("confirmedAt", order.getContractConfirmedAt());
        return contract;
    }

    @Override
    @Transactional
    public void terminate(String id, String reason) {
        Order order = loadAndAssert(id);

        // 检查终止次数限制
        if (order.getTerminateCount() != null && order.getTerminateCount() >= 3) {
            // 检查是否是今天
            if (order.getLastTerminateAt() != null &&
                order.getLastTerminateAt().toLocalDate().equals(LocalDateTime.now().toLocalDate())) {
                throw new BusinessException("Today's terminate limit reached");
            }
            // 重置计数
            order.setTerminateCount(0);
        }

        order.setTerminateReason(reason);
        order.setTerminateCount((order.getTerminateCount() == null ? 0 : order.getTerminateCount()) + 1);
        order.setLastTerminateAt(LocalDateTime.now());
        order.setStatus("TERMINATED");
        orderMapper.updateById(order);
        addLog(id, "terminate", "Transaction terminated: " + reason);
    }

    @Override
    public Map<String, Integer> getTerminateCount(String id) {
        Order order = loadAndAssert(id);

        int used = order.getTerminateCount() == null ? 0 : order.getTerminateCount();
        int remaining = 3 - used;

        // 检查是否是今天
        if (order.getLastTerminateAt() != null &&
            !order.getLastTerminateAt().toLocalDate().equals(LocalDateTime.now().toLocalDate())) {
            remaining = 3; // 不是今天，重置
        }

        Map<String, Integer> result = new java.util.HashMap<>();
        result.put("used", used);
        result.put("remaining", Math.max(0, remaining));
        result.put("limit", 3);
        return result;
    }

    /**
     * 加载订单并做"买家或卖家"权限校验；找不到订单或当前用户不是双方时抛异常
     */
    private Order loadAndAssert(String id) {
        if (id == null || id.isBlank()) {
            throw new BusinessException("Order id missing");
        }
        Order order = orderMapper.selectById(id);
        if (order == null) throw new BusinessException("Order not found");
        Long current = SecurityUtils.getCurrentUserId();
        OwnerAssert.assertBuyerOrSeller(current, order.getBuyerId(), order.getSellerId());
        return order;
    }

    @Override
    public OrderStatsVO getStats() {
        OrderStatsVO stats = new OrderStatsVO();

        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        Long userId = SecurityUtils.getCurrentUserId();

        // 作为买家或卖家的所有订单
        wrapper.and(w -> w.eq(Order::getBuyerId, userId).or().eq(Order::getSellerId, userId));

        List<Order> allOrders = orderMapper.selectList(wrapper);
        stats.setTotalCount(allOrders.size());

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Order order : allOrders) {
            String status = order.getStatus();
            if ("PENDING_CONFIRM".equals(status)) {
                stats.setPendingConfirmCount(stats.getPendingConfirmCount() + 1);
            } else if ("TRADING".equals(status)) {
                stats.setTradingCount(stats.getTradingCount() + 1);
            } else if ("DISPUTE".equals(status)) {
                stats.setDisputeCount(stats.getDisputeCount() + 1);
            } else if ("COMPLETED".equals(status)) {
                stats.setCompletedCount(stats.getCompletedCount() + 1);
                if (order.getTotalPrice() != null) {
                    totalAmount = totalAmount.add(order.getTotalPrice());
                }
            } else if ("CANCELLED".equals(status) || "TERMINATED".equals(status)) {
                stats.setCancelledCount(stats.getCancelledCount() + 1);
            }
            // 待支付保证金: 有 deposit 但未支付
            if (order.getDepositAmount() != null
                    && order.getDepositAmount().compareTo(BigDecimal.ZERO) > 0
                    && !Boolean.TRUE.equals(order.getBuyerDepositPaid())) {
                stats.setPendingDepositCount(stats.getPendingDepositCount() + 1);
            }
        }
        stats.setTotalAmount(totalAmount);
        return stats;
    }
}
