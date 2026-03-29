package jo.ai.joreminder.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "reminder")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reminder extends BaseEntity {

    @Column(nullable = false)
    private String title;

    private String memo;

    @Column(nullable = false)
    private boolean completed = false;

    private LocalDateTime completedAt;

    private LocalDate dueDate;

    private LocalTime dueTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority = Priority.NONE;

    @Column(nullable = false)
    private int displayOrder = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_id", nullable = false)
    private ReminderList list;

    public Reminder(String title, ReminderList list) {
        this.title = title;
        this.list = list;
        initTimestamps();
    }

    public void update(String title, String memo, LocalDate dueDate, LocalTime dueTime, Priority priority) {
        this.title = title;
        this.memo = memo;
        this.dueDate = dueDate;
        this.dueTime = dueTime;
        this.priority = priority != null ? priority : Priority.NONE;
        markUpdated();
    }

    public void toggleCompleted() {
        this.completed = !this.completed;
        this.completedAt = this.completed ? LocalDateTime.now() : null;
        markUpdated();
    }

    public void updateDisplayOrder(int order) {
        this.displayOrder = order;
        markUpdated();
    }
}
