package com.pancosky.newcartrade.controller;
import com.pancosky.newcartrade.common.RequiresAuth;
import com.pancosky.newcartrade.common.AuthLevel;

import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.dto.TicketCreateDTO;
import com.pancosky.newcartrade.service.CustomerService;
import com.pancosky.newcartrade.vo.TicketDetailVO;
import com.pancosky.newcartrade.vo.TicketVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 客服工单控制器
 * 描述：提供工单创建、列表查询及详情查看接口。
 * 基础路径：/api/v1/cs
 * 认证要求：所有工单相关接口必须登录；用户只能看到自己的工单。
 */
@RestController
@RequestMapping("/api/v1/cs")
@RequiredArgsConstructor
@RequiresAuth(AuthLevel.PROTECTED)
public class CustomerServiceController {

    private final CustomerService customerService;

    /**
     * 创建客服工单
     * HTTP: POST /api/v1/cs/tickets
     * 请求参数：TicketCreateDTO（JSON body）—— title、category（ORDER/PAYMENT/CERTIFICATION/AUCTION/OTHER）、
     *         content、priority（LOW/NORMAL/HIGH/URGENT）。
     * 响应：ApiResponse&lt;TicketVO&gt; —— 返回创建后的工单基本信息。
     * 认证要求：必须登录。
     */
    @PostMapping("/tickets")
    public ApiResponse<TicketVO> createTicket(@RequestBody TicketCreateDTO dto) {
        return ApiResponse.success(customerService.createTicket(dto));
    }

    /**
     * 我的工单列表
     * HTTP: GET /api/v1/cs/tickets?status=OPEN
     * 请求参数：status（可选，OPEN/CLOSED/PROCESSING 等）。
     * 响应：ApiResponse&lt;List&lt;TicketVO&gt;&gt; —— 当前用户提交的工单，按创建时间倒序。
     * 认证要求：必须登录。
     */
    @GetMapping("/tickets")
    public ApiResponse<List<TicketVO>> listTickets(@RequestParam(required = false) String status) {
        return ApiResponse.success(customerService.listTickets(status));
    }

    /**
     * 工单详情
     * HTTP: GET /api/v1/cs/tickets/{id}
     * 请求参数：id（path，工单ID）
     * 响应：ApiResponse&lt;TicketDetailVO&gt; —— 工单详细内容，含客服回复、操作记录。
     * 认证要求：必须登录；仅可查看自己创建的工单或管理员可见工单。
     */
    @GetMapping("/tickets/{id}")
    public ApiResponse<TicketDetailVO> ticketDetail(@PathVariable Long id) {
        return ApiResponse.success(customerService.ticketDetail(id));
    }
}
