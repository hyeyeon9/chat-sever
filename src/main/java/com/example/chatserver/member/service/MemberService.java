package com.example.chatserver.member.service;

import com.example.chatserver.member.domain.Member;
import com.example.chatserver.member.dto.MemberListResDto;
import com.example.chatserver.member.dto.MemberLoginReqDto;
import com.example.chatserver.member.dto.MemberResDto;
import com.example.chatserver.member.dto.MemberSaveReqDto;
import com.example.chatserver.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class MemberService {
    private final PasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;


    public MemberService(PasswordEncoder passwordEncoder, MemberRepository memberRepository) {
        this.passwordEncoder = passwordEncoder;
        this.memberRepository = memberRepository;
    }

    public Member create(MemberSaveReqDto memberSaveReqDto) {
// 이미 가입되어 있는 이메일 검증
        if(memberRepository.findByEmail(memberSaveReqDto.getEmail()).isPresent()){
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        Member newMember = Member.builder()
                .name(memberSaveReqDto.getName())
                .email(memberSaveReqDto.getEmail())
                .password(passwordEncoder.encode(memberSaveReqDto.getPassword()))
                .build( );

        Member member = memberRepository.save(newMember);

        return member;
    }

    public Member login(MemberLoginReqDto memberLoginReqDto) {

        Member member = memberRepository.findByEmail(memberLoginReqDto.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 이메일입니다."));

        if(!passwordEncoder.matches(memberLoginReqDto.getPassword(), member.getPassword())){
            throw  new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return member;
    }

    public List<MemberListResDto> findAll(){
        // 멤버 리스트로 조회
        List<Member> members = memberRepository.findAll();
        // MemberListResDto 형식에 맞춰서 넣을 거임
        List<MemberListResDto> memberListResDtos = new ArrayList<>();
        // for문으로 돌려서 하나씩 넣어주기
        for(Member m : members){
            MemberListResDto memberListResDto =new MemberListResDto();
            memberListResDto.setId(m.getId());
            memberListResDto.setEmail(m.getEmail());
            memberListResDto.setName(m.getName());
            memberListResDtos.add(memberListResDto);
        }
        // 그거 리턴함
        return memberListResDtos;
    }

    // 내 정보 가져가기
    public MemberResDto getMyPageInfo(){
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(()-> new EntityNotFoundException("Member not found"));

        MemberResDto memberResDto = MemberResDto.builder()
                .profileImageUrl(member.getProfileImageUrl())
                .name(member.getName())
                .build();

        return memberResDto;
    }

    // 닉네임 업데이트
    public void updateName(String name){
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(()-> new EntityNotFoundException("Member not found"));
        member.updateName(name);
        memberRepository.save(member);
    }

    // 프로필 사진 업데이트
    public void updateProfileImage(String imageUrl){
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(()-> new EntityNotFoundException("Member not found"));
        member.updateProfileImage(imageUrl);
        memberRepository.save(member);
    }
}
