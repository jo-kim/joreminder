package jo.ai.joreminder.service;

import jo.ai.joreminder.domain.Reminder;
import jo.ai.joreminder.dto.ReorderRequest;
import jo.ai.joreminder.dto.ReminderRequest;
import jo.ai.joreminder.dto.ReminderResponse;
import jo.ai.joreminder.repository.ReminderListRepository;
import jo.ai.joreminder.repository.ReminderRepository;
import jo.ai.joreminder.service.ports.in.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DefaultReminderService implements ReminderService {

    private final ReminderRepository reminderRepository;
    private final ReminderListRepository reminderListRepository;

    @Override
    public List<ReminderResponse> findByListId(Long listId) {
        if (!reminderListRepository.existsById(listId)) {
            throw new NoSuchElementException("목록을 찾을 수 없습니다. id=" + listId);
        }
        return reminderRepository.findByListIdOrderByCreatedAt(listId).stream()
                .map(ReminderResponse::from)
                .toList();
    }

    @Override
    public ReminderResponse findById(Long id) {
        return ReminderResponse.from(getById(id));
    }

    @Override
    @Transactional
    public ReminderResponse create(ReminderRequest request) {
        var list = reminderListRepository.findById(request.listId())
                .orElseThrow(() -> new NoSuchElementException("목록을 찾을 수 없습니다. id=" + request.listId()));
        var reminder = new Reminder(request.title(), list,
                request.memo(), request.dueDate(), request.dueTime(), request.priority());
        reminderRepository.save(reminder);
        return ReminderResponse.from(reminder);
    }

    @Override
    @Transactional
    public ReminderResponse update(Long id, ReminderRequest request) {
        var reminder = getById(id);
        reminder.update(request.title(), request.memo(),
                request.dueDate(), request.dueTime(), request.priority());
        return ReminderResponse.from(reminder);
    }

    @Override
    @Transactional
    public ReminderResponse toggle(Long id) {
        var reminder = getById(id);
        reminder.toggleCompleted();
        return ReminderResponse.from(reminder);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        var reminder = getById(id);
        reminderRepository.delete(reminder);
    }

    @Override
    @Transactional
    public void reorder(List<ReorderRequest> requests) {
        for (var req : requests) {
            var reminder = getById(req.id());
            reminder.updateDisplayOrder(req.displayOrder());
        }
    }

    private Reminder getById(Long id) {
        return reminderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("리마인더를 찾을 수 없습니다. id=" + id));
    }
}
