# 프로그래머스 데브코스 BE - 3차 프로젝트

## 주제
> ### 조립식 컴퓨터에 익숙하지 않은 사용자를 위한 PC 견적 서비스

#### [[2차 프로젝트]](https://github.com/prgrms-be-devcourse/NBE3-4-2-Team03)를 Kotlin으로 리팩토링하고, 추가 기능을 기획 및 구현한 프로젝트입니다.

### 서비스 구조
![스크린샷 2025-03-15 오후 11 10 25](https://github.com/user-attachments/assets/93d10ba0-0d74-4e6c-b67b-2980c9b5a1f4)

## 주요 기능
- 구매자
  - 회원가입 / 로그인 **(소셜 로그인 추가)**
  - 견적 요청
  - 받은 견적 확인
  - 견적 채택 및 배송 확인
  - ~~댓글 문의~~ -> 판매자와 실시간 **채팅**
- 판매자
  - 회원가입 / 로그인 **(소셜 로그인 추가)**
  - 받은 견적 요청 내역 확인
  - 견적서 작성
  - ~~댓글 답변~~ -> 구매자와 실시간 **채팅**
- 관리자
  - 부품 카테고리 관리
  - 부품 관리

## 기술 스택
| 카테고리          | 기술 스택                 |
|---------------|-----------------------|
| 언어            | Java 17               |
| 프레임워크         | Spring Boot           |
| DB            | MySQL, JPA            |
| 인증 방식         | JWT, OAuth2           |
| 실시간 통신        | SSE, WebSocket, STOMP |
| Test          | JUnit5                |
| CI            | GitHub Actions        |
| Collaboration | Slack, Notion, Figma  |

## 설계 문서
### 추가 요구사항 명세서
![스크린샷 2025-03-15 오후 11 40 48](https://github.com/user-attachments/assets/32940973-4ff4-4dc0-8671-9631d9d72248)

### API 명세서
- 2차 프로젝트 [[위키]](https://github.com/prgrms-be-devcourse/NBE3-4-2-Team03/wiki)
- 서버 실행 후 Swagger UI로 확인하실 수도 있습니다.

### 발표 자료
#### [[3팀_3차_발표자료]](https://github.com/user-attachments/files/19262904/NBE3-4-3_Team03_.pdf)