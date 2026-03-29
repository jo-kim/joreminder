# JoReminder — Development Plan

> spec.md 기반, 단순한 것부터 점진적으로 기능을 추가하는 방식으로 구성.
> 각 Phase 완료 시 **동작하는 애플리케이션** 상태를 유지한다.

---

## Tech Stack Summary

| 레이어 | 기술 | 버전 | 비고 |
|--------|------|------|------|
| Runtime | Java | 25 | toolchain 설정 완료 |
| Backend Framework | Spring Boot | 4.0.3 | Jakarta EE 기반, WebMVC |
| ORM | Spring Data JPA | (Boot managed) | Repository 패턴 |
| Database | H2 | (Boot managed) | 개발: 인메모리, 이후 파일 모드 전환 가능 |
| Build | Gradle Kotlin DSL | 8.x | 이미 설정됨 |
| Utility | Lombok | (Boot managed) | @Data, @Builder 등 |
| Frontend Framework | Next.js | 15.x (latest) | App Router, RSC |
| Language | TypeScript | 5.x | strict mode |
| Styling | Tailwind CSS | 4.x | CSS 변수로 Apple 색상 토큰 관리 |
| State Management | TanStack Query | 5.x | 서버 상태 캐싱, 낙관적 업데이트 |
| DnD | @dnd-kit | latest | 드래그앤드롭 정렬 |
| Package Manager | npm | - | - |

---

## Phase 1 — Backend Foundation + 최소 프론트엔드 연결

> 목표: API 서버가 동작하고, 프론트엔드에서 리스트/리마인더를 생성·조회할 수 있는 상태

### 1-1. Backend: Entity & Repository

**기술 포인트:**
- JPA Entity 설계 시 `List`라는 이름이 `java.util.List`와 충돌하므로 Entity 클래스명은 `ReminderList`로 사용하고 `@Table(name = "list")` 매핑
- `@MappedSuperclass`로 `BaseEntity`(createdAt, updatedAt) 공통 추출 — `@PrePersist`, `@PreUpdate` 콜백으로 자동 관리
- Reminder의 `parent` 필드는 이 Phase에서 컬럼만 생성하고, 실제 서브태스크 로직은 Phase 4에서 구현

**작업:**
1. `BaseEntity` 생성 — id, createdAt, updatedAt
2. `ReminderList` Entity — name, color(기본 BLUE), icon(기본 list.bullet), displayOrder
3. `Reminder` Entity — title, memo, dueDate, priority(Enum), isCompleted, isFlagged, displayOrder, completedAt, parent(nullable FK)
4. `Priority` Enum — NONE, LOW, MEDIUM, HIGH
5. `ListRepository`, `ReminderRepository` — JpaRepository 상속
6. ReminderRepository에 커스텀 쿼리 메서드 추가:
   - `findByListIdAndParentIsNullOrderByDisplayOrder()` — 리스트별 최상위 리마인더
   - `findByIsCompletedFalse()` — 전체 미완료
   - `findByDueDate(LocalDate)` — 오늘 마감
   - `findByDueDateIsNotNull()` — 예정됨
   - `findByIsFlaggedTrue()` — 깃발
   - `findByIsCompletedTrue()` — 완료됨

### 1-2. Backend: Service & Controller

**기술 포인트:**
- Controller → Service → Repository 3-layer 구조
- DTO는 Java `record`로 정의하여 immutable하게 관리 (Java 25에서 record는 완전히 안정적)
- 리스트 삭제 시 `cascade = CascadeType.ALL, orphanRemoval = true`로 소속 리마인더 자동 삭제
- `@CrossOrigin` 또는 WebMvcConfigurer로 CORS 설정 (localhost:3000 허용)

**작업:**
1. Request/Response DTO — `ListRequest`, `ListResponse`, `ReminderRequest`, `ReminderResponse` (record)
2. `ListService` — CRUD + reorder
3. `ReminderService` — CRUD + 완료 토글 + 스마트 리스트 조회
4. `ListController` (`/api/lists/**`)
5. `ReminderController` (`/api/reminders/**`, `/api/lists/{listId}/reminders/**`)
6. `SummaryController` (`/api/summary`) — 각 스마트 리스트 카운트 반환
7. CORS 설정 — `WebMvcConfigurer` 구현
8. H2 콘솔 활성화 (`spring.h2.console.enabled=true`)
9. `data.sql`로 샘플 데이터 시드 (리스트 3개, 리마인더 10개 정도)

### 1-3. Frontend: 프로젝트 초기화 + API 연결

