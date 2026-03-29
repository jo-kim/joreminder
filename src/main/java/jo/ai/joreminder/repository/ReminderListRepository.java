package jo.ai.joreminder.repository;

import jo.ai.joreminder.domain.ReminderList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReminderListRepository extends JpaRepository<ReminderList, Long> {
}
