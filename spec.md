# JoReminder - Apple Reminders Web Clone PRD

Apple Reminders 앱의 핵심 기능을 웹으로 구현한 할 일 관리 애플리케이션.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Spring Boot 4.0.3, Java 25, JPA, H2 |
| Frontend | Next.js (latest), React, TypeScript |
| API | REST JSON |

## 핵심 기능

### 1. 리마인더 (Reminder)

- 리마인더 생성 / 수정 / 삭제
- 제목 (필수), 메모 (선택)
- 마감일 & 마감시간 (선택)
- 우선순위: 없음 / 낮음 / 보통 / 높음
- 완료 체크 / 해제 (토글)
- 완료된 리마인더 숨기기 / 보기

### 2. 목록 (List)

- 목록 생성 / 수정 / 삭제
- 목록 이름, 색상 (프리셋 색상 중 선택)
- 리마인더는 반드시 하나의 목록에 속함
- 기본 목록: "미리 알림" (삭제 불가)

### 3. 스마트 목록 (Smart List)

서버에서 필터링하여 제공하는 가상 목록 (읽기 전용):

- **오늘**: 마감일이 오늘인 리마인더
- **예정**: 마감일이 설정된 모든 미완료 리마인더 (날짜순)
- **전체**: 모든 미완료 리마인더
- **완료됨**: 완료 처리된 리마인더

### 4. UI/UX (Apple Reminders 디자인 준수)

Apple Reminders 앱의 룩앤필을 최대한 충실히 재현한다.

#### 전체 레이아웃
- 좌측 사이드바 (240px 고정) + 우측 메인 콘텐츠 영역
- 사이드바 배경: 밝은 회색 (`#F5F5F7`), 메인 영역: 흰색
- 사이드바와 메인 영역 사이 미세한 구분선

#### 사이드바
- 상단: 스마트 목록 카드 그리드 (2x2)
    - 오늘 (파란색 아이콘), 예정 (빨간색), 전체 (검정), 완료됨 (회색)
    - 각 카드에 원형 아이콘 + 이름 + 우측 상단 카운트 숫자
- 하단: "나의 목록" 섹션
    - 각 목록 앞에 목록 색상의 원형 불릿
    - 우측에 리마인더 카운트
- 최하단: "+ 목록 추가" 버튼

#### 메인 영역 - 리마인더 목록
- 상단: 목록 이름 (목록 색상, 큰 볼드 타이틀)
- 리마인더 행:
    - 좌측: 원형 체크박스 (목록 색상 테두리, 완료 시 색상 채움 + 체크마크)
    - 체크박스 우측: 제목 텍스트 (완료 시 취소선 + 회색)
    - 제목 아래: 마감일/메모 등 서브텍스트 (작은 회색 글씨)
    - 행 사이 얇은 구분선
- 목록 최하단: "+ 새로운 미리 알림" 버튼 (목록 색상 텍스트)

#### 리마인더 상세 편집
- 리마인더 클릭 시 해당 행이 확장되어 인라인 편집 모드
- 제목, 메모, 마감일, 마감시간, 우선순위 필드 표시
- 우선순위: 느낌표 아이콘 (! / !! / !!!)
- 행 외부 클릭 시 편집 모드 닫힘

#### 목록 생성/수정 모달
- 중앙 모달 다이얼로그
- 목록 이름 입력 + 색상 선택 (12색 프리셋 원형 팔레트)
- 프리셋 색상: 빨강, 주황, 노랑, 초록, 청록, 파랑, 남색, 보라, 분홍, 갈색, 회색, 감청

#### 인터랙션
- 완료 토글 시 체크 애니메이션 (scale bounce)
- 완료된 리마인더는 0.5초 후 부드럽게 사라짐 (fade + collapse)
- 목록/리마인더 hover 시 미세한 배경색 변화
- 삭제 시 확인 다이얼로그

#### 색상 & 타이포그래피
- 시스템 폰트 스택 (`-apple-system, BlinkMacSystemFont, 'SF Pro', ...`)
- 목록 타이틀: 28px bold
- 리마인더 제목: 15px regular
- 서브텍스트: 13px, `#8E8E93`
- 전반적으로 Apple Human Interface Guidelines의 간결하고 깔끔한 톤

## 데이터 모델

### ReminderList

| Field | Type | 비고 |
|-------|------|------|
| id | Long | PK, auto |
| name | String | 필수 |
| color | String | hex 또는 프리셋 이름 |
| isDefault | boolean | 기본 목록 여부 |
| createdAt | LocalDateTime | |
| updatedAt | LocalDateTime | |

### Reminder

| Field | Type | 비고 |
|-------|------|------|
| id | Long | PK, auto |
| title | String | 필수 |
| memo | String | nullable |
| dueDate | LocalDate | nullable |
| dueTime | LocalTime | nullable |
| priority | Enum | NONE, LOW, MEDIUM, HIGH |
| completed | boolean | default false |
| completedAt | LocalDateTime | nullable |
| listId | Long | FK -> ReminderList |
| displayOrder | int | 목록 내 정렬 순서 |
| createdAt | LocalDateTime | |
| updatedAt | LocalDateTime | |

## API 설계

### Lists

| Method | Path | 설명 |
|--------|------|------|
| GET | /api/lists | 전체 목록 조회 (리마인더 개수 포함) |
| POST | /api/lists | 목록 생성 |
| PUT | /api/lists/{id} | 목록 수정 |
| DELETE | /api/lists/{id} | 목록 삭제 (기본 목록 불가) |

### Reminders

| Method | Path | 설명 |
|--------|------|------|
| GET | /api/lists/{listId}/reminders | 특정 목록의 리마인더 조회 |
| GET | /api/reminders/today | 오늘 마감 리마인더 |
| GET | /api/reminders/scheduled | 마감일 있는 미완료 리마인더 |
| GET | /api/reminders/all | 전체 미완료 리마인더 |
| GET | /api/reminders/completed | 완료된 리마인더 |
| POST | /api/reminders | 리마인더 생성 |
| PUT | /api/reminders/{id} | 리마인더 수정 |
| PATCH | /api/reminders/{id}/toggle | 완료 토글 |
| DELETE | /api/reminders/{id} | 리마인더 삭제 |

## 제외 범위 (MVP 이후)

- 사용자 인증 / 로그인
- 반복 리마인더
- 태그
- 위치 기반 알림
- 첨부파일 / 이미지
- 서브태스크
- 목록 공유
- Push 알림