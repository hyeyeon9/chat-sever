package com.example.chatserver.member.controller;

import com.example.chatserver.common.auth.JwtTokenProvider;
import com.example.chatserver.member.domain.Member;
import com.example.chatserver.member.dto.MemberListResDto;
import com.example.chatserver.member.dto.MemberLoginReqDto;
import com.example.chatserver.member.dto.MemberResDto;
import com.example.chatserver.member.dto.MemberSaveReqDto;
import com.example.chatserver.member.service.MemberService;
import com.example.chatserver.member.service.S3Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

// responseBody가 붙는 형식이다.
@RestController
@RequestMapping("/member")
public class MemberController
 {
     private final MemberService memberService;
     private final JwtTokenProvider jwtTokenProvider;
     private final S3Service s3Service;


     // 의존성 주입
     public MemberController(MemberService memberService, JwtTokenProvider jwtTokenProvider, S3Service s3Service) {
         this.memberService = memberService;
         this.jwtTokenProvider = jwtTokenProvider;
         this.s3Service = s3Service;
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

     @GetMapping("/list")
     public ResponseEntity<?> memberList(){
         List<MemberListResDto> dtos = memberService.findAll();
         return new ResponseEntity<>(dtos, HttpStatus.OK);
     }

     // 현재 사용자 정보 넘기기
     @GetMapping("/me")
     public ResponseEntity<?> getMyPageInfo(){
         MemberResDto dto = memberService.getMyPageInfo();
         return new ResponseEntity<>(dto, HttpStatus.OK);
     }

     // 멤버 닉네임 수정하기
     @PatchMapping("/name")
     public ResponseEntity<?> updateName(@RequestParam String name){
         memberService.updateName(name);
         return ResponseEntity.ok().build();
     }

     // 프로필 사진 수정하기
     @PostMapping("/profile")
     public ResponseEntity<?> updateProfile(@RequestPart MultipartFile image){
         String imageUrl = s3Service.upload(image);

         memberService.updateProfileImage(imageUrl);
         return ResponseEntity.ok(Map.of("profileImageUrl", imageUrl));
     }

 }
