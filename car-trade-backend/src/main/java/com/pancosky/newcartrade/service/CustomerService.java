package com.pancosky.newcartrade.service;

import com.pancosky.newcartrade.vo.TicketDetailVO;
import com.pancosky.newcartrade.vo.TicketVO;

import java.util.List;

public interface CustomerService {
    TicketVO createTicket(Object dto);
    List<TicketVO> listTickets(String status);
    TicketDetailVO ticketDetail(Long id);
}
