package com.pancosky.newcartrade.controller;

import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.dto.OrderCreateDTO;
import com.pancosky.newcartrade.service.OrderService;
import com.pancosky.newcartrade.vo.OrderDetailVO;
import com.pancosky.newcartrade.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 订单控制器
 * 描述：提供订单创建、列表、详情、状态变更（确认/取消/支付保证金/完成）、纠纷、合同管理、
 *       终止交易申请、订单日志等完整流程接口。
 * 基础路径：/api/v1/orders
 * 认证要求：所有订单接口必须登录；用户仅能操作自己作为买家或卖家的订单。
 */
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 订单列表
     * HTTP: GET /api/v1/orders?type=BUY&status=PAID
     * 请求参数：type（BUY 作为买家、SELL 作为卖家）、status（订单状态过滤）。
     * 响应：ApiResponse&lt;List&lt;OrderVO&gt;&gt; —— 符合条件的订单列表。
     * 认证要求：必须登录。
     */
    @GetMapping
    public ApiResponse<List<OrderVO>> list(@RequestParam(required = false) String type,
                                           @RequestParam(required = false) String status) {
        return ApiResponse.success(orderService.list(type, status));
    }

    /**
     * 订单详情
     * HTTP: GET /api/v1/orders/{id}
     * 请求参数：id（path，订单号）
     * 响应：ApiResponse&lt;OrderDetailVO&gt; —— 完整订单信息，含车源、金额、合同、纠纷、保证金状态。
     * 认证要求：必须登录；仅买卖双方及管理员可见。
     */
    @GetMapping("/{id}")
    public ApiResponse<OrderDetailVO> detail(@PathVariable String id) {
        return ApiResponse.success(orderService.detail(id));
    }

    /**
     * 创建订单（一口价车源下单）
     * HTTP: POST /api/v1/orders
     * 请求参数：OrderCreateDTO（JSON body）—— carId、depositAmount、remark。
     * 响应：ApiResponse&lt;OrderVO&gt; —— 创建后返回订单基本信息。
     * 认证要求：必须登录；下单人必须是买家且不得为发布者本人。
     */
    @PostMapping
    public ApiResponse<OrderVO> create(@RequestBody OrderCreateDTO dto) {
        return ApiResponse.success(orderService.create(dto));
    }

    /**
     * 确认订单（同意/确认条款）
     * HTTP: PUT /api/v1/orders/{id}/confirm
     * 请求参数：id（path）
     * 响应：ApiResponse&lt;Void&gt;
     * 认证要求：必须登录，买家/卖家角色按业务场景决定。
     */
    @PutMapping("/{id}/confirm")
    public ApiResponse<Void> confirm(@PathVariable String id) {
        orderService.confirm(id);
        return ApiResponse.success();
    }

    /**
     * 取消订单
     * HTTP: PUT /api/v1/orders/{id}/cancel
     * 请求参数：id（path）
     * 响应：ApiResponse&lt;Void&gt;
     * 认证要求：必须登录；不同状态下对可取消角色有不同限制。
     */
    @PutMapping("/{id}/cancel")
    public ApiResponse<Void> cancel(@PathVariable String id) {
        orderService.cancel(id);
        return ApiResponse.success();
    }

    /**
     * 支付保证金
     * HTTP: PUT /api/v1/orders/{id}/pay-deposit
     * 请求参数：id（path）
     * 响应：ApiResponse&lt;Void&gt;
     * 认证要求：必须登录（买家）；将从保证金账户冻结或扣款。
     */
    @PutMapping("/{id}/pay-deposit")
    public ApiResponse<Void> payDeposit(@PathVariable String id) {
        orderService.payDeposit(id);
        return ApiResponse.success();
    }

    /**
     * 完成订单
     * HTTP: PUT /api/v1/orders/{id}/complete
     * 请求参数：id（path）
     * 响应：ApiResponse&lt;Void&gt;
     * 认证要求：必须登录；标记订单完成，保证金结算或退还。
     */
    @PutMapping("/{id}/complete")
    public ApiResponse<Void> complete(@PathVariable String id) {
        orderService.complete(id);
        return ApiResponse.success();
    }

    /**
     * 发起纠纷
     * HTTP: POST /api/v1/orders/{id}/dispute
     * 请求参数：id（path）
     * 响应：ApiResponse&lt;Void&gt;
     * 认证要求：必须登录；一般由买家或卖家任一方对交易结果不满时发起；需要上传证据由客服裁定。
     */
    @PostMapping("/{id}/dispute")
    public ApiResponse<Void> dispute(@PathVariable String id) {
        orderService.dispute(id);
        return ApiResponse.success();
    }

    /* ==========================
     * 合同相关接口
     * ========================== */

    /**
     * 提交合同内容
     * HTTP: POST /api/v1/orders/{id}/contract
     * 请求参数：id（path，订单ID）；body.content（字符串，合同正文或合同文件URL）。
     * 响应：ApiResponse&lt;Void&gt;
     * 认证要求：必须登录；由卖家或平台发起。
     */
    @PostMapping("/{id}/contract")
    public ApiResponse<Void> submitContract(@PathVariable String id, @RequestBody Map<String, String> body) {
        orderService.submitContract(id, body.get("content"));
        return ApiResponse.success();
    }

    /**
     * 修改合同内容
     * HTTP: PUT /api/v1/orders/{id}/contract
     * 请求参数：id（path）；body.content（新的合同内容）。
     * 响应：ApiResponse&lt;Void&gt;
     * 认证要求：必须登录；仅在双方均未确认前可修改。
     */
    @PutMapping("/{id}/contract")
    public ApiResponse<Void> updateContract(@PathVariable String id, @RequestBody Map<String, String> body) {
        orderService.updateContract(id, body.get("content"));
        return ApiResponse.success();
    }

    /**
     * 确认签署合同
     * HTTP: PUT /api/v1/orders/{id}/contract/confirm
     * 请求参数：id（path）
     * 响应：ApiResponse&lt;Void&gt;
     * 认证要求：必须登录；买家/卖家按角色分别确认。
     */
    @PutMapping("/{id}/contract/confirm")
    public ApiResponse<Void> confirmContract(@PathVariable String id) {
        orderService.confirmContract(id);
        return ApiResponse.success();
    }

    /**
     * 查看合同
     * HTTP: GET /api/v1/orders/{id}/contract
     * 请求参数：id（path）
     * 响应：ApiResponse&lt;Map&lt;String, Object&gt;&gt; —— 合同内容、签署状态、历史版本等。
     * 认证要求：必须登录；仅订单买卖双方及管理员可见。
     */
    @GetMapping("/{id}/contract")
    public ApiResponse<Map<String, Object>> getContract(@PathVariable String id) {
        return ApiResponse.success(orderService.getContract(id));
    }

    /* ==========================
     * 终止交易接口
     * ========================== */

    /**
     * 申请终止交易
     * HTTP: POST /api/v1/orders/{id}/terminate
     * 请求参数：id（path）；body.reason（终止原因说明）。
     * 响应：ApiResponse&lt;Void&gt;
     * 认证要求：必须登录；买卖双方任一可申请；需对方同意或客服介入后生效。
     */
    @PostMapping("/{id}/terminate")
    public ApiResponse<Void> terminate(@PathVariable String id, @RequestBody Map<String, String> body) {
        orderService.terminate(id, body.get("reason"));
        return ApiResponse.success();
    }

    /**
     * 查询终止交易申请次数
     * HTTP: GET /api/v1/orders/{id}/terminate/count
     * 请求参数：id（path）
     * 响应：ApiResponse&lt;Map&lt;String, Integer&gt;&gt; —— 当前订单终止申请次数（用于风控、防止滥用）。
     * 认证要求：必须登录；仅订单参与方可见。
     */
    @GetMapping("/{id}/terminate/count")
    public ApiResponse<Map<String, Integer>> getTerminateCount(@PathVariable String id) {
        return ApiResponse.success(orderService.getTerminateCount(id));
    }

    /* ==========================
     * 订单日志接口
     * ========================== */

    /**
     * 订单操作日志
     * HTTP: GET /api/v1/orders/{id}/logs
     * 请求参数：id（path）
     * 响应：ApiResponse&lt;List&lt;Object&gt;&gt; —— 订单创建、状态变更、合同、支付、纠纷等全量操作记录。
     * 认证要求：必须登录；仅订单参与方及管理员可见。
     */
    @GetMapping("/{id}/logs")
    public ApiResponse<List<Object>> logs(@PathVariable String id) {
        return ApiResponse.success(orderService.logs(id));
    }
}
