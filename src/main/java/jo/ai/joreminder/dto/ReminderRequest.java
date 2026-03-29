package jo.ai.joreminder.dto;

public record ReminderRequest(
        String title,
        Long listId
) {
}
