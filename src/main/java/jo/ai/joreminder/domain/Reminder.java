package jo.ai.joreminder.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reminder")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reminder extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private boolean completed = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_id", nullable = false)
    private ReminderList list;

    public Reminder(String title, ReminderList list) {
        this.title = title;
        this.list = list;
        initTimestamps();
    }

    public void update(String title) {
        this.title = title;
        markUpdated();
    }

    public void toggleCompleted() {
        this.completed = !this.completed;
        markUpdated();
    }
}
