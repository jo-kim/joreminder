package jo.ai.joreminder.service;

import jo.ai.joreminder.domain.ReminderList;
import jo.ai.joreminder.dto.ReminderListRequest;
import jo.ai.joreminder.repository.ReminderListRepository;
import jo.ai.joreminder.service.ports.in.ReminderListService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class DefaultReminderListServiceTest {

    @Autowired
    private ReminderListService service;

    @Autowired
    private ReminderListRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("저장된 모든 목록을 반환한다")
        void returnsAllLists() {
            repository.save(new ReminderList("Work", "RED"));
            repository.save(new ReminderList("Personal", "BLUE"));

            var result = service.findAll();

            assertThat(result).hasSize(2);
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("ID로 목록을 조회한다")
        void returnsListById() {
            var saved = repository.save(new ReminderList("Work", "RED"));

            var result = service.findById(saved.getId());

            assertThat(result.name()).isEqualTo("Work");
            assertThat(result.color()).isEqualTo("RED");
        }

        @Test
        @DisplayName("존재하지 않는 ID 조회 시 예외를 던진다")
        void throwsWhenNotFound() {
            assertThatThrownBy(() -> service.findById(999L))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("새 목록을 생성한다")
        void createsNewList() {
            var request = new ReminderListRequest("Shopping", "GREEN");

            var result = service.create(request);

            assertThat(result.id()).isNotNull();
            assertThat(result.name()).isEqualTo("Shopping");
            assertThat(result.color()).isEqualTo("GREEN");
            assertThat(result.isDefault()).isFalse();
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("목록 이름과 색상을 수정한다")
        void updatesNameAndColor() {
            var saved = repository.save(new ReminderList("Old", "BLUE"));
            var request = new ReminderListRequest("New", "RED");

            var result = service.update(saved.getId(), request);

            assertThat(result.name()).isEqualTo("New");
            assertThat(result.color()).isEqualTo("RED");
        }

        @Test
        @DisplayName("존재하지 않는 ID 수정 시 예외를 던진다")
        void throwsWhenNotFound() {
            var request = new ReminderListRequest("New", "RED");

            assertThatThrownBy(() -> service.update(999L, request))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("목록을 삭제한다")
        void deletesList() {
            var saved = repository.save(new ReminderList("Work", "RED"));

            service.delete(saved.getId());

            assertThat(repository.findById(saved.getId())).isEmpty();
        }

        @Test
        @DisplayName("기본 목록 삭제 시 예외를 던진다")
        void throwsWhenDeletingDefault() {
            var defaultList = repository.save(ReminderList.createDefault("미리 알림"));

            assertThatThrownBy(() -> service.delete(defaultList.getId()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("기본 목록");
        }

        @Test
        @DisplayName("존재하지 않는 ID 삭제 시 예외를 던진다")
        void throwsWhenNotFound() {
            assertThatThrownBy(() -> service.delete(999L))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }
}
