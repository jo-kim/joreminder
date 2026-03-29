# JoReminder - Apple Reminders Web Clone PRD

## 1. Overview

Apple Reminder 앱의 핵심 기능을 웹으로 구현한 개인용 할 일 관리 애플리케이션.

- **Backend**: Spring Boot 4.0.3 / JPA / H2 (REST API)
- **Frontend**: Next.js (App Router, latest)
- **인증**: 없음 (단일 사용자, 로컬 사용)

---

## 2. Core Features

### 2.1 리스트 (List) 관리

| 기능 | 설명 |
|------|------|
| 리스트 CRUD | 리스트 생성, 조회, 수정, 삭제 |
| 리스트 색상 | 12가지 프리셋 색상 중 선택 |
| 리스트 아이콘 | 프리셋 아이콘 중 선택 |
| 리스트 정렬 | 드래그앤드롭으로 순서 변경 |

### 2.2 리마인더 (Reminder) 관리

| 기능 | 설명 |
|------|------|
| 리마인더 CRUD | 리마인더 생성, 조회, 수정, 삭제 |
| 완료 토글 | 체크박스로 완료/미완료 전환 |
| 우선순위 | 없음 / 낮음 / 보통 / 높음 (4단계) |
| 마감일 | 날짜 설정 (선택) |
| 메모 | 추가 텍스트 메모 (선택) |
| 하위 리마인더 | 리마인더 하위에 서브태스크 추가 |
| 리스트 내 정렬 | 드래그앤드롭으로 순서 변경 |

### 2.3 스마트 리스트 (Smart Lists)

서버에서 필터링하여 자동 집계되는 가상 리스트:

| 스마트 리스트 | 조건 |
|--------------|------|
| **Today** | 마감일이 오늘인 리마인더 |
| **Scheduled** | 마감일이 설정된 모든 리마인더 |
| **All** | 모든 미완료 리마인더 |
| **Completed** | 완료된 리마인더 |
| **Flagged** | 깃발 표시된 리마인더 |

### 2.4 검색

- 리마인더 제목/메모 텍스트 검색
- 실시간 필터링 (debounced)

---

## 3. Data Model

### List

| 필드 | 타입 | 설명 |
|------|------|------|
| id | Long (PK) | 자동 생성 |
| name | String | 리스트 이름 (필수) |
| color | String | 색상 코드 (기본: BLUE) |
| icon | String | 아이콘 이름 (기본: list.bullet) |
| displayOrder | Integer | 정렬 순서 |
| createdAt | LocalDateTime | 생성일 |
| updatedAt | LocalDateTime | 수정일 |

### Reminder

| 필드 | 타입 | 설명 |
|------|------|------|
| id | Long (PK) | 자동 생성 |
| list | List (FK) | 소속 리스트 (필수) |
| parent | Reminder (FK) | 상위 리마인더 (nullable, 서브태스크용) |
| title | String | 제목 (필수) |
| memo | String (TEXT) | 메모 |
| dueDate | LocalDate | 마감일 |
| priority | Enum | NONE, LOW, MEDIUM, HIGH |
| isCompleted | Boolean | 완료 여부 (기본: false) |
| isFlagged | Boolean | 깃발 표시 (기본: false) |
| displayOrder | Integer | 리스트 내 정렬 순서 |
| completedAt | LocalDateTime | 완료 시각 |
| createdAt | LocalDateTime | 생성일 |
| updatedAt | LocalDateTime | 수정일 |

---

## 4. API Design

Base path: `/api`

### Lists

| Method | Path | 설명 |
|--------|------|------|
| GET | /lists | 전체 리스트 조회 |
| POST | /lists | 리스트 생성 |
| GET | /lists/{id} | 리스트 상세 조회 |
| PUT | /lists/{id} | 리스트 수정 |
| DELETE | /lists/{id} | 리스트 삭제 (소속 리마인더 포함) |
| PATCH | /lists/reorder | 리스트 순서 변경 |

