package jo.ai.joreminder.dto;

import jo.ai.joreminder.domain.Reminder;

import java.time.LocalDateTime;

public record ReminderResponse(
        Long id,
        String title,
        boolean completed,
        Long listId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ReminderResponse from(Reminder reminder) {
        return new ReminderResponse(
                reminder.getId(),
                reminder.getTitle(),
                reminder.isCompleted(),
                reminder.getList().getId(),
                reminder.getCreatedAt(),
                reminder.getUpdatedAt()
        );
    }
}
