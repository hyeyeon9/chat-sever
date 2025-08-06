package com.example.chatserver.chat.repository;

import com.example.chatserver.chat.domain.ChatParticipant;
import com.example.chatserver.chat.domain.ChatRoom;
import com.example.chatserver.member.domain.Member;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {

    List<ChatParticipant> findByChatRoom(ChatRoom chatRoom);

    Optional<ChatParticipant> findByChatRoomAndMember(ChatRoom chatRoom, Member member);

    List<ChatParticipant> findAllByMember(Member member);

    @Query("""
        SELECT cp1.chatRoom from ChatParticipant cp1 
        Join ChatParticipant cp2 on cp1.chatRoom.id = cp2.chatRoom.id
        where cp1.member.id = :myId and cp2.member.id = :otherMemberId
        and cp1.chatRoom.isGroupChat = 'N'
        """)
    Optional<ChatRoom> findExistingPrivateRoom(@Param("myId") Long myId, @Param("otherMemberId") Long otherMemberId);


}