### Reminders

| Method | Path | 설명 |
|--------|------|------|
| GET | /lists/{listId}/reminders | 리스트별 리마인더 조회 |
| POST | /lists/{listId}/reminders | 리마인더 생성 |
| GET | /reminders/{id} | 리마인더 상세 조회 |
| PUT | /reminders/{id} | 리마인더 수정 |
| DELETE | /reminders/{id} | 리마인더 삭제 |
| PATCH | /reminders/{id}/complete | 완료 토글 |
| PATCH | /reminders/reorder | 순서 변경 |

### Smart Lists

| Method | Path | 설명 |
|--------|------|------|
| GET | /reminders/today | 오늘 마감 리마인더 |
| GET | /reminders/scheduled | 마감일 있는 리마인더 |
| GET | /reminders/all | 전체 미완료 리마인더 |
| GET | /reminders/completed | 완료된 리마인더 |
| GET | /reminders/flagged | 깃발 표시 리마인더 |

### Search

| Method | Path | 설명 |
|--------|------|------|
| GET | /reminders/search?q={query} | 텍스트 검색 |

### Summary (사이드바 카운트용)

| Method | Path | 설명 |
|--------|------|------|
| GET | /summary | 스마트 리스트별 카운트 반환 |

---

## 5. UI/UX Design — Apple Reminders 준수

> 디자인 원칙: macOS/iOS Apple Reminders 앱의 시각적 언어와 인터랙션 패턴을 최대한 충실히 재현한다.

### 기술 스택

- Next.js (App Router)
- TypeScript
- Tailwind CSS
- React Query (서버 상태 관리)
- SF Pro / system-ui 폰트 스택

### 5.1 Color System

Apple Reminders의 시스템 색상을 그대로 사용:

| 토큰 | Light | Dark | 용도 |
|------|-------|------|------|
| --bg-primary | #FFFFFF | #1C1C1E | 메인 배경 |
| --bg-secondary | #F2F2F7 | #2C2C2E | 사이드바, 카드 배경 |
| --bg-tertiary | #E5E5EA | #3A3A3C | hover, 구분선 |
| --text-primary | #000000 | #FFFFFF | 제목, 본문 |
| --text-secondary | #8E8E93 | #8E8E93 | 보조 텍스트, 날짜 |
| --accent-blue | #007AFF | #0A84FF | 기본 강조, 링크 |
| --accent-red | #FF3B30 | #FF453A | 삭제, 오늘 카운트 |
| --accent-orange | #FF9500 | #FF9F0A | 우선순위 높음 |
| --accent-green | #34C759 | #30D158 | 완료 체크 |

리스트 프리셋 색상 (12종):
`RED, ORANGE, YELLOW, GREEN, CYAN, BLUE, INDIGO, PURPLE, PINK, BROWN, GRAY, TEAL`

### 5.2 Layout — 2-Column (Sidebar + Content)

```
┌──────────────────────────────────────────────────────────────────┐
│ ← Sidebar (280px, bg-secondary)  │  Main Content (flex-1)       │
│                                  │                               │
│  🔍 Search                       │  ┌─ List Header ───────────┐ │
│                                  │  │ 🔵 Personal         ... │ │
│  ┌─ Smart Lists (2x2 Grid) ───┐ │  │     7 reminders          │ │
│  │ ┌──────────┐ ┌──────────┐  │ │  └────────────────────────┘ │
│  │ │📅 Today  │ │📋 Sched. │  │ │                               │
│  │ │    3     │ │    5     │  │ │  ○  Buy groceries             │
│  │ └──────────┘ └──────────┘  │ │     Notes: Milk, eggs...      │
│  │ ┌──────────┐ ┌──────────┐  │ │     📅 Today   🚩             │
│  │ │📁 All    │ │🚩Flagged │  │ │                               │
│  │ │   12     │ │    2     │  │ │  ○  Call dentist              │
│  │ └──────────┘ └──────────┘  │ │     📅 Tomorrow               │
│  │          ┌──────────┐      │ │                               │
│  │          │✅Completed│      │ │  ● ̶P̶a̶y̶ ̶b̶i̶l̶l̶s̶  (completed)   │
│  │          └──────────┘      │ │                               │
│  └────────────────────────────┘ │  ○  Pick up package           │
│                                  │     └ ○  Check tracking      │
│  ── My Lists ──────────────────  │     └ ○  Confirm address     │
│  │ 🔴  Work                 4 │ │                               │
│  │ 🔵  Personal             3 │ │                               │
│  │ 🟢  Shopping             5 │ │  ──────────────────────────── │
│  └─────────────────────────────  │  ○  (인라인 새 리마인더 입력)  │
│                                  │                               │
│  [+ Add List]                    │                               │
└──────────────────────────────────────────────────────────────────┘
```

