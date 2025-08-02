package com.example.chatserver.chat.dto;

import com.example.chatserver.chat.domain.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomListResDto {
    private Long roomId;
    private String roomName;
}