**기술 포인트:**
- `frontend/` 디렉토리에 Next.js App Router 프로젝트 생성
- Tailwind CSS 설정 시 `globals.css`에 Apple 색상 토큰을 CSS 변수로 정의 → `tailwind.config.ts`에서 참조
- API client는 단순 fetch wrapper로 시작 (`lib/api.ts`), TanStack Query는 Phase 2에서 도입
- `next.config.ts`에서 `/api/**` 프록시 설정 → 백엔드 `localhost:8080`으로 rewrites

**작업:**
1. `npx create-next-app@latest frontend` — TypeScript, Tailwind, App Router, src/ 디렉토리
2. Apple 색상 토큰 CSS 변수 설정 (`globals.css`)
3. Tailwind config에 커스텀 색상 매핑
4. TypeScript 타입 정의 (`lib/types.ts`) — List, Reminder, Priority, SummaryResponse
5. API client (`lib/api.ts`) — fetch 기반 CRUD 함수들
6. `next.config.ts` rewrites 설정 (API 프록시)
7. 기본 2-column 레이아웃 (`app/layout.tsx`) — 사이드바 + 메인 영역 뼈대
8. 사이드바: 리스트 목록 표시 (API에서 가져오기)
9. 메인 영역: 선택한 리스트의 리마인더 목록 표시

### Phase 1 완료 상태
- 백엔드: 모든 REST API 동작, H2 콘솔에서 데이터 확인 가능
- 프론트엔드: 사이드바에 리스트 표시, 리스트 클릭 시 리마인더 목록 표시
- 아직 스타일링은 기본 수준, CRUD는 조회만

---

## Phase 2 — CRUD 완성 + Apple UI 기본 적용

> 목표: 리스트와 리마인더의 생성·수정·삭제가 모두 동작하고, Apple Reminders 스타일이 적용된 상태

### 2-1. Frontend: TanStack Query 도입

**기술 포인트:**
- `QueryClientProvider`를 App Router의 client component로 감싸기
- 각 API 호출을 custom hook으로 래핑 (`hooks/useList.ts`, `hooks/useReminders.ts`)
- 리마인더 완료 토글은 **낙관적 업데이트(Optimistic Update)** 적용 — 체크 즉시 UI 반영, 실패 시 롤백
- `staleTime: 30_000`으로 불필요한 재요청 방지

**작업:**
1. TanStack Query 설치 및 Provider 설정
2. `hooks/useLists.ts` — useQuery, useMutation (create, update, delete)
3. `hooks/useReminders.ts` — useQuery, useMutation (create, update, delete, toggle complete)
4. `hooks/useSummary.ts` — 스마트 리스트 카운트
5. 완료 토글 낙관적 업데이트 구현

### 2-2. 리스트 CRUD UI

**작업:**
1. `+ Add List` 버튼 → 리스트 생성 모달 (이름 + 색상 선택 + 아이콘 선택)
2. 사이드바 리스트 행: 색상 원형 아이콘 + 이름 + 카운트
3. 리스트 컨텍스트 메뉴 (우클릭 또는 ... 버튼) — 편집, 삭제
4. 리스트 편집 모달 (생성 모달 재사용)
5. 리스트 삭제 확인 다이얼로그

### 2-3. 리마인더 CRUD UI

**작업:**
1. 리마인더 행 컴포넌트 (`ReminderRow`) — 체크 원형 + 제목 + 메모 미리보기 + 메타 정보
2. 인라인 새 리마인더 입력 (목록 하단, Enter로 생성)
3. 체크박스 완료 토글 + 애니메이션 (원 채움 → strikethrough → fade-out)
4. 리마인더 클릭 → 인라인 상세 편집 패널 확장
5. 상세 패널: 제목, 메모, 마감일, 우선순위, 깃발, 리스트 이동, 삭제
6. 외부 클릭 또는 Esc로 패널 닫기 (자동 저장 — debounced PUT)

### 2-4. Apple UI 스타일 적용

**기술 포인트:**
- `system-ui, -apple-system, BlinkMacSystemFont` 폰트 스택으로 macOS에서 SF Pro 자동 적용
- 체크 원형 버튼은 SVG circle + CSS transition으로 구현
- 구분선은 `border-b`에 `ml-11` (44px 오프셋) — Apple 스타일

**작업:**
1. Typography 적용 (spec 5.9 기준)
2. Spacing & Sizing 적용 (spec 5.10 기준)
3. 사이드바 배경 `bg-secondary`, 카드/모달 `rounded-xl`
4. 리마인더 행 구분선 (좌측 오프셋)
5. 선택 상태 하이라이트 (`bg-tertiary`, `rounded-lg`)
6. 모달: `backdrop-blur`, scale 애니메이션

### Phase 2 완료 상태
- 리스트 생성/편집/삭제 동작
- 리마인더 생성/편집/삭제/완료 동작
- Apple Reminders 느낌의 UI (색상, 폰트, 간격, 애니메이션)
- 사이드바 리스트 + 메인 리마인더 목록이 연동

