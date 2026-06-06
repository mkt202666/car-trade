package com.pancosky.newcartrade.service;

import com.pancosky.newcartrade.dto.OrderCreateDTO;
import com.pancosky.newcartrade.vo.OrderDetailVO;
import com.pancosky.newcartrade.vo.OrderVO;

import java.util.List;

public interface OrderService {
    List<OrderVO> list(String type, String status);
    OrderDetailVO detail(String id);
    OrderVO create(OrderCreateDTO dto);
    void confirm(String id);
    void cancel(String id);
    void payDeposit(String id);
    void complete(String id);
    void dispute(String id);
    List<Object> logs(String id);
}
