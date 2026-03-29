package jo.ai.joreminder.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
        @DisplayName("제목을 변경한다")
        void updateTitle() {
            var reminder = new Reminder("Old", sampleList());

            reminder.update("New");

            assertThat(reminder.getTitle()).isEqualTo("New");
        }

        @Test
        @DisplayName("update 시 updatedAt이 갱신된다")
        void updateRefreshesUpdatedAt() throws InterruptedException {
            var reminder = new Reminder("Task", sampleList());
            var originalUpdatedAt = reminder.getUpdatedAt();

            Thread.sleep(10);
            reminder.update("Updated");

            assertThat(reminder.getUpdatedAt()).isAfter(originalUpdatedAt);
        }
    }

    @Nested
    @DisplayName("toggleCompleted")
    class ToggleCompleted {

        @Test
        @DisplayName("미완료 → 완료로 토글한다")
        void toggleToCompleted() {
            var reminder = new Reminder("Task", sampleList());

            reminder.toggleCompleted();

            assertThat(reminder.isCompleted()).isTrue();
        }

        @Test
        @DisplayName("완료 → 미완료로 토글한다")
        void toggleBackToIncomplete() {
            var reminder = new Reminder("Task", sampleList());
            reminder.toggleCompleted();

            reminder.toggleCompleted();

            assertThat(reminder.isCompleted()).isFalse();
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
}
