package com.example.chatserver.member.controller;

import com.example.chatserver.common.auth.JwtTokenProvider;
import com.example.chatserver.member.domain.Member;
import com.example.chatserver.member.dto.MemberLoginReqDto;
import com.example.chatserver.member.dto.MemberSaveReqDto;
import com.example.chatserver.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// responseBody가 붙는 형식이다.
@RestController
@RequestMapping("/member")
public class MemberController
 {
     private final MemberService memberService;
     private final JwtTokenProvider jwtTokenProvider;


     // 의존성 주입
     public MemberController(MemberService memberService, JwtTokenProvider jwtTokenProvider) {
         this.memberService = memberService;
         this.jwtTokenProvider = jwtTokenProvider;
     }

     @PostMapping("/create") // url 패
     public ResponseEntity<?> memberCreate(@RequestBody MemberSaveReqDto meberSaveReqDto){
      Member member = memberService.create(meberSaveReqDto);
      return new ResponseEntity<>(member.getId(), HttpStatus.CREATED); // Id와 상태코드까지 같이 responseEntity형식으로 전달하겠ㄷ.
     }

     @PostMapping("/doLogin")
     public  ResponseEntity<?> doLogin(@RequestBody MemberLoginReqDto memberLoginReqDto){
         // email, password 검증하고
         Member member = memberService.login(memberLoginReqDto);


         // 일치하면 access 토큰을 발행할 것
        String jwtToken = jwtTokenProvider.createToken(member.getEmail(), member.getRole().toString());
        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id", member.getId());
        loginInfo.put("token", jwtToken);
        // { "id" : 1, "token" : "acd8sasgbw"} 처럼 json 형태로 나가기 위함

        return new ResponseEntity<>(loginInfo, HttpStatus.OK);

     }


 }
