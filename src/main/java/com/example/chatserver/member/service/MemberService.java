package com.example.chatserver.member.service;

import com.example.chatserver.member.domain.Member;
import com.example.chatserver.member.dto.MemberLoginReqDto;
import com.example.chatserver.member.dto.MemberSaveReqDto;
import com.example.chatserver.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
}
