package com.pancosky.cartradeadmin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pancosky.cartradeadmin.common.PageResult;
import com.pancosky.cartradeadmin.dto.NotificationQueryDTO;
import com.pancosky.cartradeadmin.entity.AdminNotification;
import com.pancosky.cartradeadmin.entity.AppUser;
import com.pancosky.cartradeadmin.event.MobileNotification;
import com.pancosky.cartradeadmin.event.MobileEventPublisher;
import com.pancosky.cartradeadmin.mapper.AdminNotificationMapper;
import com.pancosky.cartradeadmin.mapper.AppUserMapper;
import com.pancosky.cartradeadmin.vo.NotificationVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminNotificationService {

    @Autowired
    private AdminNotificationMapper adminNotificationMapper;

    @Autowired
    private MobileEventPublisher mobileEventPublisher;

    @Autowired
    private AppUserMapper appUserMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /** 发送通知并保存记录 */
    public void sendAndSave(MobileNotification notification) {
        // 保存到数据库
        AdminNotification record = new AdminNotification();
        record.setType(notification.getType().name());
        record.setTargetUserId(notification.getTargetUserId());
        record.setTitle(notification.getTitle());
        record.setContent(notification.getContent());
        record.setTargetType(notification.getTargetType());
        record.setTargetId(notification.getTargetId());
        record.setStatus("SENT");

        if (notification.getExtra() != null && !notification.getExtra().isEmpty()) {
            try {
                record.setExtraJson(objectMapper.writeValueAsString(notification.getExtra()));
            } catch (Exception e) {
                record.setExtraJson(null);
            }
        }

        adminNotificationMapper.insert(record);

        // 发布到 Redis
        mobileEventPublisher.publish(notification);
    }

    /** 便捷方法 */
    public void notify(MobileNotification.NotifyType type, Long targetUserId,
                       String title, String content,
                       String targetType, String targetId) {
        MobileNotification notification = MobileNotification.builder()
                .type(type)
                .targetUserId(targetUserId)
                .title(title)
                .content(content)
                .targetType(targetType)
                .targetId(targetId)
                .build();
        sendAndSave(notification);
    }

    /** 发送系统公告（广播给所有活跃用户） */
    public int broadcastAnnouncement(String title, String content) {
        // 查询所有活跃用户
        List<AppUser> users = appUserMapper.selectList(
                new LambdaQueryWrapper<AppUser>()
                        .isNull(AppUser::getDeletedAt)
                        .eq(AppUser::getStatus, "ACTIVE"));

        MobileNotification notification = MobileNotification.builder()
                .type(MobileNotification.NotifyType.SYSTEM_ANNOUNCEMENT)
                .title(title)
                .content(content)
                .targetType("system")
                .targetId("0")
                .build();

        for (AppUser user : users) {
            notification.setTargetUserId(user.getId());
            sendAndSave(notification);
        }

        return users.size();
    }

    /** 分页查询通知记录 */
    public PageResult<NotificationVO> getNotificationList(NotificationQueryDTO query) {
        LambdaQueryWrapper<AdminNotification> wrapper = new LambdaQueryWrapper<>();

        if (query.getType() != null && !query.getType().isEmpty()) {
            wrapper.eq(AdminNotification::getType, query.getType());
        }
        if (query.getTargetUserId() != null) {
            wrapper.eq(AdminNotification::getTargetUserId, query.getTargetUserId());
        }
        if (query.getStatus() != null && !query.getStatus().isEmpty()) {
            wrapper.eq(AdminNotification::getStatus, query.getStatus());
        }
        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            wrapper.like(AdminNotification::getTitle, query.getKeyword())
                    .or().like(AdminNotification::getContent, query.getKeyword());
        }

        wrapper.orderByDesc(AdminNotification::getCreatedAt);

        Page<AdminNotification> page = adminNotificationMapper.selectPage(
                new Page<>(query.getPage(), query.getSize()), wrapper);

        List<NotificationVO> voList = page.getRecords().stream().map(n -> {
            NotificationVO vo = new NotificationVO();
            vo.setId(n.getId());
            vo.setType(n.getType());
            vo.setTargetUserId(n.getTargetUserId());
            vo.setTitle(n.getTitle());
            vo.setContent(n.getContent());
            vo.setTargetType(n.getTargetType());
            vo.setTargetId(n.getTargetId());
            vo.setStatus(n.getStatus());
            vo.setCreatedAt(n.getCreatedAt());

            // 查询用户名
            if (n.getTargetUserId() != null) {
                AppUser user = appUserMapper.selectById(n.getTargetUserId());
                if (user != null) {
                    vo.setTargetUserName(user.getNickname());
                }
            }
            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(voList, page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }

    /** 获取通知统计 */
    public Map<String, Object> getNotificationStats() {
        long totalSent = adminNotificationMapper.selectCount(
                new LambdaQueryWrapper<AdminNotification>().eq(AdminNotification::getStatus, "SENT"));
        long totalFailed = adminNotificationMapper.selectCount(
                new LambdaQueryWrapper<AdminNotification>().eq(AdminNotification::getStatus, "FAILED"));

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSent", totalSent);
        stats.put("totalFailed", totalFailed);
        return stats;
    }
}
