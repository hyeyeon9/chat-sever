package com.example.chatserver.chat.controller;

import com.example.chatserver.chat.dto.ChatMessageDto;
import com.example.chatserver.chat.service.ChatService;
import com.example.chatserver.chat.service.RedisPubSubService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;


@Controller
@Log4j2
public class StompController {

    private final SimpMessageSendingOperations messageTemplate;
    private final ChatService chatService;
    private final RedisPubSubService redisPubSubService;
    private final ObjectMapper objectMapper;

    public StompController(SimpMessageSendingOperations messageTemplate, ChatService chatService, RedisPubSubService redisPubSubService, ObjectMapper objectMapper) {
        this.messageTemplate = messageTemplate;
        this.chatService = chatService;
        this.redisPubSubService = redisPubSubService;
        this.objectMapper = objectMapper;
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
    public void sendMessage(@DestinationVariable Long roomId, ChatMessageDto chatMessageDto) throws JsonProcessingException {
        log.info("Sending message: {} ", chatMessageDto.getMessage());
        chatService.saveMessage(roomId, chatMessageDto);

        chatMessageDto.setRoomId(roomId);
        // json 형태로 바꾸기
        ObjectMapper  objectMapper = new ObjectMapper();
        String message = objectMapper.writeValueAsString(chatMessageDto);

        redisPubSubService.publish("chat",message );
    }


}
