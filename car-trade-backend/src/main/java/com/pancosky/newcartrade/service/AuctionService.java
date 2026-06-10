package com.pancosky.newcartrade.service;

import com.pancosky.newcartrade.common.PageResult;
import com.pancosky.newcartrade.dto.AuctionCreateDTO;
import com.pancosky.newcartrade.dto.AuctionQueryDTO;
import com.pancosky.newcartrade.dto.BidDTO;
import com.pancosky.newcartrade.vo.AuctionDetailVO;
import com.pancosky.newcartrade.vo.AuctionVO;
import com.pancosky.newcartrade.vo.BidRecordVO;

import java.math.BigDecimal;
import java.util.List;

public interface AuctionService {

    PageResult<AuctionVO> list(AuctionQueryDTO query);

    AuctionDetailVO detail(Long id);

    AuctionVO create(AuctionCreateDTO dto);

    void cancel(Long id);

    void watch(Long auctionId);

    void unwatch(Long auctionId);

    BidRecordVO placeBid(BidDTO dto);

    List<BidRecordVO> getBidRecords(Long auctionId, Integer page, Integer size);

    AuctionDetailVO getDetailForBid(Long auctionId);

    void settleAuction(Long auctionId);

    BigDecimal getCurrentPrice(Long auctionId);
}