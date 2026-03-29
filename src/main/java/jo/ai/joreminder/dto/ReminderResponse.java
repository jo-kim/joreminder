package jo.ai.joreminder.dto;

import jo.ai.joreminder.domain.Priority;
import jo.ai.joreminder.domain.Reminder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ReminderResponse(
        Long id,
        String title,
        String memo,
        boolean completed,
        LocalDateTime completedAt,
        LocalDate dueDate,
        LocalTime dueTime,
        Priority priority,
        int displayOrder,
        Long listId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ReminderResponse from(Reminder reminder) {
        return new ReminderResponse(
                reminder.getId(),
                reminder.getTitle(),
                reminder.getMemo(),
                reminder.isCompleted(),
                reminder.getCompletedAt(),
                reminder.getDueDate(),
                reminder.getDueTime(),
                reminder.getPriority(),
                reminder.getDisplayOrder(),
                reminder.getList().getId(),
                reminder.getCreatedAt(),
                reminder.getUpdatedAt()
        );
    }
}
