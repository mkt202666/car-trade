package com.pancosky.cartradeadmin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pancosky.cartradeadmin.common.BusinessException;
import com.pancosky.cartradeadmin.common.PageResult;
import com.pancosky.cartradeadmin.dto.DisputeHandleDTO;
import com.pancosky.cartradeadmin.dto.DisputeQueryDTO;
import com.pancosky.cartradeadmin.entity.AppDispute;
import com.pancosky.cartradeadmin.entity.AppOrder;
import com.pancosky.cartradeadmin.entity.AppUser;
import com.pancosky.cartradeadmin.event.AdminEvent;
import com.pancosky.cartradeadmin.event.AdminEventPublisher;
import com.pancosky.cartradeadmin.event.MobileNotification;
import com.pancosky.cartradeadmin.mapper.AppDisputeMapper;
import com.pancosky.cartradeadmin.mapper.AppOrderMapper;
import com.pancosky.cartradeadmin.mapper.AppUserMapper;
import com.pancosky.cartradeadmin.vo.DisputeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminDisputeService {

    @Autowired
    private AppDisputeMapper appDisputeMapper;

    @Autowired
    private AppUserMapper appUserMapper;

    @Autowired
    private AppOrderMapper appOrderMapper;

    @Autowired
    private AdminEventPublisher adminEventPublisher;

    @Autowired
    private AdminNotificationService adminNotificationService;

    public PageResult<DisputeVO> getDisputeList(DisputeQueryDTO query) {
        LambdaQueryWrapper<AppDispute> wrapper = new LambdaQueryWrapper<AppDispute>();

        if (query.getStatus() != null && !query.getStatus().isEmpty() && !"ALL".equals(query.getStatus())) {
            wrapper.eq(AppDispute::getStatus, query.getStatus());
        }

        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            wrapper.like(AppDispute::getReason, query.getKeyword());
        }

        wrapper.orderByDesc(AppDispute::getCreatedAt);

        Page<AppDispute> page = appDisputeMapper.selectPage(
                new Page<>(query.getPage(), query.getSize()), wrapper);

        List<DisputeVO> voList = page.getRecords().stream().map(dispute -> {
            DisputeVO vo = new DisputeVO();
            vo.setId(dispute.getId());
            vo.setOrderId(dispute.getOrderId());
            vo.setReason(dispute.getReason());
            vo.setStatus(dispute.getStatus());
            vo.setCreatedAt(dispute.getCreatedAt());

            // 联表查询发起人信息
            if (dispute.getInitiatorId() != null) {
                AppUser initiator = appUserMapper.selectById(dispute.getInitiatorId());
                if (initiator != null) {
                    vo.setInitiatorName(initiator.getNickname());
                    vo.setInitiatorPhone(maskPhone(initiator.getPhone()));
                }
            }

            // 联表查询订单号
            if (dispute.getOrderId() != null) {
                AppOrder order = appOrderMapper.selectById(dispute.getOrderId());
                if (order != null) {
                    vo.setOrderTitle(order.getId());
                }
            }

            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(voList, page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }

    public List<DisputeVO> getDisputeExportList(DisputeQueryDTO query) {
        LambdaQueryWrapper<AppDispute> wrapper = new LambdaQueryWrapper<AppDispute>();

        if (query.getStatus() != null && !query.getStatus().isEmpty() && !"ALL".equals(query.getStatus())) {
            wrapper.eq(AppDispute::getStatus, query.getStatus());
        }

        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            wrapper.like(AppDispute::getReason, query.getKeyword());
        }

        wrapper.orderByDesc(AppDispute::getCreatedAt);

        List<AppDispute> disputes = appDisputeMapper.selectList(wrapper);

        return disputes.stream().map(dispute -> {
            DisputeVO vo = new DisputeVO();
            vo.setId(dispute.getId());
            vo.setOrderId(dispute.getOrderId());
            vo.setReason(dispute.getReason());
            vo.setStatus(dispute.getStatus());
            vo.setCreatedAt(dispute.getCreatedAt());

            // 联表查询发起人信息
            if (dispute.getInitiatorId() != null) {
                AppUser initiator = appUserMapper.selectById(dispute.getInitiatorId());
                if (initiator != null) {
                    vo.setInitiatorName(initiator.getNickname());
                    vo.setInitiatorPhone(maskPhone(initiator.getPhone()));
                }
            }

            // 联表查询订单号
            if (dispute.getOrderId() != null) {
                AppOrder order = appOrderMapper.selectById(dispute.getOrderId());
                if (order != null) {
                    vo.setOrderTitle(order.getId());
                }
            }

            return vo;
        }).collect(Collectors.toList());
    }

    public void handleDispute(Long id, DisputeHandleDTO dto, Long operatorId) {
        AppDispute dispute = appDisputeMapper.selectById(id);
        if (dispute == null) {
            throw new BusinessException(404, "争议不存在");
        }

        String action = dto.getAction();
        String newStatus;
        AdminEvent.EventType eventType;

        switch (action) {
            case "APPROVE":
                newStatus = "RESOLVED";
                eventType = AdminEvent.EventType.DISPUTE_RESOLVED;
                break;
            case "REJECT":
                newStatus = "REJECTED";
                eventType = AdminEvent.EventType.DISPUTE_RESOLVED;
                break;
            case "NEGOTIATE":
                newStatus = "IN_PROGRESS";
                eventType = AdminEvent.EventType.DISPUTE_RESOLVED;
                break;
            default:
                throw new BusinessException(400, "无效的操作类型");
        }

        dispute.setStatus(newStatus);
        appDisputeMapper.updateById(dispute);

        // 发布 Redis 事件
        adminEventPublisher.publish(eventType, operatorId, String.valueOf(operatorId),
                "dispute", String.valueOf(id), dto.getResult());

        // 发送移动端通知给争议发起人
        if (dispute.getInitiatorId() != null) {
            String actionText = switch (action) {
                case "APPROVE" -> "支持";
                case "REJECT" -> "驳回";
                case "NEGOTIATE" -> "要求协商";
                default -> "处理";
            };
            adminNotificationService.notify(
                    MobileNotification.NotifyType.DISPUTE_RESOLVED, dispute.getInitiatorId(),
                    "争议处理结果", "您发起的争议已处理，处理结果：" + actionText + "。" + dto.getResult(),
                    "dispute", String.valueOf(id));
        }
    }

    public long getPendingCount() {
        return appDisputeMapper.selectCount(
                new LambdaQueryWrapper<AppDispute>()
                        .in(AppDispute::getStatus, "OPEN", "IN_PROGRESS"));
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
}
