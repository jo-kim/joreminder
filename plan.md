# JoReminder — Development Plan

> spec.md 기반, 단순한 것부터 점진적으로 기능을 추가하는 방식으로 구성.
> 각 Phase 완료 시 **동작하는 애플리케이션** 상태를 유지한다.

---

## 기술 스택 상세

### Backend
- **Spring Boot 4.0.3** / Java 25
- **Spring Data JPA** + **H2** (in-memory)
- **Lombok** - 보일러플레이트 제거
- **Gradle Kotlin DSL** - 빌드
- 패키지 구조: `workspace.joreminder.{entity,repository,service,controller,dto}`

### Frontend
- **Next.js** (latest, App Router)
- **TypeScript**
- **CSS Modules** - 컴포넌트 단위 스타일링 (Apple 디자인 정밀 구현에 적합)
- 프로젝트 경로: `frontend/`
- Backend API 호출: `fetch` (Next.js 내장)

### 개발 환경
- Backend: `localhost:8080`
- Frontend: `localhost:3000` (proxy → 8080)

---

## Phase 1: Backend 기본 CRUD

가장 단순한 형태의 API를 먼저 동작시킨다.

### 1-1. Entity & Repository
- `ReminderList` 엔티티 (id, name, color, isDefault, createdAt, updatedAt)
- `Reminder` 엔티티 (id, title, completed, listId, createdAt, updatedAt)
   - 이 단계에서는 memo, dueDate, dueTime, priority, displayOrder 제외
- `ReminderListRepository`, `ReminderRepository` 생성

### 1-2. Service & Controller - 목록
- `ReminderListService` / `ReminderListController`
- GET /api/lists - 전체 목록 조회
- POST /api/lists - 목록 생성
- PUT /api/lists/{id} - 목록 수정
- DELETE /api/lists/{id} - 목록 삭제 (기본 목록 보호)

### 1-3. Service & Controller - 리마인더
- `ReminderService` / `ReminderController`
- GET /api/lists/{listId}/reminders - 목록별 리마인더 조회
- POST /api/reminders - 리마인더 생성
- PUT /api/reminders/{id} - 리마인더 수정
- PATCH /api/reminders/{id}/toggle - 완료 토글
- DELETE /api/reminders/{id} - 리마인더 삭제

### 1-4. 초기 데이터
- `ApplicationRunner`로 기본 목록 "미리 알림" 생성

### 1-5. 검증
- `./gradlew build` 통과
- curl 또는 HTTP client로 CRUD 동작 확인

---

## Phase 2: Frontend 기본 레이아웃 + 목록 표시

화면을 띄우고 Backend 데이터를 표시한다.

### 2-1. Next.js 프로젝트 생성
- `frontend/` 디렉토리에 `create-next-app` (TypeScript, App Router)
- `next.config.ts`에 API proxy 설정 (`/api` → `localhost:8080`)

### 2-2. 레이아웃 구현
- 사이드바 + 메인 영역 2단 레이아웃
- 사이드바: 나의 목록 표시 (색상 불릿 + 이름 + 카운트)
- 목록 클릭 시 메인 영역에 해당 목록의 리마인더 표시

### 2-3. 리마인더 목록 표시
- 목록 타이틀 (색상, 볼드)
- 리마인더 행: 원형 체크박스 + 제목
- 완료 토글 동작

---

## Phase 3: 리마인더 CRUD UI

리마인더 생성/수정/삭제를 UI에서 할 수 있게 한다.

### 3-1. 리마인더 생성
- "+ 새로운 미리 알림" 버튼 클릭 시 새 행 추가 + 제목 입력 포커스
- Enter로 저장, Escape로 취소

### 3-2. 리마인더 인라인 편집
- 리마인더 클릭 시 행 확장 → 제목 편집 가능
- 행 외부 클릭 시 저장 + 닫힘

### 3-3. 리마인더 삭제
- 삭제 버튼 (hover 시 표시)
- 확인 다이얼로그 후 삭제

---

## Phase 4: 목록 CRUD UI

목록을 UI에서 관리할 수 있게 한다.

### 4-1. 목록 생성 모달
- "+ 목록 추가" 버튼 → 모달 열기
- 이름 입력 + 12색 프리셋 팔레트에서 색상 선택

### 4-2. 목록 수정/삭제
- 목록 우클릭 또는 컨텍스트 메뉴 → 수정/삭제
- 기본 목록 삭제 불가 처리

---

## Phase 5: Reminder 상세 필드 추가

Reminder 엔티티에 나머지 필드를 추가하고 UI에 반영한다.

### 5-1. Backend 필드 추가
- memo (String, nullable)
- dueDate (LocalDate, nullable)
- dueTime (LocalTime, nullable)
- priority (Enum: NONE, LOW, MEDIUM, HIGH)
- completedAt (LocalDateTime, nullable)
- displayOrder (int)

### 5-2. 인라인 편집 확장
- 리마인더 클릭 시 확장 영역에 메모, 마감일, 마감시간, 우선순위 필드 추가
- 마감일: date picker
- 우선순위: 느낌표 아이콘 (! / !! / !!!)

### 5-3. 서브텍스트 표시
- 리마인더 행에 마감일/메모 미리보기 (작은 회색 글씨)

---

## Phase 6: 스마트 목록

가상 필터 목록을 추가한다.

### 6-1. Backend API
- GET /api/reminders/today - 마감일이 오늘
- GET /api/reminders/scheduled - 마감일 있는 미완료 (날짜순)
- GET /api/reminders/all - 전체 미완료
- GET /api/reminders/completed - 완료된 리마인더

### 6-2. 사이드바 스마트 목록 카드
- 상단 2x2 그리드: 오늘(파란), 예정(빨간), 전체(검정), 완료됨(회색)
- 각 카드에 원형 아이콘 + 이름 + 카운트

### 6-3. 스마트 목록 메인 영역
- 스마트 목록 선택 시 해당 필터의 리마인더 표시
- "완료됨" 목록은 completedAt 역순 정렬

---

## Phase 7: Polish & 애니메이션

Apple Reminders의 세련된 인터랙션을 구현한다.

### 7-1. 애니메이션
- 완료 토글: scale bounce 체크 애니메이션
- 완료 리마인더: 0.5초 후 fade + collapse로 사라짐
- 목록/리마인더 hover 배경색 전환

### 7-2. 디자인 다듬기
- Apple 시스템 폰트 스택 적용
- 색상, 간격, 구분선 등 Apple HIG에 맞게 미세 조정
- 빈 상태 메시지 (목록에 리마인더 없을 때)
- 로딩 상태 표시

### 7-3. 리마인더 드래그 정렬
- displayOrder 기반 드래그앤드롭 재정렬
- 순서 변경 시 PATCH API로 서버 반영