# JoReminder — Task List

---

## Phase 1 — Backend Foundation + 최소 프론트엔드 연결

### 1-1. Backend: Entity & Repository
- [ ] `BaseEntity` 생성 (id, createdAt, updatedAt — `@MappedSuperclass`)
- [ ] `ReminderList` Entity (name, color, icon, displayOrder — `@Table(name = "list")`)
- [ ] `Reminder` Entity (title, memo, dueDate, priority, isCompleted, isFlagged, displayOrder, completedAt, parent FK)
- [ ] `Priority` Enum (NONE, LOW, MEDIUM, HIGH)
- [ ] `ListRepository` — JpaRepository 상속
- [ ] `ReminderRepository` — JpaRepository 상속 + 커스텀 쿼리 메서드
  - [ ] `findByListIdAndParentIsNullOrderByDisplayOrder()`
  - [ ] `findByIsCompletedFalse()`
  - [ ] `findByDueDate(LocalDate)`
  - [ ] `findByDueDateIsNotNull()`
  - [ ] `findByIsFlaggedTrue()`
  - [ ] `findByIsCompletedTrue()`

### 1-2. Backend: Service & Controller
- [ ] Request/Response DTO (`ListRequest`, `ListResponse`, `ReminderRequest`, `ReminderResponse` — Java record)
- [ ] `ListService` — CRUD + reorder
- [ ] `ReminderService` — CRUD + 완료 토글 + 스마트 리스트 조회
- [ ] `ListController` (`/api/lists/**`)
- [ ] `ReminderController` (`/api/reminders/**`, `/api/lists/{listId}/reminders/**`)
- [ ] `SummaryController` (`/api/summary`)
- [ ] CORS 설정 (`WebMvcConfigurer`, localhost:3000 허용)
- [ ] H2 콘솔 활성화 (`spring.h2.console.enabled=true`)
- [ ] `data.sql` 샘플 데이터 시드 (리스트 3개, 리마인더 ~10개)

### 1-3. Frontend: 프로젝트 초기화 + API 연결
- [ ] Next.js 프로젝트 생성 (`frontend/` — TypeScript, Tailwind, App Router)
- [ ] Apple 색상 토큰 CSS 변수 설정 (`globals.css`)
- [ ] Tailwind config 커스텀 색상 매핑
- [ ] TypeScript 타입 정의 (`lib/types.ts`)
- [ ] API client (`lib/api.ts`) — fetch 기반 CRUD 함수
- [ ] `next.config.ts` rewrites 설정 (API 프록시 → localhost:8080)
- [ ] 기본 2-column 레이아웃 (`app/layout.tsx`)
- [ ] 사이드바: 리스트 목록 표시
- [ ] 메인 영역: 선택한 리스트의 리마인더 목록 표시

---

## Phase 2 — CRUD 완성 + Apple UI 기본 적용

### 2-1. TanStack Query 도입
- [ ] TanStack Query 설치 및 `QueryClientProvider` 설정
- [ ] `hooks/useLists.ts` (useQuery, useMutation — create, update, delete)
- [ ] `hooks/useReminders.ts` (useQuery, useMutation — create, update, delete, toggle)
- [ ] `hooks/useSummary.ts` (스마트 리스트 카운트)
- [ ] 완료 토글 낙관적 업데이트 구현

### 2-2. 리스트 CRUD UI
- [ ] `+ Add List` 버튼 → 리스트 생성 모달 (이름 + 색상 + 아이콘)
- [ ] 사이드바 리스트 행 (색상 원형 아이콘 + 이름 + 카운트)
- [ ] 리스트 컨텍스트 메뉴 (... 버튼 — 편집, 삭제)
- [ ] 리스트 편집 모달 (생성 모달 재사용)
- [ ] 리스트 삭제 확인 다이얼로그

### 2-3. 리마인더 CRUD UI
- [ ] `ReminderRow` 컴포넌트 (체크 원형 + 제목 + 메모 미리보기 + 메타 정보)
- [ ] 인라인 새 리마인더 입력 (목록 하단, Enter로 생성)
- [ ] 체크박스 완료 토글 + 애니메이션 (원 채움 → strikethrough → fade-out)
- [ ] 리마인더 클릭 → 인라인 상세 편집 패널 확장
- [ ] 상세 패널: 제목, 메모, 마감일, 우선순위, 깃발, 리스트 이동, 삭제
- [ ] 외부 클릭 / Esc → 패널 닫기 (자동 저장 — debounced PUT)

### 2-4. Apple UI 스타일 적용
- [ ] Typography 적용 (spec 5.9)
- [ ] Spacing & Sizing 적용 (spec 5.10)
- [ ] 사이드바 배경 `bg-secondary`, 카드/모달 `rounded-xl`
- [ ] 리마인더 행 구분선 (좌측 44px 오프셋)
- [ ] 선택 상태 하이라이트 (`bg-tertiary`, `rounded-lg`)
- [ ] 모달: `backdrop-blur`, scale 애니메이션

