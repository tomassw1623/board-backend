# 게시판 프로젝트

회원가입/로그인, 게시글 CRUD, 댓글, 검색, 페이징을 지원하는 게시판 웹 애플리케이션입니다.

## 기술 스택

| 구분 | 사용 기술 |
|---|---|
| Backend | Java 17, Spring Boot, MyBatis, PostgreSQL |
| Frontend | React 19, Vite |
| 인증 | 세션(쿠키) 기반 로그인, BCrypt 비밀번호 암호화 |
| 테스트 | JUnit 5, Mockito, MockMvc |

## 주요 기능

- 회원가입 / 로그인 / 로그아웃 (세션)
- 게시글 작성·조회·수정·삭제, 제목·내용 검색, 페이징
- 조회수 (DB에서 원자적으로 증가)
- 댓글 작성·삭제
- **본인 글/댓글만 수정·삭제 가능** (서버에서 검증, 401/403 응답)
- 입력값 검증과 일관된 에러 응답 형식

## 실행 방법

### 1. DB 준비
PostgreSQL에서 `board-backand/db/schema.sql`을 실행합니다.

### 2. 백엔드
```bash
cd board-backand
# DB 접속 정보는 환경변수로 전달합니다 (Windows cmd 기준)
set DB_PASSWORD=(본인 DB 비밀번호)
gradlew bootRun
```
- 기본 접속 주소: `jdbc:postgresql://localhost:5432/postgres` (`DB_URL`, `DB_USERNAME`으로 변경 가능)
- 서버 주소: http://localhost:8080

### 3. 프론트엔드
```bash
cd board-frontend
npm install
npm run dev
```
- 접속 주소: http://localhost:5173

### 4. 테스트
```bash
cd board-backand
gradlew test
```

## API 명세

| Method | URL | 설명 | 인증 |
|---|---|---|---|
| POST | `/api/members/signup` | 회원가입 | - |
| POST | `/api/members/login` | 로그인 | - |
| POST | `/api/members/logout` | 로그아웃 | - |
| GET | `/api/members/me` | 내 정보 | 필요 |
| GET | `/api/posts?keyword=&page=&size=` | 글 목록 (검색, 페이징) | - |
| GET | `/api/posts/{id}` | 글 상세 (조회수 +1) | - |
| POST | `/api/posts` | 글 작성 | 필요 |
| PUT | `/api/posts/{id}` | 글 수정 | 작성자 본인 |
| DELETE | `/api/posts/{id}` | 글 삭제 | 작성자 본인 |
| GET | `/api/posts/{postId}/comments` | 댓글 목록 | - |
| POST | `/api/posts/{postId}/comments` | 댓글 작성 | 필요 |
| DELETE | `/api/comments/{id}` | 댓글 삭제 | 작성자 본인 |

### 에러 응답 형식
```json
{
  "status": 400,
  "message": "입력값이 올바르지 않습니다.",
  "errors": { "title": "제목을 입력해주세요." },
  "timestamp": "2026-11-03T14:32:19"
}
```
`400` 입력값 오류 · `401` 로그인 필요 · `403` 권한 없음 · `404` 대상 없음 · `409` 아이디 중복 · `500` 서버 오류(상세 내용은 서버 로그에만 기록)

## 설계에서 신경 쓴 점

- 작성자는 클라이언트가 보낸 값이 아니라 **세션의 로그인 회원**으로 결정합니다.
- 비밀번호는 BCrypt로 암호화해 저장하고, 응답에는 포함하지 않습니다.
- 로그인 실패 시 아이디/비밀번호 중 무엇이 틀렸는지 구분하지 않고 같은 메시지를 반환합니다.
- 서버 내부 오류는 사용자에게 일반 메시지만 보여주고, 원인은 로그로만 남깁니다.
- 서비스 단위 테스트와 컨트롤러 테스트로 권한 검사(401/403)를 자동 검증합니다.

## 개선 예정

- 로그인 시도 횟수 제한
- 조회수 중복 방지
- 통합 테스트 (실제 DB 연결)
