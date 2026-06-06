package com.pancosky.newcartrade.manager;

import com.pancosky.newcartrade.vo.MessageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PushManager {

    private final SimpMessagingTemplate messagingTemplate;

    public void pushToUser(Long userId, MessageVO message) {
        messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/messages", message);
    }

    public void pushToAll(MessageVO message) {
        messagingTemplate.convertAndSend("/topic/announcements", message);
    }
}
