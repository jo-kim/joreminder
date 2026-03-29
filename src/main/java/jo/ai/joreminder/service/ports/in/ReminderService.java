package jo.ai.joreminder.service.ports.in;

import jo.ai.joreminder.dto.ReminderRequest;
import jo.ai.joreminder.dto.ReminderResponse;

import java.util.List;

public interface ReminderService {

    List<ReminderResponse> findByListId(Long listId);

    ReminderResponse create(ReminderRequest request);

    ReminderResponse update(Long id, ReminderRequest request);

    ReminderResponse toggle(Long id);

    void delete(Long id);
}
