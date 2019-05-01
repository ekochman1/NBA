package NBADraftProject.NBADraftProject.controller;

import NBADraftProject.NBADraftProject.model.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    @MessageMapping("/{leagueID}/chat.sendMessage")
    @SendTo("/draft/{leagueID}")
    public ChatMessage sendDraftMessage(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }

    @MessageMapping("/{leagueID}/draft.sendPick")
    @SendTo("/draft/{leagueID}")
    public ChatMessage sendDraftPick(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }

    @MessageMapping("/trash/chat.sendMessage")
    @SendTo("/trash/public")
    public ChatMessage sendTrashMessage(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }

    @MessageMapping("/{leagueID}/chat.addUser")
    @SendTo("/draft/{leagueID}")
    public ChatMessage addDraftUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }

    @MessageMapping("/trash/chat.addUser")
    @SendTo("/trash/public")
    public ChatMessage addTrashUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }
}
