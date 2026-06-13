package com.pancosky.cartradeadmin.controller;

import com.pancosky.cartradeadmin.annotation.AuditLog;
import com.pancosky.cartradeadmin.common.ApiResponse;
import com.pancosky.cartradeadmin.common.PageResult;
import com.pancosky.cartradeadmin.dto.AnnouncementDTO;
import com.pancosky.cartradeadmin.dto.NotificationQueryDTO;
import com.pancosky.cartradeadmin.event.MobileNotification;
import com.pancosky.cartradeadmin.service.AdminNotificationService;
import com.pancosky.cartradeadmin.vo.NotificationVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/notifications")
public class AdminNotificationController {

    @Autowired
    private AdminNotificationService adminNotificationService;

    /** 发送系统公告（广播给所有活跃用户） */
    @AuditLog(module = "notification", action = "发送系统公告", targetType = "system")
    @PostMapping("/announcement")
    public ApiResponse<Map<String, Object>> sendAnnouncement(@Valid @RequestBody AnnouncementDTO dto) {
        int count = adminNotificationService.broadcastAnnouncement(dto.getTitle(), dto.getContent());
        return ApiResponse.success(Map.of("sentCount", count));
    }

    /** 发送单条通知给指定用户 */
    @AuditLog(module = "notification", action = "发送通知", targetType = "user")
    @PostMapping("/send")
    public ApiResponse<Void> sendNotification(@RequestBody MobileNotification notification) {
        adminNotificationService.sendAndSave(notification);
        return ApiResponse.success();
    }

    /** 分页查询通知记录 */
    @GetMapping
    public ApiResponse<PageResult<NotificationVO>> listNotifications(@ModelAttribute NotificationQueryDTO query) {
        return ApiResponse.success(adminNotificationService.getNotificationList(query));
    }

    /** 通知统计 */
    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getStats() {
        return ApiResponse.success(adminNotificationService.getNotificationStats());
    }
}
