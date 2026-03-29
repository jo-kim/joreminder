package jo.ai.joreminder.dto;

import jo.ai.joreminder.domain.ReminderList;

import java.time.LocalDateTime;

public record ReminderListResponse(
        Long id,
        String name,
        String color,
        boolean isDefault,
        int reminderCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ReminderListResponse from(ReminderList list, int reminderCount) {
        return new ReminderListResponse(
                list.getId(),
                list.getName(),
                list.getColor(),
                list.isDefault(),
                reminderCount,
                list.getCreatedAt(),
                list.getUpdatedAt()
        );
    }

    public static ReminderListResponse from(ReminderList list) {
        return from(list, 0);
    }
}
