package jo.ai.joreminder.controller;

import jo.ai.joreminder.dto.ReminderRequest;
import jo.ai.joreminder.dto.ReminderResponse;
import jo.ai.joreminder.service.ports.in.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;

    @GetMapping("/api/lists/{listId}/reminders")
    public ResponseEntity<List<ReminderResponse>> findByListId(@PathVariable Long listId) {
        return ResponseEntity.ok(reminderService.findByListId(listId));
    }

    @PostMapping("/api/reminders")
    public ResponseEntity<ReminderResponse> create(@RequestBody ReminderRequest request) {
        var created = reminderService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/api/reminders/{id}")
    public ResponseEntity<ReminderResponse> update(@PathVariable Long id,
                                                   @RequestBody ReminderRequest request) {
        return ResponseEntity.ok(reminderService.update(id, request));
    }

    @PatchMapping("/api/reminders/{id}/toggle")
    public ResponseEntity<ReminderResponse> toggle(@PathVariable Long id) {
        return ResponseEntity.ok(reminderService.toggle(id));
    }

    @DeleteMapping("/api/reminders/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reminderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
