package com.pancosky.newcartrade.service;

import com.pancosky.newcartrade.entity.Message;
import com.pancosky.newcartrade.vo.MessageVO;

import java.util.List;

public interface MessageService {
    List<MessageVO> list(String type, Boolean isRead);
    long unreadCount();
    void markRead(Long id);
    void markAllRead();
    void delete(Long id);
    void sendSystem(Message message);
    void sendChat(Message message);
    String getNotificationSettings();
    void updateNotificationSettings(String settings);
}