---

## Phase 3 — 스마트 리스트 + 검색

### 3-1. 스마트 리스트 카드 그리드
- [ ] 스마트 리스트 카드 컴포넌트 (아이콘 원형 배경 + 카운트 + 라벨)
- [ ] 사이드바 상단 2열 그리드 배치
- [ ] Today 뷰 (오늘 마감, 리스트별 그룹핑)
- [ ] Scheduled 뷰 (날짜순 정렬, 날짜별 그룹 헤더)
- [ ] All 뷰 (리스트별 그룹핑)
- [ ] Flagged 뷰 (깃발 리마인더 목록)
- [ ] Completed 뷰 (완료 시각 역순)
- [ ] 각 뷰에서 리마인더 CRUD + 완료 토글 동작 확인

### 3-2. 검색
- [ ] 백엔드: 검색 API (`/api/reminders/search?q=` — JPQL LIKE 쿼리)
- [ ] 사이드바 검색 바 컴포넌트
- [ ] 검색 결과 뷰 (리마인더 목록 + 검색어 하이라이트)
- [ ] debounced API 호출 (300ms)
- [ ] 빈 검색 결과 empty state
- [ ] 검색 모드 진입/해제 상태 관리

---

## Phase 4 — 서브태스크 + 드래그앤드롭 정렬

### 4-1. 하위 리마인더 (서브태스크)
- [ ] 백엔드: 서브 리마인더 생성 API (`POST /api/reminders/{parentId}/sub`)
- [ ] 백엔드: 리마인더 조회 시 children 포함 응답
- [ ] 프론트엔드: ReminderRow에 children 렌더링 (들여쓰기)
- [ ] 컨텍스트 메뉴에 "하위 리마인더 추가" 항목
- [ ] 인라인 서브태스크 입력 UI

### 4-2. 드래그앤드롭 정렬
- [ ] @dnd-kit 설치 (`@dnd-kit/core`, `@dnd-kit/sortable`)
- [ ] 사이드바 리스트 드래그앤드롭 정렬
- [ ] 리마인더 목록 드래그앤드롭 정렬
- [ ] 드래그 오버레이 스타일 (shadow-lg + rotate)
- [ ] 드롭 후 서버 동기화 (reorder API)
- [ ] 서브태스크 부모 내 정렬 제한

---

## Phase 5 — 컨텍스트 메뉴 + 키보드 + 반응형

### 5-1. 컨텍스트 메뉴
- [ ] ContextMenu 공통 컴포넌트 (포지셔닝 + 외부 클릭 닫기)
- [ ] 리마인더 컨텍스트 메뉴 (상세 편집, 마감일, 깃발, 우선순위, 리스트 이동, 서브 추가, 삭제)
- [ ] 리스트 컨텍스트 메뉴 (편집, 삭제)
- [ ] 마감일 빠른 설정 서브메뉴 (오늘 / 내일 / 이번 주말 / 사용자 지정)

### 5-2. 키보드 단축키
- [ ] `⌘+N` → 새 리마인더 입력 포커스
- [ ] `Enter` → 리마인더 생성 확정, 다음 행 자동 입력
- [ ] `Esc` → 편집 취소, 상세 패널 닫기
- [ ] `⌘+Backspace` → 선택된 리마인더 삭제
- [ ] `⌘+F` → 검색 바 포커스
- [ ] `↑/↓` → 리마인더 간 포커스 이동

### 5-3. 반응형 레이아웃
- [ ] 모바일 사이드바 드로어 (slide-in + backdrop)
- [ ] 햄버거 메뉴 버튼 (모바일 헤더)
- [ ] 태블릿 축소 사이드바 (아이콘만)
- [ ] 터치 제스처 (스와이프로 사이드바 토글)

---

## Phase 6 — Dark Mode + Empty States + Polish

### 6-1. Dark Mode
- [ ] `globals.css` dark 색상 변수 블록 추가
- [ ] 전체 컴포넌트 dark 모드 검증 및 미세 조정
- [ ] 그림자/구분선 강도 dark 대응

### 6-2. Empty States
- [ ] 리마인더 없는 리스트 → 아이콘 + "No Reminders"
- [ ] 검색 결과 없음 → 돋보기 + "No Results"
- [ ] 리스트 없음 → "Get started by adding a list"
- [ ] 각 스마트 리스트 빈 상태 메시지

### 6-3. 최종 Polish
- [ ] 애니메이션 미세 조정 (spec 5.8 duration)
- [ ] 로딩 스켈레톤 UI (사이드바, 리마인더 목록)
- [ ] 에러 상태 toast 알림
- [ ] favicon + 페이지 타이틀
- [ ] H2 파일 모드 전환 옵션
- [ ] 크로스 브라우저 테스트 (Chrome, Safari, Firefox)
