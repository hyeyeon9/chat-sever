package com.example.chatserver.member.repository;

import com.example.chatserver.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // Optional은 값이 있을수도 없을수도, 그래서 isPresent 메서드를 사용함
    Optional<Member> findByEmail(String email);
}
