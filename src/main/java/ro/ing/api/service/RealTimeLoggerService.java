package ro.ing.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class RealTimeLoggerService {

    private static final Logger logger = LoggerFactory.getLogger(RealTimeLoggerService.class);
    private final SimpMessagingTemplate messagingTemplate;

    public RealTimeLoggerService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendLog(String message) {
        logger.info("Broadcasting log message: {}", message);
        messagingTemplate.convertAndSend("/topic/logs", message);
    }
}