### 5.3 Smart Lists — 카드 그리드

- 사이드바 상단에 **2열 그리드** 형태로 배치 (Apple Reminders 동일)
- 각 카드: 좌측 상단 아이콘 (원형 배경), 우측 상단 카운트 (bold), 하단 라벨
- 카드 배경: `bg-primary` (white/dark), 모서리 `rounded-xl`, 미세한 `shadow-sm`
- Completed 카드는 하단 중앙에 단독 배치 (홀수이므로)

| 카드 | 아이콘 배경 | 아이콘 |
|------|-----------|--------|
| Today | #007AFF | 캘린더 (오늘 날짜 숫자) |
| Scheduled | #FF3B30 | 캘린더 |
| All | #5856D6 | 트레이 |
| Flagged | #FF9500 | 깃발 |
| Completed | #8E8E93 | 체크마크 |

### 5.4 My Lists — 리스트 목록

- 각 행: `[색상 원형 아이콘 (24px)] [리스트 이름] [카운트 (text-secondary)]`
- 아이콘: 선택한 색상의 채워진 원 안에 SF Symbol 스타일 아이콘 (흰색)
- 선택된 리스트: `bg-tertiary` 배경 + `rounded-lg`
- 하단 `+ Add List` 버튼: `text-secondary`, hover 시 `text-accent-blue`

### 5.5 Main Content — 리마인더 목록

#### 리스트 헤더
- 리스트 이름: 리스트 색상으로 표시, **34px bold**
- 리스트 아이콘: 이름 좌측
- `...` 메뉴 버튼 (우측): 리스트 편집, 삭제 등

#### 리마인더 행 (ReminderRow)

```
┌──────────────────────────────────────────────────┐
│  ○   Title text here                         🚩  │
│      Notes preview in secondary text...          │
│      📅 Mar 29   !! High                         │
│      └ ○  Sub-reminder 1                         │
│      └ ○  Sub-reminder 2                         │
└──────────────────────────────────────────────────┘
```

- **체크 원형 버튼** (좌측): 빈 원(`○`) — 리스트 색상 테두리, hover 시 연한 채움
- 완료 시: 원이 리스트 색상으로 채워지며 체크마크 애니메이션 → 0.5초 후 strikethrough + fade out
- **제목**: 16px regular, 완료 시 `line-through` + `text-secondary`
- **메모 미리보기**: 14px `text-secondary`, 1줄 truncate
- **메타 정보 행**: 마감일 (📅), 우선순위 (! 표시, 색상), 깃발 (🚩)
- **우선순위 표시**: LOW=`!` MEDIUM=`!!` HIGH=`!!!` (각각 blue, orange, red)
- **서브태스크**: 들여쓰기 + 동일한 행 구조

#### 인라인 새 리마인더 입력
- 목록 최하단에 항상 빈 입력 행 표시
- 포커스 시 확장되어 제목 + 부가정보 입력 가능
- Enter → 생성 후 다음 빈 행 자동 포커스
- Esc → 입력 취소

