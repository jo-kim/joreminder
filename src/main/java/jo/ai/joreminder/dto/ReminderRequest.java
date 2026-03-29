package jo.ai.joreminder.dto;

import jo.ai.joreminder.domain.Priority;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReminderRequest(
        String title,
        Long listId,
        String memo,
        LocalDate dueDate,
        LocalTime dueTime,
        Priority priority
) {
}
