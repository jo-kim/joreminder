package jo.ai.joreminder.dto;

public record ReorderRequest(
        Long id,
        int displayOrder
) {
}
