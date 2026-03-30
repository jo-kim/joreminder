package jo.ai.joreminder.repository;

import jo.ai.joreminder.domain.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    List<Reminder> findByListIdOrderByCreatedAt(Long listId);

    int countByListIdAndCompletedFalse(Long listId);

    @Query("SELECT r.list.id, COUNT(r) FROM Reminder r WHERE r.completed = false GROUP BY r.list.id")
    List<Object[]> countByListGrouped();
}
