package com.pancosky.newcartrade.controller;

import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.service.CustomerService;
import com.pancosky.newcartrade.vo.TicketDetailVO;
import com.pancosky.newcartrade.vo.TicketVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cs")
@RequiredArgsConstructor
public class CustomerServiceController {

    private final CustomerService customerService;

    @PostMapping("/tickets")
    public ApiResponse<TicketVO> createTicket(@RequestBody Object dto) {
        return ApiResponse.success(customerService.createTicket(dto));
    }

    @GetMapping("/tickets")
    public ApiResponse<List<TicketVO>> listTickets(@RequestParam(required = false) String status) {
        return ApiResponse.success(customerService.listTickets(status));
    }

    @GetMapping("/tickets/{id}")
    public ApiResponse<TicketDetailVO> ticketDetail(@PathVariable Long id) {
        return ApiResponse.success(customerService.ticketDetail(id));
    }
}
