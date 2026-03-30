package jo.ai.joreminder.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReminderListTest {

    @Nested
    @DisplayName("생성자")
    class Constructor {

        @Test
        @DisplayName("이름과 색상을 지정하여 생성한다")
        void createWithNameAndColor() {
            var list = new ReminderList("Work", "RED");

            assertThat(list.getName()).isEqualTo("Work");
            assertThat(list.getColor()).isEqualTo("RED");
            assertThat(list.isDefault()).isFalse();
        }

        @Test
        @DisplayName("color가 null이면 기본값 BLUE가 적용된다")
        void createWithNullColor() {
            var list = new ReminderList("Personal", null);

            assertThat(list.getColor()).isEqualTo("BLUE");
        }

        @Test
        @DisplayName("생성 시 createdAt과 updatedAt이 자동 설정된다")
        void timestampsSetOnCreation() {
            var before = java.time.LocalDateTime.now();
            var list = new ReminderList("Work", "BLUE");
            var after = java.time.LocalDateTime.now();

            assertThat(list.getCreatedAt()).isBetween(before, after);
            assertThat(list.getUpdatedAt()).isBetween(before, after);
            assertThat(list.getCreatedAt()).isEqualTo(list.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("createDefault")
    class CreateDefault {

        @Test
        @DisplayName("기본 목록을 생성한다")
        void createDefaultList() {
            var list = ReminderList.createDefault("미리 알림");

            assertThat(list.getName()).isEqualTo("미리 알림");
            assertThat(list.getColor()).isEqualTo("BLUE");
            assertThat(list.isDefault()).isTrue();
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("이름과 색상을 변경한다")
        void updateFields() {
            var list = new ReminderList("Old", "BLUE");

            list.update("New", "RED");

            assertThat(list.getName()).isEqualTo("New");
            assertThat(list.getColor()).isEqualTo("RED");
        }

        @Test
        @DisplayName("color가 null이면 기존 값을 유지한다")
        void updateWithNullColorKeepsExisting() {
            var list = new ReminderList("Work", "RED");

            list.update("Work", null);

            assertThat(list.getColor()).isEqualTo("RED");
        }

        @Test
        @DisplayName("update 시 updatedAt이 갱신되고 createdAt은 유지된다")
        void updateRefreshesUpdatedAt() {
            var list = new ReminderList("Work", "BLUE");
            var originalCreatedAt = list.getCreatedAt();
            var originalUpdatedAt = list.getUpdatedAt();

            list.update("Updated", "RED");

            assertThat(list.getCreatedAt()).isEqualTo(originalCreatedAt);
            assertThat(list.getUpdatedAt()).isNotEqualTo(originalUpdatedAt);
        }
    }
}
