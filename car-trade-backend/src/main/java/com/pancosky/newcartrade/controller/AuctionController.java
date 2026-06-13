package com.pancosky.newcartrade.controller;
import com.pancosky.newcartrade.common.RequiresAuth;
import com.pancosky.newcartrade.common.AuthLevel;

import com.pancosky.newcartrade.common.ApiResponse;
import com.pancosky.newcartrade.common.PageResult;
import com.pancosky.newcartrade.dto.AuctionCreateDTO;
import com.pancosky.newcartrade.dto.AuctionQueryDTO;
import com.pancosky.newcartrade.dto.BidDTO;
import com.pancosky.newcartrade.service.AuctionService;
import com.pancosky.newcartrade.vo.AuctionDetailVO;
import com.pancosky.newcartrade.vo.AuctionVO;
import com.pancosky.newcartrade.vo.BidRecordVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 拍卖活动控制器
 * 描述：提供拍卖列表、详情、创建、取消、关注、出价、出价记录、当前价及结算接口。
 * 基础路径：/api/v1/auctions
 * 认证要求：列表、详情、当前价公开；创建、取消、关注、出价需登录。
 * 数据安全：出价需冻结保证金，活动结束后自动解冻或划转。
 */
@RestController
@RequestMapping("/api/v1/auctions")
@RequiredArgsConstructor
@RequiresAuth(AuthLevel.PROTECTED)
public class AuctionController {

    private final AuctionService auctionService;

    /**
     * 分页查询拍卖活动列表
     * HTTP: GET /api/v1/auctions
     * 请求参数：AuctionQueryDTO —— page、size、keyword、status（PENDING/RUNNING/ENDED/CANCELLED）、
     *         carId、sellerId、startTimeBegin、startTimeEnd、sort、order。
     * 响应：ApiResponse&lt;PageResult&lt;AuctionVO&gt;&gt;
     * 认证要求：公开。
     */
    @GetMapping
    @RequiresAuth(AuthLevel.PUBLIC)
    public ApiResponse<PageResult<AuctionVO>> list(AuctionQueryDTO query) {
        return ApiResponse.success(auctionService.list(query));
    }

    /**
     * 拍卖活动详情
     * HTTP: GET /api/v1/auctions/{id}
     * 请求参数：id（path，拍卖活动ID）
     * 响应：ApiResponse&lt;AuctionDetailVO&gt; —— 包含车源、起止时间、出价阶梯、当前最高价、关注人数等。
     * 认证要求：公开。
     */
    @GetMapping("/{id}")
    @RequiresAuth(AuthLevel.PUBLIC)
    public ApiResponse<AuctionDetailVO> detail(@PathVariable Long id) {
        return ApiResponse.success(auctionService.detail(id));
    }

    /**
     * 创建拍卖活动
     * HTTP: POST /api/v1/auctions
     * 请求参数：AuctionCreateDTO（JSON body）—— carId、startPrice、reservePrice、bidIncrement、
     *         startTime、endTime。
     * 响应：ApiResponse&lt;AuctionVO&gt; —— 创建成功后返回活动信息。
     * 认证要求：必须登录，且必须是车源的发布者或管理员。
     */
    @PostMapping
    public ApiResponse<AuctionVO> create(@Valid @RequestBody AuctionCreateDTO dto) {
        return ApiResponse.success(auctionService.create(dto));
    }

    /**
     * 取消拍卖活动
     * HTTP: POST /api/v1/auctions/{id}/cancel
     * 请求参数：id（path，拍卖活动ID）
     * 响应：ApiResponse&lt;Void&gt;
     * 认证要求：必须登录；必须是活动创建者或管理员；活动进行中取消需退还保证金并通知关注用户。
     */
    @PostMapping("/{id}/cancel")
    public ApiResponse<Void> cancel(@PathVariable Long id) {
        auctionService.cancel(id);
        return ApiResponse.success();
    }

    /**
     * 关注拍卖活动
     * HTTP: POST /api/v1/auctions/{id}/watch
     * 请求参数：id（path，拍卖活动ID）
     * 响应：ApiResponse&lt;Void&gt;
     * 认证要求：必须登录。
     */
    @PostMapping("/{id}/watch")
    public ApiResponse<Void> watch(@PathVariable Long id) {
        auctionService.watch(id);
        return ApiResponse.success();
    }

    /**
     * 取消关注拍卖活动
     * HTTP: DELETE /api/v1/auctions/{id}/watch
     * 请求参数：id（path，拍卖活动ID）
     * 响应：ApiResponse&lt;Void&gt;
     * 认证要求：必须登录。
     */
    @DeleteMapping("/{id}/watch")
    public ApiResponse<Void> unwatch(@PathVariable Long id) {
        auctionService.unwatch(id);
        return ApiResponse.success();
    }

    /**
     * 提交出价
     * HTTP: POST /api/v1/auctions/bids
     * 请求参数：BidDTO（JSON body）—— auctionId、bidPrice。
     * 响应：ApiResponse&lt;BidRecordVO&gt; —— 成功后返回出价记录。
     * 认证要求：必须登录；用户必须已冻结保证金且出价不低于当前最高价 + 加价幅度。
     * 限流：单用户每次最低出价间隔 3 秒。
     */
    @PostMapping("/bids")
    public ApiResponse<BidRecordVO> placeBid(@Valid @RequestBody BidDTO dto) {
        return ApiResponse.success(auctionService.placeBid(dto));
    }

    /**
     * 查询出价记录（按拍卖活动）
     * HTTP: GET /api/v1/auctions/{id}/bids?page=1&size=20
     * 请求参数：id（path）；page、size（query）。
     * 响应：ApiResponse&lt;List&lt;BidRecordVO&gt;&gt; —— 按时间倒序的出价历史。
     * 认证要求：公开（出价人信息可能脱敏）。
     */
    @GetMapping("/{id}/bids")
    public ApiResponse<List<BidRecordVO>> bidRecords(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return ApiResponse.success(auctionService.getBidRecords(id, page, size));
    }

    /**
     * 查询当前最高价
     * HTTP: GET /api/v1/auctions/{id}/current-price
     * 请求参数：id（path）
     * 响应：ApiResponse&lt;BigDecimal&gt; —— 当前最高价；若未出价返回起拍价。
     * 认证要求：公开。
     */
    @GetMapping("/{id}/current-price")
    @RequiresAuth(AuthLevel.PUBLIC)
    public ApiResponse<BigDecimal> currentPrice(@PathVariable Long id) {
        return ApiResponse.success(auctionService.getCurrentPrice(id));
    }

    /**
     * 结算拍卖活动
     * HTTP: POST /api/v1/auctions/{id}/settle
     * 请求参数：id（path）
     * 响应：ApiResponse&lt;Void&gt;
     * 认证要求：必须登录；仅活动创建者、管理员或后台任务可调用；将生成订单并通知买家。
     */
    @PostMapping("/{id}/settle")
    public ApiResponse<Void> settle(@PathVariable Long id) {
        auctionService.settleAuction(id);
        return ApiResponse.success();
    }
}
