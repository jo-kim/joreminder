# JoReminder — Task List

---
## Phase 1: Backend 기본 CRUD

### 1-1. Entity & Repository
- [x] `ReminderList` 엔티티 생성 (id, name, color, isDefault, createdAt, updatedAt)
- [x] `Reminder` 엔티티 생성 (id, title, completed, listId, createdAt, updatedAt)
- [x] `ReminderListRepository` 인터페이스 생성
- [x] `ReminderRepository` 인터페이스 생성

### 1-2. 목록 API
- [x] `ReminderListService` 구현
- [x] `ReminderListController` 구현
- [x] GET /api/lists — 전체 목록 조회 (리마인더 개수 포함)
- [x] POST /api/lists — 목록 생성
- [x] PUT /api/lists/{id} — 목록 수정
- [x] DELETE /api/lists/{id} — 목록 삭제 (기본 목록 보호)

### 1-3. 리마인더 API
- [x] `ReminderService` 구현
- [x] `ReminderController` 구현
- [x] GET /api/lists/{listId}/reminders — 목록별 리마인더 조회
- [x] POST /api/reminders — 리마인더 생성
- [x] PUT /api/reminders/{id} — 리마인더 수정
- [x] PATCH /api/reminders/{id}/toggle — 완료 토글
- [x] DELETE /api/reminders/{id} — 리마인더 삭제

### 1-4. 초기 데이터 & 검증
- [x] `ApplicationRunner`로 기본 목록 "미리 알림" 생성
- [x] `./gradlew build` 통과 확인
- [x] curl로 전체 CRUD 동작 확인

---

## Phase 2: Frontend 기본 레이아웃 + 목록 표시

### 2-1. 프로젝트 설정
- [x] `frontend/`에 Next.js 프로젝트 생성 (TypeScript, App Router)
- [x] `next.config.ts`에 API proxy 설정 (/api → localhost:8080)
- [x] API 호출 유틸 함수 작성

### 2-2. 레이아웃
- [x] 사이드바 + 메인 영역 2단 레이아웃 컴포넌트
- [x] 사이드바에 목록 표시 (색상 불릿 + 이름 + 카운트)
- [x] 목록 클릭 시 선택 상태 관리 (URL 또는 state)

### 2-3. 리마인더 표시
- [x] 메인 영역에 선택된 목록의 리마인더 표시
- [x] 목록 타이틀 (목록 색상, 볼드)
- [x] 리마인더 행: 원형 체크박스 + 제목
- [x] 완료 토글 클릭 시 API 호출 + UI 반영

---

## Phase 3: 리마인더 CRUD UI

### 3-1. 리마인더 생성
- [x] "+ 새로운 미리 알림" 버튼 UI
- [x] 클릭 시 새 행 추가 + 제목 입력 필드 포커스
- [x] Enter 키로 저장 (POST API 호출)
- [x] Escape 키로 취소

### 3-2. 리마인더 인라인 편집
- [x] 리마인더 클릭 시 행 확장 → 제목 편집 모드
- [x] 행 외부 클릭 시 저장 (PUT API 호출) + 편집 모드 닫힘

### 3-3. 리마인더 삭제
- [x] 리마인더 hover 시 삭제 버튼 표시
- [x] 삭제 확인 다이얼로그
- [x] DELETE API 호출 + UI에서 제거

---

## Phase 4: 목록 CRUD UI

### 4-1. 목록 생성 모달
- [x] "+ 목록 추가" 버튼 UI
- [x] 모달 컴포넌트: 이름 입력 필드
- [x] 12색 프리셋 원형 팔레트 색상 선택기
- [x] 생성 완료 시 POST API 호출 + 사이드바 갱신

### 4-2. 목록 수정/삭제
- [x] 목록 우클릭 시 컨텍스트 메뉴 (수정 / 삭제)
- [x] 수정 시 생성과 동일한 모달 재사용 (PUT API)
- [x] 삭제 시 확인 다이얼로그 + DELETE API 호출
- [x] 기본 목록("미리 알림") 삭제 메뉴 비활성화

---

## Phase 5: Reminder 상세 필드 추가

### 5-1. Backend 필드 추가
- [x] Reminder 엔티티에 memo, dueDate, dueTime 필드 추가
- [x] Priority enum 생성 (NONE, LOW, MEDIUM, HIGH)
- [x] completedAt 필드 추가 (토글 시 자동 설정)
- [x] displayOrder 필드 추가
- [x] DTO 및 Service/Controller 수정

### 5-2. 인라인 편집 확장
- [x] 리마인더 확장 영역에 메모 입력 필드 추가
- [x] 마감일 date picker 추가
- [x] 마감시간 time picker 추가
- [x] 우선순위 선택 UI (느낌표 아이콘: ! / !! / !!!)

### 5-3. 서브텍스트 표시
- [x] 리마인더 행에 마감일 미리보기 (작은 회색 글씨)
- [x] 리마인더 행에 메모 미리보기 (한 줄, 말줄임)

---

## Phase 6: 스마트 목록

### 6-1. Backend API
- [ ] GET /api/reminders/today — 마감일이 오늘인 리마인더
- [ ] GET /api/reminders/scheduled — 마감일 있는 미완료 리마인더 (날짜순)
- [ ] GET /api/reminders/all — 전체 미완료 리마인더
- [ ] GET /api/reminders/completed — 완료된 리마인더
- [ ] 각 스마트 목록 카운트 API (또는 목록 조회 시 포함)

### 6-2. 사이드바 스마트 목록 카드
- [ ] 2x2 그리드 카드 레이아웃
- [ ] 오늘 카드 (파란색 아이콘 + 카운트)
- [ ] 예정 카드 (빨간색 아이콘 + 카운트)
- [ ] 전체 카드 (검정 아이콘 + 카운트)
- [ ] 완료됨 카드 (회색 아이콘 + 카운트)

### 6-3. 스마트 목록 메인 영역
- [ ] 스마트 목록 선택 시 해당 필터의 리마인더 표시
- [ ] "완료됨" 목록은 completedAt 역순 정렬
- [ ] 스마트 목록에서 리마인더가 속한 원래 목록 이름 표시

---

## Phase 7: Polish & 애니메이션

### 7-1. 애니메이션
- [ ] 완료 토글 시 scale bounce 체크 애니메이션
- [ ] 완료된 리마인더 0.5초 후 fade + collapse 사라짐
- [ ] 목록/리마인더 hover 시 배경색 전환 효과

### 7-2. 디자인 다듬기
- [ ] Apple 시스템 폰트 스택 적용 (-apple-system, SF Pro)
- [ ] 색상, 간격, 구분선 Apple HIG에 맞게 미세 조정
- [ ] 빈 상태 메시지 (목록에 리마인더가 없을 때)
- [ ] 로딩 스피너 / 스켈레톤 UI

### 7-3. 리마인더 드래그 정렬
- [ ] 드래그앤드롭 라이브러리 연동
- [ ] displayOrder 기반 재정렬 UI
- [ ] 순서 변경 시 PATCH API로 서버 반영