### 5.6 리마인더 상세 편집 (Detail Panel)

리마인더 클릭 시 해당 행이 **인라인 확장**되어 상세 편집 UI 표시 (Apple 방식):

```
┌──────────────────────────────────────────────────┐
│  ○   [Title 편집 가능 input]                     │
│      ┌─────────────────────────────────────────┐ │
│      │ Notes textarea                          │ │
│      │                                         │ │
│      └─────────────────────────────────────────┘ │
│                                                  │
│      📅  Date        │  Mar 29, 2026       [x]  │
│      🚩  Flagged     │  ○ toggle               │ │
│      !!  Priority    │  [None|Low|Med|High]     │ │
│      📋  List        │  [Personal ▼]           │ │
│      🗑️  Delete Reminder                         │
└──────────────────────────────────────────────────┘
```

- 각 필드는 Apple Settings 스타일의 `grouped row`
- Date picker: 네이티브 date input 또는 캘린더 팝오버
- List 이동: 드롭다운으로 다른 리스트로 이동 가능
- 외부 클릭 또는 Esc → 패널 닫기 (자동 저장)

### 5.7 리스트 생성/편집 모달

```
┌─────────── New List ──────────────┐
│                                   │
│         🔵 (큰 아이콘 미리보기)    │
│                                   │
│   [리스트 이름 입력]              │
│                                   │
│   ── Color ──                     │
│   🔴🟠🟡🟢🩵🔵🟣💜🩷🟤⚫🩶     │
│                                   │
│   ── Icon ──                      │
│   📋📌🔖📁🎯🛒💼🏠🎓💡🔔⭐   │
│                                   │
│   [Cancel]            [Done]      │
└───────────────────────────────────┘
```

- 중앙 정렬 모달, `backdrop-blur` 배경
- 색상: 12개 원형 버튼 그리드 (선택 시 체크마크 오버레이)
- 아이콘: 12개 프리셋 아이콘 그리드
- 미리보기: 상단에 선택한 색상+아이콘 조합 실시간 표시

### 5.8 Animations & Transitions

| 인터랙션 | 애니메이션 | Duration |
|----------|-----------|----------|
| 완료 체크 | 원 채움 + 체크마크 scale-in → strikethrough → fade-out | 500ms |
| 리마인더 삭제 | slide-left + fade-out | 300ms |
| 리마인더 추가 | fade-in + slide-down | 200ms |
| 상세 패널 열기 | 행 높이 확장 (height transition) | 250ms |
| 사이드바 리스트 선택 | bg-color fade | 150ms |
| 모달 열기 | scale(0.95→1) + opacity + backdrop-blur | 200ms |
| 드래그 중 | 드래그 대상 `shadow-lg` + 약간 기울어짐 | - |

### 5.9 Typography

| 요소 | 크기 | 굵기 | 색상 |
|------|------|------|------|
| 스마트 리스트 카운트 | 28px | Bold | text-primary |
| 스마트 리스트 라벨 | 14px | Medium | text-secondary |
| 리스트 이름 (사이드바) | 15px | Regular | text-primary |
| 리스트 제목 (헤더) | 34px | Bold | 리스트 색상 |
| 리마인더 제목 | 16px | Regular | text-primary |
| 리마인더 메모/날짜 | 14px | Regular | text-secondary |
| 입력 placeholder | 16px | Regular | text-secondary |

### 5.10 Spacing & Sizing

| 요소 | 값 |
|------|------|
| 사이드바 너비 | 280px |
| 스마트 리스트 카드 | 130px × 90px, gap 10px |
| 리스트 행 높이 | 36px |
| 리마인더 행 최소 높이 | 44px |
| 체크 원형 버튼 | 22px |
| 리스트 색상 원형 (사이드바) | 24px |
| 콘텐츠 좌우 패딩 | 20px |
| 리마인더 간 구분선 | 1px, bg-tertiary, 좌측 44px 오프셋 |

