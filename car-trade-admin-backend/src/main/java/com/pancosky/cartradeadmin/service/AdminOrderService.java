package com.pancosky.cartradeadmin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pancosky.cartradeadmin.common.BusinessException;
import com.pancosky.cartradeadmin.common.PageResult;
import com.pancosky.cartradeadmin.dto.OrderQueryDTO;
import com.pancosky.cartradeadmin.entity.AppCarSource;
import com.pancosky.cartradeadmin.entity.AppCarInspection;
import com.pancosky.cartradeadmin.entity.AppCarImage;
import com.pancosky.cartradeadmin.entity.AdminCarBrand;
import com.pancosky.cartradeadmin.entity.AdminCarSeries;
import com.pancosky.cartradeadmin.entity.AdminCarModel;
import com.pancosky.cartradeadmin.entity.AppOrder;
import com.pancosky.cartradeadmin.entity.AppOrderLog;
import com.pancosky.cartradeadmin.entity.AppUser;
import com.pancosky.cartradeadmin.entity.AdminUser;
import com.pancosky.cartradeadmin.event.MobileNotification;
import com.pancosky.cartradeadmin.mapper.AppCarSourceMapper;
import com.pancosky.cartradeadmin.mapper.AppCarInspectionMapper;
import com.pancosky.cartradeadmin.mapper.AppCarImageMapper;
import com.pancosky.cartradeadmin.mapper.AdminCarBrandMapper;
import com.pancosky.cartradeadmin.mapper.AdminCarSeriesMapper;
import com.pancosky.cartradeadmin.mapper.AdminCarModelMapper;
import com.pancosky.cartradeadmin.mapper.AppOrderLogMapper;
import com.pancosky.cartradeadmin.mapper.AppOrderMapper;
import com.pancosky.cartradeadmin.mapper.AppUserMapper;
import com.pancosky.cartradeadmin.mapper.AdminUserMapper;
import com.pancosky.cartradeadmin.vo.OrderDetailVO;
import com.pancosky.cartradeadmin.vo.OrderLogVO;
import com.pancosky.cartradeadmin.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminOrderService {

    @Autowired
    private AppOrderMapper appOrderMapper;

    @Autowired
    private AppCarSourceMapper appCarSourceMapper;

    @Autowired
    private AppCarInspectionMapper appCarInspectionMapper;

    @Autowired
    private AppCarImageMapper appCarImageMapper;

    @Autowired
    private AdminCarBrandMapper adminCarBrandMapper;

    @Autowired
    private AdminCarSeriesMapper adminCarSeriesMapper;

    @Autowired
    private AdminCarModelMapper adminCarModelMapper;

    @Autowired
    private AppUserMapper appUserMapper;

    @Autowired
    private AppOrderLogMapper appOrderLogMapper;

    @Autowired
    private AdminUserMapper adminUserMapper;

    @Autowired
    private AdminNotificationService adminNotificationService;

    @Autowired
    private HttpServletRequest request;

    public PageResult<OrderVO> getOrderList(OrderQueryDTO query) {
        LambdaQueryWrapper<AppOrder> wrapper = new LambdaQueryWrapper<AppOrder>();

        // 关键字搜索：订单号或车源标题
        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            String kw = query.getKeyword();
            // 如果关键字是纯数字，可能是订单号或车源ID
            wrapper.and(w -> {
                w.eq(AppOrder::getId, kw);
                // 也需要关联车源表搜索标题，这里先简单处理
                // TODO: 如需搜索车源标题，需要自定义 SQL 关联查询
            });
        }

        if (query.getStatus() != null && !query.getStatus().isEmpty()) {
            wrapper.eq(AppOrder::getStatus, query.getStatus());
        }

        // 日期范围查询
        if (query.getStartDate() != null && !query.getStartDate().isEmpty()) {
            wrapper.ge(AppOrder::getCreatedAt, query.getStartDate());
        }
        if (query.getEndDate() != null && !query.getEndDate().isEmpty()) {
            wrapper.le(AppOrder::getCreatedAt, query.getEndDate() + " 23:59:59");
        }

        wrapper.orderByDesc(AppOrder::getCreatedAt);

        Page<AppOrder> page = appOrderMapper.selectPage(
                new Page<>(query.getPage(), query.getSize()), wrapper);

        // 批量查询关联数据，避免 N+1
        List<Long> carIds = page.getRecords().stream()
                .map(AppOrder::getCarId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        List<Long> buyerIds = page.getRecords().stream()
                .map(AppOrder::getBuyerId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        List<Long> sellerIds = page.getRecords().stream()
                .map(AppOrder::getSellerId).filter(Objects::nonNull).distinct().collect(Collectors.toList());

        Map<Long, AppCarSource> carMap = carIds.isEmpty() ? Collections.emptyMap()
                : appCarSourceMapper.selectBatchIds(carIds).stream()
                        .collect(Collectors.toMap(AppCarSource::getId, c -> c));
        Map<Long, AppUser> buyerMap = buyerIds.isEmpty() ? Collections.emptyMap()
                : appUserMapper.selectBatchIds(buyerIds).stream()
                        .collect(Collectors.toMap(AppUser::getId, u -> u));
        Map<Long, AppUser> sellerMap = sellerIds.isEmpty() ? Collections.emptyMap()
                : appUserMapper.selectBatchIds(sellerIds).stream()
                        .collect(Collectors.toMap(AppUser::getId, u -> u));

        List<OrderVO> voList = page.getRecords().stream().map(order -> {
            OrderVO vo = new OrderVO();
            vo.setId(order.getId());
            vo.setCarId(order.getCarId());
            vo.setTotalPrice(order.getTotalPrice());
            vo.setDepositAmount(order.getDepositAmount());
            vo.setStatus(order.getStatus());
            vo.setCreatedAt(order.getCreatedAt());

            AppCarSource car = carMap.get(order.getCarId());
            if (car != null) {
                vo.setCarTitle(car.getTitle());
            }

            AppUser buyer = buyerMap.get(order.getBuyerId());
            if (buyer != null) {
                vo.setBuyerName(buyer.getNickname());
            }

            AppUser seller = sellerMap.get(order.getSellerId());
            if (seller != null) {
                vo.setSellerName(seller.getNickname());
            }

            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(voList, page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }

    public List<OrderVO> getOrderExportList(OrderQueryDTO query) {
        LambdaQueryWrapper<AppOrder> wrapper = new LambdaQueryWrapper<AppOrder>();

        // 关键字搜索：订单号或车源标题
        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            String kw = query.getKeyword();
            wrapper.and(w -> {
                w.eq(AppOrder::getId, kw);
            });
        }

        if (query.getStatus() != null && !query.getStatus().isEmpty()) {
            wrapper.eq(AppOrder::getStatus, query.getStatus());
        }

        // 日期范围查询
        if (query.getStartDate() != null && !query.getStartDate().isEmpty()) {
            wrapper.ge(AppOrder::getCreatedAt, query.getStartDate());
        }
        if (query.getEndDate() != null && !query.getEndDate().isEmpty()) {
            wrapper.le(AppOrder::getCreatedAt, query.getEndDate() + " 23:59:59");
        }

        wrapper.orderByDesc(AppOrder::getCreatedAt);

        List<AppOrder> orders = appOrderMapper.selectList(wrapper);

        // 批量查询关联数据，避免 N+1
        List<Long> carIds = orders.stream()
                .map(AppOrder::getCarId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        List<Long> buyerIds = orders.stream()
                .map(AppOrder::getBuyerId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        List<Long> sellerIds = orders.stream()
                .map(AppOrder::getSellerId).filter(Objects::nonNull).distinct().collect(Collectors.toList());

        Map<Long, AppCarSource> carMap = carIds.isEmpty() ? Collections.emptyMap()
                : appCarSourceMapper.selectBatchIds(carIds).stream()
                        .collect(Collectors.toMap(AppCarSource::getId, c -> c));
        Map<Long, AppUser> buyerMap = buyerIds.isEmpty() ? Collections.emptyMap()
                : appUserMapper.selectBatchIds(buyerIds).stream()
                        .collect(Collectors.toMap(AppUser::getId, u -> u));
        Map<Long, AppUser> sellerMap = sellerIds.isEmpty() ? Collections.emptyMap()
                : appUserMapper.selectBatchIds(sellerIds).stream()
                        .collect(Collectors.toMap(AppUser::getId, u -> u));

        return orders.stream().map(order -> {
            OrderVO vo = new OrderVO();
            vo.setId(order.getId());
            vo.setCarId(order.getCarId());
            vo.setTotalPrice(order.getTotalPrice());
            vo.setDepositAmount(order.getDepositAmount());
            vo.setStatus(order.getStatus());
            vo.setCreatedAt(order.getCreatedAt());

            AppCarSource car = carMap.get(order.getCarId());
            if (car != null) {
                vo.setCarTitle(car.getTitle());
            }

            AppUser buyer = buyerMap.get(order.getBuyerId());
            if (buyer != null) {
                vo.setBuyerName(buyer.getNickname());
            }

            AppUser seller = sellerMap.get(order.getSellerId());
            if (seller != null) {
                vo.setSellerName(seller.getNickname());
            }

            return vo;
        }).collect(Collectors.toList());
    }

    public OrderDetailVO getOrderDetail(String id) {
        AppOrder order = appOrderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }

        OrderDetailVO vo = new OrderDetailVO();
        vo.setId(order.getId());
        vo.setCarId(order.getCarId());
        vo.setBuyerId(order.getBuyerId());
        vo.setSellerId(order.getSellerId());
        vo.setTotalPrice(order.getTotalPrice());
        vo.setDepositAmount(order.getDepositAmount());
        vo.setStatus(order.getStatus());
        vo.setCreatedAt(order.getCreatedAt());
        vo.setUpdatedAt(order.getUpdatedAt());
        vo.setBuyerDepositPaid(order.getBuyerDepositPaid());
        vo.setSellerDepositPaid(order.getSellerDepositPaid());
        vo.setContractNo(order.getContractNo());
        vo.setContractSubmitted(order.getContractSubmitted());
        vo.setContractConfirmed(order.getContractConfirmed());
        vo.setRemark(order.getRemark());
        vo.setCancelReason(order.getCancelReason());
        vo.setCompletedAt(order.getCompletedAt());
        vo.setCancelledAt(order.getCancelledAt());

        // 查询车源信息
        if (order.getCarId() != null) {
            AppCarSource car = appCarSourceMapper.selectById(order.getCarId());
            if (car != null) {
                vo.setCarTitle(car.getTitle());
                vo.setCarName(car.getTitle());
                vo.setYear(car.getYear());
                vo.setMileage(car.getMileage());
                vo.setVin(car.getVin());
                vo.setColor(car.getColor());
                vo.setCity(car.getCityName());
                vo.setEnergyType(car.getEnergyType());
                vo.setTransmission(car.getTransmission());
                if (car.getRegistrationDate() != null) {
                    vo.setRegistrationDate(car.getRegistrationDate().toString());
                }
                if (car.getInsuranceExpiry() != null) {
                    vo.setInsuranceExpiry(car.getInsuranceExpiry().toString());
                }
                if (car.getInspectionExpiry() != null) {
                    vo.setInspectionExpiry(car.getInspectionExpiry().toString());
                }

                // 品牌名称
                if (car.getBrandId() != null) {
                    AdminCarBrand brand = adminCarBrandMapper.selectById(car.getBrandId());
                    if (brand != null) {
                        vo.setBrandName(brand.getName());
                    }
                }
                // 车系名称
                if (car.getSeriesId() != null) {
                    AdminCarSeries series = adminCarSeriesMapper.selectById(car.getSeriesId());
                    if (series != null) {
                        vo.setSeriesName(series.getName());
                    }
                }
                // 车型名称
                if (car.getModelId() != null) {
                    AdminCarModel model = adminCarModelMapper.selectById(car.getModelId());
                    if (model != null) {
                        vo.setModelName(model.getName());
                    }
                }

                // 车辆首张图片
                LambdaQueryWrapper<AppCarImage> imgWrapper = new LambdaQueryWrapper<AppCarImage>()
                        .eq(AppCarImage::getCarId, car.getId())
                        .orderByAsc(AppCarImage::getSortOrder)
                        .last("LIMIT 1");
                AppCarImage firstImage = appCarImageMapper.selectOne(imgWrapper);
                if (firstImage != null) {
                    vo.setCarImage(firstImage.getImageUrl());
                }

                // 检测信息
                LambdaQueryWrapper<AppCarInspection> inspWrapper = new LambdaQueryWrapper<AppCarInspection>()
                        .eq(AppCarInspection::getCarId, car.getId())
                        .orderByDesc(AppCarInspection::getCreatedAt)
                        .last("LIMIT 1");
                AppCarInspection inspection = appCarInspectionMapper.selectOne(inspWrapper);
                if (inspection != null) {
                    vo.setOverallCondition(inspection.getOverallCondition());
                    vo.setPaint(inspection.getPaint());
                    vo.setStructure(inspection.getStructure());
                    vo.setEngine(inspection.getEngine());
                    vo.setTransmission(inspection.getTransmission() != null
                            ? inspection.getTransmission() : car.getTransmission());
                    vo.setTransferCount(inspection.getTransferCount());
                    vo.setMileageType(inspection.getMileageType());
                    vo.setMaterials(inspection.getDescription());
                }
            }
        }

        // 查询买家信息
        if (order.getBuyerId() != null) {
            AppUser buyer = appUserMapper.selectById(order.getBuyerId());
            if (buyer != null) {
                vo.setBuyerName(buyer.getRealName() != null ? buyer.getRealName() : buyer.getNickname());
                vo.setBuyerPhone(maskPhone(buyer.getPhone()));
            }
        }

        // 查询卖家信息
        if (order.getSellerId() != null) {
            AppUser seller = appUserMapper.selectById(order.getSellerId());
            if (seller != null) {
                vo.setSellerName(seller.getRealName() != null ? seller.getRealName() : seller.getNickname());
                vo.setSellerPhone(maskPhone(seller.getPhone()));
            }
        }

        // 派生合同状态
        if (Boolean.TRUE.equals(order.getContractConfirmed())) {
            vo.setContractStatus("CONFIRMED");
        } else if (Boolean.TRUE.equals(order.getContractSubmitted())) {
            vo.setContractStatus("SUBMITTED");
        } else if (order.getContractNo() != null) {
            vo.setContractStatus("DRAFT");
        } else {
            vo.setContractStatus("NONE");
        }

        // 派生保证金状态
        boolean buyerPaid = Boolean.TRUE.equals(order.getBuyerDepositPaid());
        boolean sellerPaid = Boolean.TRUE.equals(order.getSellerDepositPaid());
        if (buyerPaid && sellerPaid) {
            vo.setDepositStatus("PAID");
        } else if (buyerPaid || sellerPaid) {
            vo.setDepositStatus("PARTIAL");
        } else {
            vo.setDepositStatus("UNPAID");
        }

        return vo;
    }

    public List<OrderLogVO> getOrderLogs(String orderId) {
        // 验证订单是否存在
        AppOrder order = appOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }

        LambdaQueryWrapper<AppOrderLog> wrapper = new LambdaQueryWrapper<AppOrderLog>()
                .eq(AppOrderLog::getOrderId, orderId)
                .orderByAsc(AppOrderLog::getCreatedAt);

        List<AppOrderLog> logs = appOrderLogMapper.selectList(wrapper);

        return logs.stream().map(log -> {
            OrderLogVO vo = new OrderLogVO();
            vo.setId(log.getId());
            vo.setOrderId(log.getOrderId());
            vo.setAction(log.getAction());
            vo.setDetail(log.getDetail());
            vo.setCreatedAt(log.getCreatedAt());

            // 查询操作人名称（优先查移动端用户表，找不到则查管理员表）
            if (log.getOperatorId() != null) {
                AppUser operator = appUserMapper.selectById(log.getOperatorId());
                if (operator != null) {
                    vo.setOperatorName(operator.getNickname());
                } else {
                    AdminUser adminOperator = adminUserMapper.selectById(log.getOperatorId());
                    if (adminOperator != null) {
                        vo.setOperatorName("[管理员] " + adminOperator.getNickname());
                    }
                }
            }

            return vo;
        }).collect(Collectors.toList());
    }

    // ==================== 运营端订单操作 ====================

    /**
     * 管理员确认订单（手动推进订单到下一状态）
     * 适用场景：线下核实保证金到账后，手动将 PENDING_DEPOSIT → CONTRACT_DRAFT
     */
    @Transactional(rollbackFor = Exception.class)
    public void confirmOrder(String orderId) {
        AppOrder order = appOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }

        String currentStatus = order.getStatus();
        if (!"PENDING_DEPOSIT".equals(currentStatus)) {
            throw new BusinessException(400, "当前订单状态不允许确认，当前状态: " + currentStatus);
        }

        String oldStatus = order.getStatus();
        order.setStatus("CONTRACT_DRAFT");
        appOrderMapper.updateById(order);

        // 记录操作日志
        Long adminId = getAdminId();
        logOrderAction(orderId, adminId, "ADMIN_CONFIRM",
                "管理员确认订单: " + oldStatus + " → CONTRACT_DRAFT");

        // 通知买家和卖家
        notifyOrderStatusChanged(order.getBuyerId(), orderId, "订单已确认",
                "您的订单已被平台确认，进入合同草拟阶段。");
        notifyOrderStatusChanged(order.getSellerId(), orderId, "订单已确认",
                "您的订单已被平台确认，进入合同草拟阶段。");

        log.info("[AdminOrder] Order {} confirmed by admin {}", orderId, adminId);
    }

    /**
     * 管理员强制取消订单
     * 适用场景：违规交易、双方协商一致取消、长时间无进展
     */
    @Transactional(rollbackFor = Exception.class)
    public void forceCancelOrder(String orderId, String reason) {
        AppOrder order = appOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }

        String currentStatus = order.getStatus();
        if ("COMPLETED".equals(currentStatus) || "CANCELLED".equals(currentStatus)) {
            throw new BusinessException(400, "当前订单状态不允许取消，当前状态: " + currentStatus);
        }

        if (reason == null || reason.trim().isEmpty()) {
            throw new BusinessException(400, "强制取消原因不能为空");
        }

        String oldStatus = order.getStatus();
        order.setStatus("CANCELLED");
        order.setCancelReason("[管理员操作] " + reason);
        order.setCancelledAt(LocalDateTime.now());
        appOrderMapper.updateById(order);

        // 记录操作日志
        Long adminId = getAdminId();
        logOrderAction(orderId, adminId, "ADMIN_FORCE_CANCEL",
                "管理员强制取消订单: " + oldStatus + " → CANCELLED, 原因: " + reason);

        // 通知买家和卖家
        notifyOrderStatusChanged(order.getBuyerId(), orderId, "订单已被平台取消",
                "您的订单已被平台管理员取消。原因: " + reason);
        notifyOrderStatusChanged(order.getSellerId(), orderId, "订单已被平台取消",
                "您的订单已被平台管理员取消。原因: " + reason);

        log.info("[AdminOrder] Order {} force cancelled by admin {}, reason: {}", orderId, adminId, reason);
    }

    /**
     * 管理员纠纷裁决
     * 适用场景：买卖双方发起纠纷后，管理员介入裁决
     * @param resolution "buyer" = 支持买家（退款）, "seller" = 支持卖家（扣保证金赔偿卖家）
     */
    @Transactional(rollbackFor = Exception.class)
    public void resolveDispute(String orderId, String resolution, String reason) {
        AppOrder order = appOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }

        if (!"DISPUTE".equals(order.getStatus())) {
            throw new BusinessException(400, "当前订单不在纠纷状态，无法裁决");
        }

        if (resolution == null || (!"buyer".equals(resolution) && !"seller".equals(resolution))) {
            throw new BusinessException(400, "裁决结果必须为 buyer 或 seller");
        }

        if (reason == null || reason.trim().isEmpty()) {
            throw new BusinessException(400, "裁决说明不能为空");
        }

        Long adminId = getAdminId();

        if ("buyer".equals(resolution)) {
            // 支持买家：取消订单，退还保证金
            order.setStatus("CANCELLED");
            order.setCancelReason("[纠纷裁决-支持买家] " + reason);
            order.setCancelledAt(LocalDateTime.now());
            appOrderMapper.updateById(order);

            logOrderAction(orderId, adminId, "ADMIN_DISPUTE_RESOLVE",
                    "纠纷裁决: 支持买家, 订单取消并退款。原因: " + reason);

            notifyOrderStatusChanged(order.getBuyerId(), orderId, "纠纷裁决结果",
                    "平台已裁决支持您的诉求，订单已取消，保证金将退还。");
            notifyOrderStatusChanged(order.getSellerId(), orderId, "纠纷裁决结果",
                    "平台已裁决支持买家，订单已取消。详情请联系客服。");
        } else {
            // 支持卖家：恢复订单到纠纷前状态（IN_TRANSIT 或 CONTRACT_SIGNED）
            order.setStatus("IN_TRANSIT");
            order.setRemark((order.getRemark() != null ? order.getRemark() + "; " : "")
                    + "[纠纷裁决-支持卖家] " + reason);
            appOrderMapper.updateById(order);

            logOrderAction(orderId, adminId, "ADMIN_DISPUTE_RESOLVE",
                    "纠纷裁决: 支持卖家, 订单恢复至 IN_TRANSIT。原因: " + reason);

            notifyOrderStatusChanged(order.getBuyerId(), orderId, "纠纷裁决结果",
                    "平台已裁决，订单将继续执行。详情请联系客服。");
            notifyOrderStatusChanged(order.getSellerId(), orderId, "纠纷裁决结果",
                    "平台已裁决支持您的诉求，订单将继续执行。");
        }

        log.info("[AdminOrder] Dispute on order {} resolved by admin {}, resolution: {}", orderId, adminId, resolution);
    }

    /**
     * 管理员退款
     * 适用场景：管理员介入后对订单执行退款操作
     * @param orderId 订单ID（雪花ID字符串）
     * @param refundAmount 退款金额，为null则全额退款
     * @param reason 退款原因
     */
    @Transactional(rollbackFor = Exception.class)
    public void refundOrder(String orderId, BigDecimal refundAmount, String reason) {
        AppOrder order = appOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }

        String currentStatus = order.getStatus();
        if (!"CONTRACT_SIGNED".equals(currentStatus)
                && !"IN_TRANSIT".equals(currentStatus)
                && !"COMPLETED".equals(currentStatus)) {
            throw new BusinessException("当前订单状态不支持退款");
        }

        if (refundAmount == null) {
            refundAmount = order.getTotalPrice();
        }

        order.setStatus("CANCELLED");
        order.setCancelReason("管理员退款: " + reason);
        order.setCancelledAt(LocalDateTime.now());
        appOrderMapper.updateById(order);

        // 记录操作日志
        logOrderAction(orderId, getAdminId(), "ADMIN_REFUND",
                "退款金额: " + refundAmount + ", 原因: " + reason);

        // 通知买家
        notifyOrderStatusChanged(order.getBuyerId(), orderId, "订单退款通知",
                "您的订单已退款，退款金额: ¥" + refundAmount);

        log.info("[AdminOrder] Order {} refunded by admin {}, amount: {}, reason: {}",
                orderId, getAdminId(), refundAmount, reason);
    }

    /**
     * 记录订单操作日志
     */
    private void logOrderAction(String orderId, Long operatorId, String action, String detail) {
        AppOrderLog orderLog = new AppOrderLog();
        orderLog.setOrderId(orderId);
        orderLog.setOperatorId(operatorId);
        orderLog.setAction(action);
        orderLog.setDetail(detail);
        appOrderLogMapper.insert(orderLog);
    }

    /**
     * 发送订单状态变更通知给买卖双方
     */
    private void notifyOrderStatusChanged(Long userId, String orderId, String title, String content) {
        if (userId == null) return;
        try {
            Map<String, Object> extra = new HashMap<>();
            extra.put("orderId", orderId);
            MobileNotification notification = MobileNotification.builder()
                    .type(MobileNotification.NotifyType.ORDER_STATUS_CHANGED)
                    .targetUserId(userId)
                    .title(title)
                    .content(content)
                    .targetType("order")
                    .targetId(orderId)
                    .extra(extra)
                    .timestamp(LocalDateTime.now())
                    .build();
            adminNotificationService.sendAndSave(notification);
        } catch (Exception e) {
            log.warn("[AdminOrder] Failed to send notification to user {} for order {}: {}",
                    userId, orderId, e.getMessage());
        }
    }

    /**
     * 获取当前管理员 ID
     */
    private Long getAdminId() {
        Object adminIdObj = request.getAttribute("ADMIN_ID");
        if (adminIdObj != null) {
            try {
                return Long.parseLong(adminIdObj.toString());
            } catch (NumberFormatException e) {
                log.warn("[AdminOrder] Invalid admin ID format: {}", adminIdObj);
            }
        }
        return 0L;
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
}
