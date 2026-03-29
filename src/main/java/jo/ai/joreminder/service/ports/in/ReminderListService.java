package jo.ai.joreminder.service.ports.in;

import jo.ai.joreminder.dto.ReminderListRequest;
import jo.ai.joreminder.dto.ReminderListResponse;

import java.util.List;

public interface ReminderListService {

    List<ReminderListResponse> findAll();

    ReminderListResponse findById(Long id);

    ReminderListResponse create(ReminderListRequest request);

    ReminderListResponse update(Long id, ReminderListRequest request);

    void delete(Long id);
}
