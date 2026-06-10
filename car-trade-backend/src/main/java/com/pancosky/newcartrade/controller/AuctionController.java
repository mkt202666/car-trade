package com.pancosky.newcartrade.controller;

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

@RestController
@RequestMapping("/api/v1/auctions")
@RequiredArgsConstructor
public class AuctionController {

    private final AuctionService auctionService;

    @GetMapping
    public ApiResponse<PageResult<AuctionVO>> list(AuctionQueryDTO query) {
        return ApiResponse.success(auctionService.list(query));
    }

    @GetMapping("/{id}")
    public ApiResponse<AuctionDetailVO> detail(@PathVariable Long id) {
        return ApiResponse.success(auctionService.detail(id));
    }

    @PostMapping
    public ApiResponse<AuctionVO> create(@Valid @RequestBody AuctionCreateDTO dto) {
        return ApiResponse.success(auctionService.create(dto));
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<Void> cancel(@PathVariable Long id) {
        auctionService.cancel(id);
        return ApiResponse.success();
    }

    @PostMapping("/{id}/watch")
    public ApiResponse<Void> watch(@PathVariable Long id) {
        auctionService.watch(id);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}/watch")
    public ApiResponse<Void> unwatch(@PathVariable Long id) {
        auctionService.unwatch(id);
        return ApiResponse.success();
    }

    @PostMapping("/bids")
    public ApiResponse<BidRecordVO> placeBid(@Valid @RequestBody BidDTO dto) {
        return ApiResponse.success(auctionService.placeBid(dto));
    }

    @GetMapping("/{id}/bids")
    public ApiResponse<List<BidRecordVO>> bidRecords(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return ApiResponse.success(auctionService.getBidRecords(id, page, size));
    }

    @GetMapping("/{id}/current-price")
    public ApiResponse<BigDecimal> currentPrice(@PathVariable Long id) {
        return ApiResponse.success(auctionService.getCurrentPrice(id));
    }

    @PostMapping("/{id}/settle")
    public ApiResponse<Void> settle(@PathVariable Long id) {
        auctionService.settleAuction(id);
        return ApiResponse.success();
    }
}
