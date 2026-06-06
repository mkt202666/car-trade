package com.pancosky.newcartrade.converter;

import com.pancosky.newcartrade.entity.Order;
import com.pancosky.newcartrade.vo.OrderDetailVO;
import com.pancosky.newcartrade.vo.OrderVO;
import org.springframework.stereotype.Component;

@Component
public class OrderConverter {

    public OrderVO toVO(Order order) {
        if (order == null) return null;
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setCarId(order.getCarId());
        vo.setTotalPrice(order.getTotalPrice());
        vo.setDepositAmount(order.getDepositAmount());
        vo.setStatus(order.getStatus());
        vo.setBuyerId(order.getBuyerId());
        vo.setSellerId(order.getSellerId());
        vo.setCreatedAt(order.getCreatedAt());
        vo.setUpdatedAt(order.getUpdatedAt());
        return vo;
    }

    public OrderDetailVO toDetailVO(Order order) {
        if (order == null) return null;
        OrderDetailVO vo = new OrderDetailVO();
        vo.setId(order.getId());
        vo.setCarId(order.getCarId());
        vo.setTotalPrice(order.getTotalPrice());
        vo.setDepositAmount(order.getDepositAmount());
        vo.setStatus(order.getStatus());
        vo.setBuyerId(order.getBuyerId());
        vo.setSellerId(order.getSellerId());
        vo.setCreatedAt(order.getCreatedAt());
        vo.setUpdatedAt(order.getUpdatedAt());
        vo.setContractNo(order.getContractNo());
        vo.setBuyerDepositPaid(order.getBuyerDepositPaid());
        vo.setSellerDepositPaid(order.getSellerDepositPaid());
        vo.setCancelReason(order.getCancelReason());
        vo.setCompletedAt(order.getCompletedAt());
        vo.setCancelledAt(order.getCancelledAt());
        return vo;
    }
}
