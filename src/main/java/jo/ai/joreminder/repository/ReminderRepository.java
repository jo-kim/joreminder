package jo.ai.joreminder.repository;

import jo.ai.joreminder.domain.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    List<Reminder> findByListIdOrderByCreatedAt(Long listId);

    int countByListIdAndCompletedFalse(Long listId);
}
