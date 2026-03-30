package jo.ai.joreminder.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class ReminderTest {

    private ReminderList sampleList() {
        return new ReminderList("Work", "RED");
    }

    @Nested
    @DisplayName("생성자")
    class Constructor {

        @Test
        @DisplayName("제목과 목록을 지정하여 생성한다")
        void createWithTitleAndList() {
            var list = sampleList();
            var reminder = new Reminder("Buy milk", list);

            assertThat(reminder.getTitle()).isEqualTo("Buy milk");
            assertThat(reminder.isCompleted()).isFalse();
            assertThat(reminder.getList()).isSameAs(list);
            assertThat(reminder.getMemo()).isNull();
            assertThat(reminder.getDueDate()).isNull();
            assertThat(reminder.getDueTime()).isNull();
            assertThat(reminder.getPriority()).isEqualTo(Priority.NONE);
            assertThat(reminder.getDisplayOrder()).isZero();
            assertThat(reminder.getCompletedAt()).isNull();
        }

        @Test
        @DisplayName("상세 필드를 포함하여 생성한다")
        void createWithDetailFields() {
            var list = sampleList();
            var dueDate = LocalDate.of(2026, 4, 1);
            var dueTime = LocalTime.of(9, 30);
            var reminder = new Reminder("Task", list, "메모", dueDate, dueTime, Priority.HIGH);

            assertThat(reminder.getTitle()).isEqualTo("Task");
            assertThat(reminder.getMemo()).isEqualTo("메모");
            assertThat(reminder.getDueDate()).isEqualTo(dueDate);
            assertThat(reminder.getDueTime()).isEqualTo(dueTime);
            assertThat(reminder.getPriority()).isEqualTo(Priority.HIGH);
            assertThat(reminder.getList()).isSameAs(list);
            assertThat(reminder.getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("생성 시 타임스탬프가 자동 설정된다")
        void timestampsSetOnCreation() {
            var before = java.time.LocalDateTime.now();
            var reminder = new Reminder("Task", sampleList());
            var after = java.time.LocalDateTime.now();

            assertThat(reminder.getCreatedAt()).isBetween(before, after);
            assertThat(reminder.getUpdatedAt()).isBetween(before, after);
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("모든 필드를 변경한다")
        void updateAllFields() {
            var reminder = new Reminder("Old", sampleList());
            var dueDate = LocalDate.of(2026, 4, 1);
            var dueTime = LocalTime.of(9, 30);

            reminder.update("New", "메모입니다", dueDate, dueTime, Priority.HIGH);

            assertThat(reminder.getTitle()).isEqualTo("New");
            assertThat(reminder.getMemo()).isEqualTo("메모입니다");
            assertThat(reminder.getDueDate()).isEqualTo(dueDate);
            assertThat(reminder.getDueTime()).isEqualTo(dueTime);
            assertThat(reminder.getPriority()).isEqualTo(Priority.HIGH);
        }

        @Test
        @DisplayName("priority가 null이면 NONE으로 설정된다")
        void nullPriorityDefaultsToNone() {
            var reminder = new Reminder("Task", sampleList());
            reminder.update("Task", null, null, null, Priority.HIGH);

            reminder.update("Task", null, null, null, null);

            assertThat(reminder.getPriority()).isEqualTo(Priority.NONE);
        }

        @Test
        @DisplayName("update 시 updatedAt이 갱신된다")
        void updateRefreshesUpdatedAt() throws InterruptedException {
            var reminder = new Reminder("Task", sampleList());
            var originalUpdatedAt = reminder.getUpdatedAt();

            Thread.sleep(10);
            reminder.update("Updated", null, null, null, null);

            assertThat(reminder.getUpdatedAt()).isAfter(originalUpdatedAt);
        }
    }

    @Nested
    @DisplayName("toggleCompleted")
    class ToggleCompleted {

        @Test
        @DisplayName("미완료 → 완료로 토글하면 completedAt이 설정된다")
        void toggleToCompleted() {
            var reminder = new Reminder("Task", sampleList());

            reminder.toggleCompleted();

            assertThat(reminder.isCompleted()).isTrue();
            assertThat(reminder.getCompletedAt()).isNotNull();
        }

        @Test
        @DisplayName("완료 → 미완료로 토글하면 completedAt이 null이 된다")
        void toggleBackToIncomplete() {
            var reminder = new Reminder("Task", sampleList());
            reminder.toggleCompleted();

            reminder.toggleCompleted();

            assertThat(reminder.isCompleted()).isFalse();
            assertThat(reminder.getCompletedAt()).isNull();
        }

        @Test
        @DisplayName("토글 시 updatedAt이 갱신된다")
        void toggleRefreshesUpdatedAt() throws InterruptedException {
            var reminder = new Reminder("Task", sampleList());
            var originalUpdatedAt = reminder.getUpdatedAt();

            Thread.sleep(10);
            reminder.toggleCompleted();

            assertThat(reminder.getUpdatedAt()).isAfter(originalUpdatedAt);
        }
    }

    @Nested
    @DisplayName("updateDisplayOrder")
    class UpdateDisplayOrder {

        @Test
        @DisplayName("표시 순서를 변경한다")
        void updatesOrder() {
            var reminder = new Reminder("Task", sampleList());

            reminder.updateDisplayOrder(5);

            assertThat(reminder.getDisplayOrder()).isEqualTo(5);
        }
    }
}
