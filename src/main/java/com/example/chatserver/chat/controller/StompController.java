package com.example.chatserver.chat.controller;

import com.example.chatserver.chat.dto.ChatMessageReqDto;
import com.example.chatserver.chat.service.ChatService;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;


@Controller
@Log4j2
public class StompController {

    private final SimpMessageSendingOperations messageTemplate;
    private final ChatService chatService;

    public StompController(SimpMessageSendingOperations messageTemplate, ChatService chatService) {
        this.messageTemplate = messageTemplate;
        this.chatService = chatService;
    }

    // 방법1. MessageMapping(수신)과 SendTo(topic에 메시지전달)한꺼번에 처리
//    @MessageMapping("/{roomId}") // 클라이언트에서 특정 publish/roomId 메시지를 발행시 MessageMapping 수신
//    @SendTo("/topic/{roomId}") // 해당 roomId에 메시지를 발행하여 구독중인 사용자에게 메시지를 전달
//    // DestinationVariable : MessageMapping 어노테이션으로 정의된 웹소켓 컨트롤러 내에서만 사용된다.
//    public String sendMessage(@DestinationVariable Long roomId, String message) {
//        log.info("Sending message: {} ", message);
//
//        return message; // SendTo에 의해서  매시지가 발행된다.
//    }


    // 방법2. MessageMapping 어노테이션만 활용
    @MessageMapping("/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, ChatMessageReqDto chatMessageReqDto) {
        log.info("Sending message: {} ", chatMessageReqDto.getMessage());
        chatService.saveMessage(roomId, chatMessageReqDto);
        messageTemplate.convertAndSend("/topic/" + roomId, chatMessageReqDto);

    }


}
