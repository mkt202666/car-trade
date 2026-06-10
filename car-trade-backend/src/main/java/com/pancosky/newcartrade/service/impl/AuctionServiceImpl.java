package com.pancosky.newcartrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pancosky.newcartrade.common.PageResult;
import com.pancosky.newcartrade.dto.AuctionCreateDTO;
import com.pancosky.newcartrade.dto.AuctionQueryDTO;
import com.pancosky.newcartrade.dto.BidDTO;
import com.pancosky.newcartrade.entity.*;
import com.pancosky.newcartrade.enums.AuctionStatus;
import com.pancosky.newcartrade.exception.BusinessException;
import com.pancosky.newcartrade.mapper.*;
import com.pancosky.newcartrade.service.AuctionService;
import com.pancosky.newcartrade.util.SecurityUtils;
import com.pancosky.newcartrade.vo.AuctionDetailVO;
import com.pancosky.newcartrade.vo.AuctionVO;
import com.pancosky.newcartrade.vo.BidRecordVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionServiceImpl implements AuctionService {

    private final AuctionMapper auctionMapper;
    private final AuctionBidMapper auctionBidMapper;
    private final AuctionWatchMapper auctionWatchMapper;
    private final CarMapper carMapper;
    private final UserMapper userMapper;
    private final CarImageMapper carImageMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String BID_LOCK_PREFIX = "auction:bid:lock:";

    @Override
    public PageResult<AuctionVO> list(AuctionQueryDTO query) {
        LambdaQueryWrapper<Auction> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(Auction::getStatus, AuctionStatus.valueOf(query.getStatus()));
        }
        if (query.getCarId() != null) wrapper.eq(Auction::getCarId, query.getCarId());
        if (query.getSellerId() != null) wrapper.eq(Auction::getSellerId, query.getSellerId());

        if ("endTime".equals(query.getSort())) {
            wrapper.orderByDesc(Auction::getEndTime);
        } else {
            wrapper.orderByDesc(Auction::getStartTime);
        }

        int page = query.getPage() == null ? 1 : query.getPage();
        int size = query.getSize() == null ? 10 : query.getSize();
        Page<Auction> pageResult = auctionMapper.selectPage(new Page<>(page, size), wrapper);

        List<AuctionVO> vos = pageResult.getRecords().stream()
                .map(this::toAuctionVO)
                .collect(Collectors.toList());
        return PageResult.of(vos, pageResult.getTotal(), page, size);
    }

    @Override
    public AuctionDetailVO detail(Long id) {
        Auction auction = auctionMapper.selectById(id);
        if (auction == null) throw new BusinessException("拍卖不存在");

        // 增加浏览次数
        auction.setViewCount(auction.getViewCount() + 1);
        auctionMapper.updateById(auction);

        return toDetailVO(auction);
    }

    @Override
    @Transactional
    public AuctionVO create(AuctionCreateDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) throw new BusinessException("请先登录");

        CarSource car = carMapper.selectById(dto.getCarId());
        if (car == null) throw new BusinessException("车源不存在");
        if (!car.getUserId().equals(userId)) throw new BusinessException("只能为自己的车源创建拍卖");
        if (dto.getEndTime().isBefore(dto.getStartTime())) throw new BusinessException("结束时间必须晚于开始时间");

        Auction auction = new Auction();
        auction.setCarId(dto.getCarId());
        auction.setSellerId(userId);
        auction.setStartPrice(dto.getStartPrice());
        auction.setReservePrice(dto.getReservePrice());
        auction.setCurrentPrice(dto.getStartPrice());
        auction.setBidIncrement(dto.getBidIncrement());
        auction.setStartTime(dto.getStartTime());
        auction.setEndTime(dto.getEndTime());
        auction.setTotalBids(0);
        auction.setViewCount(0L);
        auction.setVersion(0);

        // 根据时间设置状态
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(dto.getStartTime())) {
            auction.setStatus(AuctionStatus.PENDING);
        } else {
            auction.setStatus(AuctionStatus.BIDDING);
        }

        auctionMapper.insert(auction);
        log.info("User {} created auction {} for car {}", userId, auction.getId(), dto.getCarId());
        return toAuctionVO(auction);
    }

    @Override
    @Transactional
    public void cancel(Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        Auction auction = auctionMapper.selectById(id);
        if (auction == null) throw new BusinessException("拍卖不存在");
        if (!auction.getSellerId().equals(userId)) throw new BusinessException("只能取消自己的拍卖");
        if (auction.getStatus() == AuctionStatus.ENDED || auction.getStatus() == AuctionStatus.SETTLED) {
            throw new BusinessException("已结束的拍卖不能取消");
        }
        auction.setStatus(AuctionStatus.CANCELLED);
        auction.setActualEndTime(LocalDateTime.now());
        auctionMapper.updateById(auction);
        log.info("User {} cancelled auction {}", userId, id);
    }

    @Override
    @Transactional
    public void watch(Long auctionId) {
        Long userId = SecurityUtils.getCurrentUserId();
        LambdaQueryWrapper<AuctionWatch> existWrapper = new LambdaQueryWrapper<>();
        existWrapper.eq(AuctionWatch::getAuctionId, auctionId).eq(AuctionWatch::getUserId, userId);
        if (auctionWatchMapper.selectCount(existWrapper) > 0) return;

        AuctionWatch watch = new AuctionWatch();
        watch.setAuctionId(auctionId);
        watch.setUserId(userId);
        auctionWatchMapper.insert(watch);
    }

    @Override
    @Transactional
    public void unwatch(Long auctionId) {
        Long userId = SecurityUtils.getCurrentUserId();
        LambdaQueryWrapper<AuctionWatch> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuctionWatch::getAuctionId, auctionId).eq(AuctionWatch::getUserId, userId);
        auctionWatchMapper.delete(wrapper);
    }

    @Override
    @Transactional
    public BidRecordVO placeBid(BidDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) throw new BusinessException("请先登录");

        // 分布式锁防止并发出价
        String lockKey = BID_LOCK_PREFIX + dto.getAuctionId();
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, userId.toString(), 10, TimeUnit.SECONDS);
        if (!Boolean.TRUE.equals(locked)) {
            throw new BusinessException("系统繁忙，请稍后重试");
        }

        try {
            Auction auction = auctionMapper.selectById(dto.getAuctionId());
            if (auction == null) throw new BusinessException("拍卖不存在");
            if (auction.getStatus() != AuctionStatus.BIDDING) throw new BusinessException("拍卖未在竞拍中");
            if (LocalDateTime.now().isAfter(auction.getEndTime())) throw new BusinessException("拍卖已结束");
            if (auction.getSellerId().equals(userId)) throw new BusinessException("不能竞拍自己的商品");

            BigDecimal minBid = auction.getCurrentPrice().add(auction.getBidIncrement());
            if (dto.getBidPrice().compareTo(minBid) < 0) {
                throw new BusinessException("出价不能低于 " + minBid);
            }

            // 将之前的最高出价标记取消
            LambdaQueryWrapper<AuctionBid> winWrapper = new LambdaQueryWrapper<>();
            winWrapper.eq(AuctionBid::getAuctionId, dto.getAuctionId())
                      .eq(AuctionBid::getIsWinning, true);
            AuctionBid prevWinner = auctionBidMapper.selectOne(winWrapper);
            if (prevWinner != null) {
                prevWinner.setIsWinning(false);
                auctionBidMapper.updateById(prevWinner);
            }

            // 记录新出价
            AuctionBid bid = new AuctionBid();
            bid.setAuctionId(dto.getAuctionId());
            bid.setBidderId(userId);
            bid.setBidPrice(dto.getBidPrice());
            bid.setBidTime(LocalDateTime.now());
            bid.setIsWinning(true);
            auctionBidMapper.insert(bid);

            // 更新拍卖当前价格和出价次数
            auction.setCurrentPrice(dto.getBidPrice());
            auction.setTotalBids(auction.getTotalBids() + 1);
            auctionMapper.updateById(auction);

            log.info("User {} bid {} on auction {}", userId, dto.getBidPrice(), dto.getAuctionId());
            return toBidRecordVO(bid);
        } finally {
            redisTemplate.delete(lockKey);
        }
    }

    @Override
    public List<BidRecordVO> getBidRecords(Long auctionId, Integer page, Integer size) {
        LambdaQueryWrapper<AuctionBid> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuctionBid::getAuctionId, auctionId)
               .orderByDesc(AuctionBid::getBidTime)
               .last("LIMIT " + (size == null ? 20 : size) + " OFFSET " + ((page == null ? 1 : page) - 1) * (size == null ? 20 : size));
        List<AuctionBid> bids = auctionBidMapper.selectList(wrapper);
        return bids.stream().map(this::toBidRecordVO).collect(Collectors.toList());
    }

    @Override
    public AuctionDetailVO getDetailForBid(Long auctionId) {
        return detail(auctionId);
    }

    @Override
    @Transactional
    public void settleAuction(Long auctionId) {
        Auction auction = auctionMapper.selectById(auctionId);
        if (auction == null) throw new BusinessException("拍卖不存在");
        if (auction.getStatus() != AuctionStatus.ENDED) throw new BusinessException("拍卖未结束");

        // 查找最高出价者
        LambdaQueryWrapper<AuctionBid> winWrapper = new LambdaQueryWrapper<>();
        winWrapper.eq(AuctionBid::getAuctionId, auctionId).eq(AuctionBid::getIsWinning, true);
        AuctionBid winner = auctionBidMapper.selectOne(winWrapper);

        if (winner != null) {
            // 检查是否达到保留价
            if (auction.getReservePrice() != null && winner.getBidPrice().compareTo(auction.getReservePrice()) < 0) {
                auction.setStatus(AuctionStatus.FAILED);
                log.info("Auction {} failed: bid {} below reserve {}", auctionId, winner.getBidPrice(), auction.getReservePrice());
            } else {
                auction.setWinnerId(winner.getBidderId());
                auction.setWinningPrice(winner.getBidPrice());
                auction.setStatus(AuctionStatus.SETTLED);
                log.info("Auction {} settled: winner={}, price={}", auctionId, winner.getBidderId(), winner.getBidPrice());
            }
        } else {
            auction.setStatus(AuctionStatus.FAILED);
            log.info("Auction {} failed: no bids", auctionId);
        }

        auction.setActualEndTime(LocalDateTime.now());
        auctionMapper.updateById(auction);
    }

    @Override
    public BigDecimal getCurrentPrice(Long auctionId) {
        Auction auction = auctionMapper.selectById(auctionId);
        if (auction == null) throw new BusinessException("拍卖不存在");
        return auction.getCurrentPrice();
    }

    // ==================== helpers ====================

    private AuctionVO toAuctionVO(Auction auction) {
        AuctionVO vo = new AuctionVO();
        vo.setId(auction.getId());
        vo.setCarId(auction.getCarId());
        vo.setStartPrice(auction.getStartPrice());
        vo.setCurrentPrice(auction.getCurrentPrice());
        vo.setBidIncrement(auction.getBidIncrement());
        vo.setStartTime(auction.getStartTime());
        vo.setEndTime(auction.getEndTime());
        vo.setStatus(auction.getStatus());
        vo.setSellerId(auction.getSellerId());
        vo.setTotalBids(auction.getTotalBids());
        vo.setViewCount(auction.getViewCount());
        vo.setCreatedAt(auction.getCreatedAt());

        // 填充车源信息
        CarSource car = carMapper.selectById(auction.getCarId());
        if (car != null) {
            vo.setCarName(car.getTitle());
            vo.setCarYear(car.getYear());
            vo.setCarMileage(car.getMileage());
            vo.setCity(car.getCityName());
        }

        // 填充卖家信息
        User seller = userMapper.selectById(auction.getSellerId());
        if (seller != null) {
            vo.setSellerName(seller.getNickname());
            vo.setSellerAvatar(seller.getAvatarUrl());
        }

        // 检查当前用户是否关注
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId != null) {
            LambdaQueryWrapper<AuctionWatch> watchWrapper = new LambdaQueryWrapper<>();
            watchWrapper.eq(AuctionWatch::getAuctionId, auction.getId())
                        .eq(AuctionWatch::getUserId, currentUserId);
            vo.setIsWatching(auctionWatchMapper.selectCount(watchWrapper) > 0);
            vo.setCanBid(!auction.getSellerId().equals(currentUserId)
                    && auction.getStatus() == AuctionStatus.BIDDING
                    && LocalDateTime.now().isBefore(auction.getEndTime()));
        }

        return vo;
    }

    private AuctionDetailVO toDetailVO(Auction auction) {
        AuctionDetailVO vo = new AuctionDetailVO();
        vo.setId(auction.getId());
        vo.setCarId(auction.getCarId());
        vo.setStartPrice(auction.getStartPrice());
        vo.setReservePrice(auction.getReservePrice());
        vo.setCurrentPrice(auction.getCurrentPrice());
        vo.setBidIncrement(auction.getBidIncrement());
        vo.setStartTime(auction.getStartTime());
        vo.setEndTime(auction.getEndTime());
        vo.setStatus(auction.getStatus());
        vo.setSellerId(auction.getSellerId());
        vo.setTotalBids(auction.getTotalBids());
        vo.setViewCount(auction.getViewCount());
        vo.setWinnerId(auction.getWinnerId());
        vo.setWinningPrice(auction.getWinningPrice());
        vo.setActualEndTime(auction.getActualEndTime());
        vo.setCreatedAt(auction.getCreatedAt());

        // 车源信息
        CarSource car = carMapper.selectById(auction.getCarId());
        if (car != null) {
            vo.setCarName(car.getTitle());
            vo.setCarYear(car.getYear());
            vo.setCarMileage(car.getMileage());
            vo.setCity(car.getCityName());
            List<CarImage> images = carImageMapper.selectList(
                    new LambdaQueryWrapper<CarImage>().eq(CarImage::getCarId, car.getId()));
            vo.setCarImages(images.stream().map(CarImage::getImageUrl).collect(Collectors.toList()));
        }

        // 卖家信息
        User seller = userMapper.selectById(auction.getSellerId());
        if (seller != null) {
            vo.setSellerName(seller.getNickname());
            vo.setSellerAvatar(seller.getAvatarUrl());
            vo.setSellerShopName(seller.getShopName());
        }

        // 最近出价记录
        vo.setRecentBids(getBidRecords(auction.getId(), 1, 10));

        // 当前用户状态
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId != null) {
            LambdaQueryWrapper<AuctionWatch> watchWrapper = new LambdaQueryWrapper<>();
            watchWrapper.eq(AuctionWatch::getAuctionId, auction.getId())
                        .eq(AuctionWatch::getUserId, currentUserId);
            vo.setIsWatching(auctionWatchMapper.selectCount(watchWrapper) > 0);
            vo.setCanBid(!auction.getSellerId().equals(currentUserId)
                    && auction.getStatus() == AuctionStatus.BIDDING
                    && LocalDateTime.now().isBefore(auction.getEndTime()));
        }

        return vo;
    }

    private BidRecordVO toBidRecordVO(AuctionBid bid) {
        BidRecordVO vo = new BidRecordVO();
        vo.setId(bid.getId());
        vo.setAuctionId(bid.getAuctionId());
        vo.setBidderId(bid.getBidderId());
        vo.setBidPrice(bid.getBidPrice());
        vo.setBidTime(bid.getBidTime());
        vo.setIsWinning(bid.getIsWinning());

        User bidder = userMapper.selectById(bid.getBidderId());
        if (bidder != null) {
            vo.setBidderName(bidder.getNickname());
            vo.setBidderAvatar(bidder.getAvatarUrl());
        }
        return vo;
    }
}