---

## Phase 3 — 스마트 리스트 + 검색

> 목표: Today, Scheduled, All, Flagged, Completed 스마트 리스트가 동작하고, 검색이 가능한 상태

### 3-1. 스마트 리스트 카드 그리드

**기술 포인트:**
- 스마트 리스트는 URL 라우팅으로 관리: `/smart/today`, `/smart/scheduled` 등
- 사이드바 상단에 2x2 + 1 그리드 배치 (CSS Grid: `grid-cols-2`)
- Summary API 호출로 카운트 표시, polling 또는 mutation 후 invalidate로 실시간 반영

**작업:**
1. 스마트 리스트 카드 컴포넌트 (아이콘 원형 배경 + 카운트 + 라벨)
2. 사이드바 상단 2열 그리드 배치
3. Today 뷰: 오늘 마감 리마인더 목록 (리스트별 그룹핑)
4. Scheduled 뷰: 날짜순 정렬, 날짜별 그룹 헤더
5. All 뷰: 리스트별 그룹핑
6. Flagged 뷰: 깃발 리마인더 목록
7. Completed 뷰: 완료 시각 역순
8. 각 뷰에서도 리마인더 CRUD, 완료 토글 동작

### 3-2. 검색

**기술 포인트:**
- 검색은 사이드바 상단 input → 300ms debounce 후 API 호출
- 검색 활성화 시 메인 영역이 검색 결과로 전환
- 백엔드: `LIKE '%keyword%'` 쿼리 (title, memo 대상) — H2에서는 충분한 성능
- ReminderRepository에 `@Query` 어노테이션으로 JPQL 검색 쿼리 추가

**작업:**
1. 백엔드: 검색 API 구현 (`/api/reminders/search?q=`)
2. 사이드바 검색 바 컴포넌트
3. 검색 결과 뷰 (리마인더 목록, 검색어 하이라이트)
4. 검색어 입력 시 debounced API 호출
5. 빈 검색 결과 empty state
6. 검색 중 사이드바 리스트 선택 유지 X → 검색 모드로 전환

### Phase 3 완료 상태
- 사이드바 상단에 스마트 리스트 카드 5개 (카운트 실시간 반영)
- 각 스마트 리스트 클릭 시 필터링된 리마인더 표시
- 검색 바에서 실시간 검색 동작

---

## Phase 4 — 서브태스크 + 드래그앤드롭 정렬

> 목표: 리마인더 하위에 서브태스크를 추가할 수 있고, 리스트와 리마인더 순서를 드래그로 변경할 수 있는 상태

### 4-1. 하위 리마인더 (서브태스크)

**기술 포인트:**
- Reminder Entity의 `parent` FK를 활용한 self-referencing 관계
- 프론트엔드에서는 트리 구조로 렌더링 — 들여쓰기(indent) 레벨 1단계만 지원 (Apple 동일)
- 부모 완료 시 자식도 함께 완료할지는 UX 판단 → Apple 방식: 독립적 완료

**작업:**
1. 백엔드: 서브 리마인더 생성 API (`POST /api/reminders/{parentId}/sub`)
2. 백엔드: 리마인더 조회 시 children 포함 응답
3. 프론트엔드: ReminderRow에 children 렌더링 (들여쓰기)
4. 프론트엔드: 리마인더 컨텍스트 메뉴에 "하위 리마인더 추가" 항목
5. 인라인 서브태스크 입력 UI (부모 아래에 들여쓰기된 입력 행)

### 4-2. 드래그앤드롭 정렬

**기술 포인트:**
- `@dnd-kit/core` + `@dnd-kit/sortable` 사용
- 드래그 중 아이템에 `shadow-lg` + 약간의 `rotate` 효과 (Apple 스타일)
- 드롭 완료 시 변경된 순서를 `PATCH /api/lists/reorder` 또는 `PATCH /api/reminders/reorder`로 일괄 전송
- Reorder API는 `[{id, displayOrder}]` 배열을 받아 bulk update

**작업:**
1. @dnd-kit 설치
2. 사이드바 리스트 드래그앤드롭 정렬
3. 리마인더 목록 드래그앤드롭 정렬
4. 드래그 오버레이 스타일 (shadow, rotate)
5. 드롭 후 서버 동기화 (reorder API 호출)
6. 서브태스크는 부모 내에서만 정렬 가능

### Phase 4 완료 상태
- 리마인더에 서브태스크 추가/관리 가능
- 사이드바 리스트 순서 드래그로 변경
- 리마인더 순서 드래그로 변경
- 드래그 시 Apple 스타일 시각 피드백

---

## Phase 5 — 컨텍스트 메뉴 + 키보드 + 반응형

