package jo.ai.joreminder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jo.ai.joreminder.domain.Priority;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReminderRequest(
        @NotBlank String title,
        @NotNull Long listId,
        String memo,
        LocalDate dueDate,
        LocalTime dueTime,
        Priority priority
) {
}
