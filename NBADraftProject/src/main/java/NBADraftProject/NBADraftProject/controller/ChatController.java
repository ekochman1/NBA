package NBADraftProject.NBADraftProject.controller;

import NBADraftProject.NBADraftProject.model.ChatMessage;
import NBADraftProject.NBADraftProject.model.DraftMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatController {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/draft.sendMessage.{leagueID}")
	@SendTo("/draft.{leagueID}")
    public DraftMessage sendDraftMessage(@DestinationVariable String leagueID, @Payload DraftMessage draftMessage) {
        return draftMessage;
    }

    @MessageMapping("/draft.sendPick.{leagueID}")
    @SendTo("/draft.{leagueID}")
    public DraftMessage sendDraftPick(@DestinationVariable String leagueID, @Payload DraftMessage draftMessage) {
    	return draftMessage;
    }

    @MessageMapping("/trash/chat.sendMessage")
    @SendTo("/trash/public")
    public ChatMessage sendTrashMessage(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }

    @MessageMapping("/chat.addUser.{leagueID}")
    @SendTo("/draft.{leagueID}")
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
