# Blog Application

Vue.js와 Spring Boot로 구현된 간단한 블로그 애플리케이션입니다.

## 기능

1. **메인 페이지**: 전체 글 리스트 조회
2. **상세 페이지**: 개별 글 상세 내용 보기
3. **글 작성 페이지**: 새로운 글 작성

## 기술 스택

### Backend
- Spring Boot 3.2.0
- Spring Data JPA
- H2 Database (In-memory)
- Java 17

### Frontend
- Vue.js 3
- Vue Router 4
- Axios
- HTML5/CSS3

## 실행 방법

### Docker Compose 사용 (권장)

```bash
# 프로젝트 루트 디렉토리에서 실행
docker-compose up --build

# 백그라운드 실행
docker-compose up -d --build
```

### 접속 정보
- 프론트엔드: http://localhost
- 백엔드 API: http://localhost:8080/api

### 개별 실행

#### Backend
```bash
cd backend
./mvnw spring-boot:run
```

#### Frontend
```bash
cd frontend
npm install
npm run serve
```

## API 엔드포인트

- `GET /api/posts` - 모든 글 조회
- `GET /api/posts/{id}` - 특정 글 조회
- `POST /api/posts` - 글 작성
- `PUT /api/posts/{id}` - 글 수정
- `DELETE /api/posts/{id}` - 글 삭제

## 프로젝트 구조

```
.
├── backend/                 # Spring Boot 백엔드
│   ├── src/main/java/
│   │   └── com/blog/demo/
│   │       ├── entity/      # JPA 엔티티
│   │       ├── repository/  # 데이터 접근 계층
│   │       ├── service/     # 비즈니스 로직
│   │       ├── controller/  # REST API 컨트롤러
│   │       └── dto/         # 데이터 전송 객체
│   └── Dockerfile
├── frontend/                # Vue.js 프론트엔드
│   ├── src/
│   │   ├── views/          # 페이지 컴포넌트
│   │   ├── services/       # API 호출 서비스
│   │   └── router/         # 라우팅 설정
│   └── Dockerfile
└── docker-compose.yml      # Docker Compose 설정
```