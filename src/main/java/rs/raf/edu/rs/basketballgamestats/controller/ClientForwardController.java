package rs.raf.edu.rs.basketballgamestats.controller;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

@RestController
public class ClientForwardController {


    private final SimpMessageSendingOperations messagingTemplate;

    public ClientForwardController(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/topic/game")
    @SendTo("/topic/game")
    public String send(@Payload String message) {
        return message;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        System.out.println("Received a new web socket connection. Session: " + headerAccessor.getSessionId());

        this.messagingTemplate.convertAndSend("/topic/game", "greeting");
    }
}
