# Chat Server (Spring Boot)
Vue 프론트엔드와 연동되는 채팅 백엔드 서버입니다.  
JWT 기반 사용자 인증, Redis Pub/Sub, WebSocket을 활용하여 1:1 실시간 채팅 기능을 구현 중입니다.

## 💡 주요 기능
- 사용자 회원가입 및 로그인 (JWT 인증)
- 비밀번호 암호화 (Spring Security)
- 실시간 채팅 기능 (WebSocket + STOMP)
- 메시지 브로커로 Redis Pub/Sub 사용
- Docker 기반 MySQL, Redis 연동

## ⚙️ 기술 스택
#### 🔧 Backend </br>
[![Java](https://img.shields.io/badge/Java-17-007396?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/SpringBoot-3.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/SpringSecurity-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)](https://spring.io/projects/spring-security)
[![JPA](https://img.shields.io/badge/JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-data-jpa)

#### 🗄 Database & Cache </br>
[![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)](https://redis.io/)

#### ⚙️ DevOps / Build </br>
[![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)](https://gradle.org/)

#### 🔌 ETC </br>
[![WebSocket](https://img.shields.io/badge/WebSocket-0ABF53?style=for-the-badge&logo=websocket&logoColor=white)](https://developer.mozilla.org/en-US/docs/Web/API/WebSocket)
[![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white)](https://www.postman.com/)


---

## 📌 현재 진행 상황
- ✅ JWT 기반 로그인 구현
- ✅ 비밀번호 암호화 및 Security 설정

---
> 이 프로젝트는 백엔드-프론트 간 실시간 통신 구조에 대한 이해를 높이기 위해 개발 중이며,
> 웹소켓 처리와 인증 구조 학습을 목표로 합니다 👏
