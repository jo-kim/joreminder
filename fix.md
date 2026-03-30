# JoReminder — Code Review Fix List

---

## Backend

### Critical

- [x] **B-C1.** DTO 입력 검증 추가 — `ReminderRequest`, `ReminderListRequest`에 `@NotBlank`/`@NotNull` 어노테이션 추가, Controller에 `@Valid` 적용
- [x] **B-C2.** N+1 쿼리 해결 — `DefaultReminderListService.findAll()`에서 목록별 카운트를 단일 쿼리로 변경 (`@Query` 또는 `countByListId` 배치 조회)
- [x] **B-C3.** Reminder 생성 로직 정리 — 생성자 호출 직후 불필요한 조건부 `update()` 제거, 생성 시 상세 필드를 받는 생성자 또는 팩토리 메서드 추가

### Major

- [x] **B-M1.** `findById()`에서 reminderCount 0 고정 수정 — `findAll()`과 동일하게 카운트 포함
- [ ] **B-M2.** 목록 삭제 시 cascade 처리 — `CascadeType.REMOVE` 설정 또는 서비스에서 소속 리마인더 명시적 삭제
- [x] **B-M3.** `GlobalExceptionHandler`에 `HttpMessageNotReadableException` 핸들러 추가 (400 응답)
- [ ] **B-M4.** 단건 조회 API 추가 — `GET /api/reminders/{id}`, `GET /api/lists/{id}`

### Minor

- [x] **B-m1.** `GlobalExceptionHandler` — `IllegalStateException` 매핑을 400 → 409로 변경
- [ ] **B-m2.** `application.properties` — profile 분리하여 프로덕션에서 `ddl-auto=validate`, `show-sql=false` 적용
- [ ] **B-m3.** `BaseEntity` — 타임존 명시 (`LocalDateTime.now()` → `Instant` 또는 `ZonedDateTime` 고려)
- [ ] **B-m4.** `displayOrder` 업데이트 API 추가 — 도메인에 메서드는 있지만 서비스/컨트롤러 미노출

---

## Frontend

### Critical

- [ ] **F-C1.** API 에러 핸들링 — 모든 async 핸들러에 try/catch 추가, 에러 상태 UI 표시 (toast 또는 에러 메시지)
- [ ] **F-C2.** `loadLists`의 `selectedId` 의존성 순환 수정 — 초기 선택 로직 분리하여 불필요한 재호출 방지
- [ ] **F-C3.** `ReminderRow` click-outside `useEffect` — 의존성 배열 정리, 매 렌더마다 이벤트 리스너 등록/해제 반복 제거

### Major

- [ ] **F-M1.** `api.ts:13` — `undefined as T` 캐스팅 개선 (반환 타입 안전성 확보)
- [ ] **F-M2.** `ConfirmDialog` / `ListModal`에 Escape 키 닫기 지원
- [ ] **F-M3.** 접근성 개선 — 사이드바 목록 항목을 `<button>` 또는 `role="button"` + `tabIndex`로 변경, 체크박스에 `aria-label` 추가
- [ ] **F-M4.** `colorSwatchSelected`의 `currentColor` box-shadow — inline `backgroundColor`와 충돌, 명시적 색상으로 수정

### Minor

- [ ] **F-m1.** `ReminderRow` 날짜 포맷 — `new Date(d + "T00:00:00")` 타임존 안전하게 처리
- [ ] **F-m2.** `ListModal` prop 타입 정리 — `list?: ReminderList | null` 중복 제거
- [ ] **F-m3.** 로딩 상태 UI 추가 — 목록/리마인더 로딩 시 스피너 또는 스켈레톤
- [ ] **F-m4.** `ConfirmDialog` 확인 버튼 텍스트 — "삭제" 하드코딩 → prop으로 커스텀 가능하게

---

## Test

### High

- [ ] **T-H1.** `Thread.sleep(10)` 제거 — `ReminderTest`, `ReminderListTest`의 타이밍 의존 테스트를 `isNotEqualTo` 비교 또는 `Clock` 주입으로 변경
- [ ] **T-H2.** 컨트롤러 테스트에 Phase 5 필드 검증 추가 — POST/PUT 응답에서 memo, dueDate, dueTime, priority 확인
- [ ] **T-H3.** 서비스 테스트 `toggle()` — `completedAt` 설정/해제 검증 추가

### Medium

- [ ] **T-M1.** 도메인 테스트 — null 필드 부분 업데이트 테스트 (memo만 변경, dueDate만 클리어 등)
- [ ] **T-M2.** 컨트롤러 테스트 — 잘못된 JSON 요청(필수 필드 누락) 시 400 응답 검증
- [ ] **T-M3.** `displayOrder` 관련 테스트 추가 (도메인 + 서비스)
- [ ] **T-M4.** 목록 삭제 시 소속 리마인더 처리 테스트 (cascade 또는 에러 검증)
