package jo.ai.joreminder.service;

import jo.ai.joreminder.domain.Reminder;
import jo.ai.joreminder.domain.ReminderList;
import jo.ai.joreminder.dto.ReminderRequest;
import jo.ai.joreminder.repository.ReminderListRepository;
import jo.ai.joreminder.repository.ReminderRepository;
import jo.ai.joreminder.service.ports.in.ReminderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jo.ai.joreminder.domain.Priority;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class DefaultReminderServiceTest {

    @Autowired
    private ReminderService service;

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private ReminderListRepository listRepository;

    private ReminderList savedList;

    @BeforeEach
    void setUp() {
        reminderRepository.deleteAll();
        listRepository.deleteAll();
        savedList = listRepository.save(new ReminderList("Work", "RED"));
    }

    @Nested
    @DisplayName("findByListId")
    class FindByListId {

        @Test
        @DisplayName("목록의 리마인더를 반환한다")
        void returnsReminders() {
            reminderRepository.save(new Reminder("Task 1", savedList));
            reminderRepository.save(new Reminder("Task 2", savedList));

            var result = service.findByListId(savedList.getId());

            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("존재하지 않는 목록 조회 시 예외를 던진다")
        void throwsWhenListNotFound() {
            assertThatThrownBy(() -> service.findByListId(999L))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("리마인더를 생성한다")
        void createsReminder() {
            var request = new ReminderRequest("Buy milk", savedList.getId(), null, null, null, null);

            var result = service.create(request);

            assertThat(result.id()).isNotNull();
            assertThat(result.title()).isEqualTo("Buy milk");
            assertThat(result.completed()).isFalse();
            assertThat(result.listId()).isEqualTo(savedList.getId());
        }

        @Test
        @DisplayName("상세 필드와 함께 리마인더를 생성한다")
        void createsWithDetailFields() {
            var dueDate = LocalDate.of(2026, 4, 1);
            var dueTime = LocalTime.of(9, 0);
            var request = new ReminderRequest("Task", savedList.getId(),
                    "메모", dueDate, dueTime, Priority.HIGH);

            var result = service.create(request);

            assertThat(result.memo()).isEqualTo("메모");
            assertThat(result.dueDate()).isEqualTo(dueDate);
            assertThat(result.dueTime()).isEqualTo(dueTime);
            assertThat(result.priority()).isEqualTo(Priority.HIGH);
        }

        @Test
        @DisplayName("존재하지 않는 목록에 생성 시 예외를 던진다")
        void throwsWhenListNotFound() {
            var request = new ReminderRequest("Task", 999L, null, null, null, null);

            assertThatThrownBy(() -> service.create(request))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("리마인더 제목을 수정한다")
        void updatesTitle() {
            var saved = reminderRepository.save(new Reminder("Old", savedList));
            var request = new ReminderRequest("New", savedList.getId(), null, null, null, null);

            var result = service.update(saved.getId(), request);

            assertThat(result.title()).isEqualTo("New");
        }

        @Test
        @DisplayName("상세 필드를 수정한다")
        void updatesDetailFields() {
            var saved = reminderRepository.save(new Reminder("Task", savedList));
            var request = new ReminderRequest("Task", savedList.getId(),
                    "새 메모", LocalDate.of(2026, 5, 1), LocalTime.of(14, 0), Priority.MEDIUM);

            var result = service.update(saved.getId(), request);

            assertThat(result.memo()).isEqualTo("새 메모");
            assertThat(result.dueDate()).isEqualTo(LocalDate.of(2026, 5, 1));
            assertThat(result.dueTime()).isEqualTo(LocalTime.of(14, 0));
            assertThat(result.priority()).isEqualTo(Priority.MEDIUM);
        }

        @Test
        @DisplayName("존재하지 않는 리마인더 수정 시 예외를 던진다")
        void throwsWhenNotFound() {
            var request = new ReminderRequest("New", savedList.getId(), null, null, null, null);

            assertThatThrownBy(() -> service.update(999L, request))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("toggle")
    class Toggle {

        @Test
        @DisplayName("완료 상태를 토글한다")
        void togglesCompleted() {
            var saved = reminderRepository.save(new Reminder("Task", savedList));

            var result = service.toggle(saved.getId());

            assertThat(result.completed()).isTrue();
            assertThat(result.completedAt()).isNotNull();
        }

        @Test
        @DisplayName("존재하지 않는 리마인더 토글 시 예외를 던진다")
        void throwsWhenNotFound() {
            assertThatThrownBy(() -> service.toggle(999L))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("리마인더를 삭제한다")
        void deletesReminder() {
            var saved = reminderRepository.save(new Reminder("Task", savedList));

            service.delete(saved.getId());

            assertThat(reminderRepository.findById(saved.getId())).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 리마인더 삭제 시 예외를 던진다")
        void throwsWhenNotFound() {
            assertThatThrownBy(() -> service.delete(999L))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }
}
