package com.example.chatserver.member.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 엔티티에 작성하면 그대로 DB에 연결된 (작성됨)
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING) // 기본 Enum이 숫자로 들어가기에 String으로 지정하기
    @Builder.Default // 이게 있어야 아래 디폴트값이 지정해서 들어감, 또한 @Builder 어노테이션 위에 있어야함
    private Role role = Role.USER;

}