### 5.11 Context Menus

우클릭 또는 `...` 버튼으로 표시되는 Apple 스타일 컨텍스트 메뉴:

**리마인더 컨텍스트 메뉴:**
- 상세 편집 열기
- 마감일 설정 → (오늘 / 내일 / 이번 주말 / 사용자 지정)
- 깃발 토글
- 우선순위 → (없음 / 낮음 / 보통 / 높음)
- 리스트 이동 → (리스트 목록)
- 하위 리마인더 추가
- 구분선
- 삭제 (빨간색)

**리스트 컨텍스트 메뉴:**
- 리스트 편집
- 구분선
- 삭제 (빨간색)

### 5.12 Empty States

- **리스트에 리마인더 없음**: 중앙에 리스트 아이콘 (연한 색상, 64px) + "No Reminders" 텍스트
- **검색 결과 없음**: 돋보기 아이콘 + "No Results"
- **리스트 없음**: "+ Add List" 유도 텍스트

### 5.13 Dark Mode

- `prefers-color-scheme: dark` 미디어 쿼리로 자동 전환
- 위 Color System 표의 Dark 컬럼 값 적용
- 사이드바/카드의 `bg-secondary`가 어두운 톤으로 전환
- 구분선, 그림자 강도 조절

### 5.14 Keyboard Shortcuts

| 단축키 | 동작 |
|--------|------|
| `⌘ + N` | 새 리마인더 (현재 리스트) |
| `Enter` | 리마인더 생성 확정 / 다음 행 |
| `Esc` | 편집 취소 / 상세 패널 닫기 |
| `⌘ + Backspace` | 선택된 리마인더 삭제 |
| `⌘ + F` | 검색 포커스 |
| `↑ / ↓` | 리마인더 간 이동 |

---

## 6. Project Structure

```
joreminder/
├── src/main/java/jo/ai/joreminder/
│   ├── JoreminderApplication.java
│   ├── list/
│   │   ├── List.java              (Entity)
│   │   ├── ListRepository.java
│   │   ├── ListService.java
│   │   └── ListController.java
│   ├── reminder/
│   │   ├── Reminder.java          (Entity)
│   │   ├── Priority.java          (Enum)
│   │   ├── ReminderRepository.java
│   │   ├── ReminderService.java
│   │   └── ReminderController.java
│   └── summary/
│       └── SummaryController.java
├── frontend/
│   ├── src/app/                   (App Router)
│   ├── src/components/
│   ├── src/lib/                   (API client, types)
│   └── package.json
└── build.gradle.kts
```

---

## 7. Implementation Phases

### Phase 1 - Backend Core

1. Entity, Repository 구현 (List, Reminder)
2. Service 레이어 구현
3. REST Controller 구현
4. H2 설정 및 초기 데이터 seed
5. CORS 설정

### Phase 2 - Frontend Core

1. Next.js 프로젝트 초기화 (frontend/)
2. 사이드바 레이아웃 + 스마트 리스트 카운트
3. 리스트 CRUD UI
4. 리마인더 CRUD UI (인라인 편집)
5. 완료 토글 + 애니메이션

### Phase 3 - Enhanced Features

1. 하위 리마인더 (서브태스크)
2. 드래그앤드롭 정렬
3. 검색 기능
4. 리스트 색상/아이콘 선택 UI

### Phase 4 - Polish

1. 반응형 레이아웃 (모바일 사이드바 토글)
2. 키보드 단축키 (Enter로 생성, Esc로 취소)
3. 빈 상태 일러스트
4. 다크 모드

---

## 8. Non-Goals (Scope Out)

- 사용자 인증/멀티 유저
- 푸시 알림
- 반복 리마인더
- 태그 기능
- 리스트 그룹 (폴더)
- 위치 기반 리마인더
- 이미지/파일 첨부
- iCloud 동기화