> 목표: 컨텍스트 메뉴, 키보드 단축키, 반응형 레이아웃으로 완성도를 높인 상태

### 5-1. 컨텍스트 메뉴

**기술 포인트:**
- `onContextMenu` 이벤트로 커스텀 메뉴 표시 (브라우저 기본 메뉴 차단)
- 메뉴 포지셔닝: 클릭 좌표 기반, 화면 밖 넘침 방지
- 서브메뉴 지원 (우선순위, 마감일 빠른 설정, 리스트 이동)

**작업:**
1. ContextMenu 공통 컴포넌트 (포지셔닝 + 외부 클릭 닫기)
2. 리마인더 컨텍스트 메뉴 (spec 5.11 항목)
3. 리스트 컨텍스트 메뉴 (편집, 삭제)
4. 마감일 빠른 설정 서브메뉴 (오늘 / 내일 / 이번 주말 / 사용자 지정)

### 5-2. 키보드 단축키

**작업:**
1. `⌘+N` — 새 리마인더 입력 포커스
2. `Enter` — 리마인더 생성 확정 → 다음 행 자동 입력
3. `Esc` — 편집 취소, 상세 패널 닫기
4. `⌘+Backspace` — 선택된 리마인더 삭제
5. `⌘+F` — 검색 바 포커스
6. `↑/↓` — 리마인더 간 포커스 이동

### 5-3. 반응형 레이아웃

**기술 포인트:**
- 모바일(< 768px): 사이드바를 오버레이 드로어로 전환, 햄버거 메뉴 토글
- 태블릿(768px~1024px): 사이드바 축소 (아이콘만 표시)
- 데스크탑(> 1024px): 풀 사이드바

**작업:**
1. 모바일 사이드바 드로어 (slide-in + backdrop)
2. 햄버거 메뉴 버튼 (모바일 헤더)
3. 태블릿 축소 사이드바
4. 터치 제스처 대응 (스와이프로 사이드바 토글)

### Phase 5 완료 상태
- 우클릭 컨텍스트 메뉴로 빠른 조작
- 키보드만으로 리마인더 관리 가능
- 모바일/태블릿에서도 사용 가능

---

## Phase 6 — Dark Mode + Empty States + Polish

> 목표: 프로덕션 수준의 완성도. 다크 모드, 빈 상태, 세부 애니메이션 마무리

### 6-1. Dark Mode

**기술 포인트:**
- Tailwind의 `dark:` variant + `prefers-color-scheme` 미디어 쿼리
- CSS 변수 기반이므로 `:root`와 `@media (prefers-color-scheme: dark)` 블록만 전환
- 수동 토글 불필요 — OS 설정 자동 추종 (Apple 방식)

**작업:**
1. `globals.css`에 dark 색상 변수 블록 추가
2. 전체 컴포넌트 dark 모드 검증 및 미세 조정
3. 그림자/구분선 강도 dark 대응

### 6-2. Empty States

**작업:**
1. 리마인더 없는 리스트 → 중앙 아이콘 + "No Reminders" 텍스트
2. 검색 결과 없음 → 돋보기 아이콘 + "No Results"
3. 리스트 없음 → "Get started by adding a list" 유도
4. 각 스마트 리스트 빈 상태 메시지

### 6-3. 최종 Polish

**작업:**
1. 애니메이션 미세 조정 (spec 5.8 duration 검증)
2. 로딩 상태 — 스켈레톤 UI (사이드바, 리마인더 목록)
3. 에러 상태 — API 실패 시 toast 알림
4. favicon, 페이지 타이틀 설정
5. H2 파일 모드 전환 옵션 (데이터 영속성)
6. 전체 UI 크로스 브라우저 테스트 (Chrome, Safari, Firefox)

### Phase 6 완료 상태
- 다크 모드 자동 적용
- 모든 빈 상태에 적절한 UI 표시
- 로딩/에러 상태 처리
- **Apple Reminders 앱과 유사한 완성된 웹 애플리케이션**

---

## 실행 순서 요약

```
Phase 1  Backend API + 최소 프론트엔드 연결
  ↓      (동작하는 API + 데이터 표시)
Phase 2  CRUD 완성 + Apple UI 스타일
  ↓      (생성/수정/삭제 + 시각적 완성도)
Phase 3  스마트 리스트 + 검색
  ↓      (Today/Scheduled/All/Flagged/Completed + 검색)
Phase 4  서브태스크 + 드래그앤드롭
  ↓      (하위 리마인더 + 순서 변경)
Phase 5  컨텍스트 메뉴 + 키보드 + 반응형
  ↓      (파워 유저 기능 + 모바일 대응)
Phase 6  Dark Mode + Empty States + Polish
         (프로덕션 수준 완성)
```
