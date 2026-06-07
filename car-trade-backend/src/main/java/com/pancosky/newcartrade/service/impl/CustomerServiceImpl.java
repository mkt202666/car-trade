package com.pancosky.newcartrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pancosky.newcartrade.entity.CustomerServiceTicket;
import com.pancosky.newcartrade.mapper.CustomerServiceTicketMapper;
import com.pancosky.newcartrade.service.CustomerService;
import com.pancosky.newcartrade.util.SecurityUtils;
import com.pancosky.newcartrade.vo.TicketDetailVO;
import com.pancosky.newcartrade.vo.TicketVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerServiceTicketMapper ticketMapper;

    @Override
    @Transactional
    public TicketVO createTicket(Object dto) {
        CustomerServiceTicket ticket = new CustomerServiceTicket();
        ticket.setUserId(SecurityUtils.getCurrentUserId());
        ticket.setStatus("OPEN");
        ticketMapper.insert(ticket);
        TicketVO vo = new TicketVO();
        vo.setId(ticket.getId());
        vo.setStatus(ticket.getStatus());
        vo.setCreatedAt(ticket.getCreatedAt());
        return vo;
    }

    @Override
    public List<TicketVO> listTickets(String status) {
        Long userId = SecurityUtils.getCurrentUserId();
        LambdaQueryWrapper<CustomerServiceTicket> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerServiceTicket::getUserId, userId);
        if (StringUtils.hasText(status)) {
            wrapper.eq(CustomerServiceTicket::getStatus, status);
        }
        wrapper.orderByDesc(CustomerServiceTicket::getCreatedAt);
        List<CustomerServiceTicket> tickets = ticketMapper.selectList(wrapper);
        return tickets.stream().map(t -> {
            TicketVO vo = new TicketVO();
            vo.setId(t.getId());
            vo.setTitle(t.getTitle());
            vo.setStatus(t.getStatus());
            vo.setCreatedAt(t.getCreatedAt());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public TicketDetailVO ticketDetail(Long id) {
        CustomerServiceTicket ticket = ticketMapper.selectById(id);
        if (ticket == null) return null;
        TicketDetailVO vo = new TicketDetailVO();
        vo.setId(ticket.getId());
        vo.setTitle(ticket.getTitle());
        vo.setCategory(ticket.getCategory());
        vo.setStatus(ticket.getStatus());
        vo.setPriority(ticket.getPriority());
        vo.setCreatedAt(ticket.getCreatedAt());
        vo.setUserId(ticket.getUserId());
        vo.setHandlerId(ticket.getHandlerId());
        vo.setHandledAt(ticket.getHandledAt());
        return vo;
    }
}
