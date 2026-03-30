package jo.ai.joreminder.controller;

import jakarta.validation.Valid;
import jo.ai.joreminder.dto.ReminderListRequest;
import jo.ai.joreminder.dto.ReminderListResponse;
import jo.ai.joreminder.service.ports.in.ReminderListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lists")
@RequiredArgsConstructor
public class ReminderListController {

    private final ReminderListService reminderListService;

    @GetMapping
    public ResponseEntity<List<ReminderListResponse>> findAll() {
        return ResponseEntity.ok(reminderListService.findAll());
    }

    @PostMapping
    public ResponseEntity<ReminderListResponse> create(@Valid @RequestBody ReminderListRequest request) {
        var created = reminderListService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReminderListResponse> update(@PathVariable Long id,
                                                       @Valid @RequestBody ReminderListRequest request) {
        return ResponseEntity.ok(reminderListService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reminderListService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
