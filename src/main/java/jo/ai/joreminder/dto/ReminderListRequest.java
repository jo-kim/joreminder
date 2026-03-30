package jo.ai.joreminder.dto;

import jakarta.validation.constraints.NotBlank;

public record ReminderListRequest(
        @NotBlank String name,
        String color
) {
}
