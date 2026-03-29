package jo.ai.joreminder.service;

import jo.ai.joreminder.domain.ReminderList;
import jo.ai.joreminder.dto.ReminderListRequest;
import jo.ai.joreminder.dto.ReminderListResponse;
import jo.ai.joreminder.repository.ReminderListRepository;
import jo.ai.joreminder.service.ports.in.ReminderListService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DefaultReminderListService implements ReminderListService {

    private final ReminderListRepository reminderListRepository;

    @Override
    public List<ReminderListResponse> findAll() {
        return reminderListRepository.findAll().stream()
                .map(ReminderListResponse::from)
                .toList();
    }

    @Override
    public ReminderListResponse findById(Long id) {
        var list = getById(id);
        return ReminderListResponse.from(list);
    }

    @Override
    @Transactional
    public ReminderListResponse create(ReminderListRequest request) {
        var list = new ReminderList(request.name(), request.color());
        reminderListRepository.save(list);
        return ReminderListResponse.from(list);
    }

    @Override
    @Transactional
    public ReminderListResponse update(Long id, ReminderListRequest request) {
        var list = getById(id);
        list.update(request.name(), request.color());
        return ReminderListResponse.from(list);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        var list = getById(id);
        if (list.isDefault()) {
            throw new IllegalStateException("기본 목록은 삭제할 수 없습니다.");
        }
        reminderListRepository.delete(list);
    }

    private ReminderList getById(Long id) {
        return reminderListRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("목록을 찾을 수 없습니다. id=" + id));
    }
}
