package com.example.chatserver.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // setter, getter, toString 등이 자동 적용됨, dto에는 setter까지 사용가능
@NoArgsConstructor
@AllArgsConstructor
public class MemberSaveReqDto {
    private String name;
    private String email;
    private String password;
}
