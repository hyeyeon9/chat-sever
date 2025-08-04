package com.example.chatserver.chat.service;

import com.example.chatserver.chat.domain.ChatMessage;
import com.example.chatserver.chat.domain.ChatParticipant;
import com.example.chatserver.chat.domain.ChatRoom;
import com.example.chatserver.chat.domain.ReadStatus;
import com.example.chatserver.chat.dto.ChatMessageDto;
import com.example.chatserver.chat.dto.ChatRoomListResDto;
import com.example.chatserver.chat.dto.MyChatListResDto;
import com.example.chatserver.chat.repository.ChatMessageRepository;
import com.example.chatserver.chat.repository.ChatParticipantRepository;
import com.example.chatserver.chat.repository.ChatRoomRepository;
import com.example.chatserver.chat.repository.ReadStatusRepository;
import com.example.chatserver.member.domain.Member;
import com.example.chatserver.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private  final ReadStatusRepository readStatusRepository;
    private final MemberRepository memberRepository;


    public ChatService(ChatRoomRepository chatRoomRepository, ChatParticipantRepository chatParticipantRepository, ChatMessageRepository chatMessageRepository, ReadStatusRepository readStatusRepository, MemberRepository memberRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.chatParticipantRepository = chatParticipantRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.readStatusRepository = readStatusRepository;
        this.memberRepository = memberRepository;
    }

    // 메시지 저장 로직
    public void saveMessage(Long roomId, ChatMessageDto chatMessageDto){
        // 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("Room not found"));

        // 보낸 사람 조회
        Member sender = memberRepository.findByEmail(chatMessageDto.getSenderEmail())
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));


        // 메시지 저장
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom) // 채팅방
                .member(sender)     // 멤버
                .content(chatMessageDto.getMessage()) // 채팅 메시지
                .build();

        chatMessageRepository.save(chatMessage);


        // 사용자별로 읽음 여부 저장
        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
        for(ChatParticipant c : chatParticipants){
            ReadStatus readStatus = ReadStatus.builder()
                    .chatRoom(chatRoom)
                    .member(c.getMember())
                    .chatMessage(chatMessage)
                    .isRead(c.getMember().equals(sender)) // 보낸사람은 바로 읽음 처리, 다른 사람은 읽음 X 처리
                    .build();

            readStatusRepository.save(readStatus);
        }

    }

    public void createGroupRoom(String chatRoomName){
        // SecurityContextHolder는 성공적으로 로그인하면 Authentication 객체가 만들어지고
        // 그 객체 안에서 이메일과 권한을 꺼내올 수 있다.
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("member cannot be found")); // 이메일

        // 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .name(chatRoomName)
                .isGroupChat("Y")
                .build();

        chatRoomRepository.save(chatRoom);

        // 채팅참여자로 member 추가 (채팅방 개설한 사람 => 위의 member)
        ChatParticipant chatParticipant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .member(member)
                .build();

        chatParticipantRepository.save(chatParticipant);

    }

    // 그룹채팅방 목록 조회
    public List<ChatRoomListResDto> getGroupchatRoom(){
        List<ChatRoom> chatRooms =  chatRoomRepository.findByIsGroupChat("Y");


        List<ChatRoomListResDto> dtos = new ArrayList<>();

        for(ChatRoom c : chatRooms){
            ChatRoomListResDto dto = ChatRoomListResDto.builder()
                    .roomId(c.getId())
                    .roomName(c.getName())
                    .build();

            dtos.add(dto);
        }
        return dtos;
    }

    public void addParticipantToGroupChat(Long roomId){
        // 채팅방 조회
        ChatRoom chatRoom =  chatRoomRepository.findById(roomId).orElseThrow(()->new EntityNotFoundException("Room not found"));

        // member 조회
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(()-> new EntityNotFoundException("Member not found"));

        // 이미 참여자인지 검증하겠다.
        Optional<ChatParticipant> participant =  chatParticipantRepository.findByChatRoomAndMember(chatRoom, member);
        if(!participant.isPresent()){
            addParticipantToRoom(chatRoom, member);
        }

    }

    // ChatParticipant 객체 생성해서 DB에 저장
    public void addParticipantToRoom(ChatRoom chatRoom, Member member){
        ChatParticipant chatParticipant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .member(member)
                .build();

        chatParticipantRepository.save(chatParticipant);

    }

    // 이전 메시지 조회
    public List<ChatMessageDto> getChatHistory(Long roomId){
       // 해당 사용자가 room 참여자일 경우에만 조회 가능
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(()->new EntityNotFoundException("Room not found"));
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(()->new EntityNotFoundException("Member not found"));

        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
        // 위 참여자들 chatParticipants 중에 내가 안들어가 있으면 에러
        boolean check = false;
        for(ChatParticipant c : chatParticipants){
                if(c.getMember().equals(member)){
                    check = true;
                }
        }

        if(!check)throw new IllegalArgumentException("본인이 속하지 않은 채팅방입니다.");


        // 특정 room에 대한 메시지 조회
        List<ChatMessage> chatMessages = chatMessageRepository.findByChatRoomOrderByCreateTimeAsc(chatRoom);

        List<ChatMessageDto> chatMessageDtos = new ArrayList<>();
        for(ChatMessage c : chatMessages){
            ChatMessageDto chatMessageDto = ChatMessageDto.builder()
                    .message(c.getContent())
                    .senderEmail(c.getMember().getEmail())
                    .build();

            chatMessageDtos.add(chatMessageDto);
        }

        return chatMessageDtos;
    }

    // 채팅방 참여자인지 확인
    public Boolean isRoomParticipant(String email,Long roomId){

        // 채팅방 조회
        ChatRoom chatRoom =  chatRoomRepository.findById(roomId).orElseThrow(()->new EntityNotFoundException("Room not found"));

        // member 조회
        Member member = memberRepository.findByEmail(email).orElseThrow(()-> new EntityNotFoundException("Member not found"));

        return chatParticipantRepository.findByChatRoomAndMember(chatRoom, member).isPresent();
    }

    public void messageRead(Long roomId){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(()->new EntityNotFoundException("Room not found"));
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow();

        // 해당 멤버의 채팅방 읽음목록 확인
        List<ReadStatus> readStatuses = readStatusRepository.findByChatRoomAndMember(chatRoom, member);

        // 다 읽음처리 하기
        for(ReadStatus rs : readStatuses){
                rs.updateRead(true);
        }
    }


    // 내 채팅방 목록 조회
    public List<MyChatListResDto> getMyChatRooms(){
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow();

        List<ChatParticipant> chatParticipants =  chatParticipantRepository.findByAllMember(member);
        List<MyChatListResDto> myChatListResDtos = new ArrayList<>();

        for(ChatParticipant c : chatParticipants){
            Long count = readStatusRepository.countByChatRoomAndMemberAndIsReadFalse(c.getChatRoom(), member);
            MyChatListResDto dto = MyChatListResDto.builder()
                    .roomId(c.getChatRoom().getId())
                    .roomName(c.getChatRoom().getName())
                    .unReadCount(count)
                    .isGroupChat(c.getChatRoom().getIsGroupChat())
                    .build();

            myChatListResDtos.add(dto);
        }

        return myChatListResDtos;

    }



}

