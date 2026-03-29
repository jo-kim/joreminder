package jo.ai.joreminder.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reminder_list")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReminderList extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String color = "BLUE";

    @Column(nullable = false)
    private boolean isDefault = false;

    public ReminderList(String name, String color) {
        this.name = name;
        this.color = color != null ? color : "BLUE";
        initTimestamps();
    }

    public static ReminderList createDefault(String name) {
        var list = new ReminderList(name, "BLUE");
        list.isDefault = true;
        return list;
    }

    public void update(String name, String color) {
        this.name = name;
        this.color = color != null ? color : this.color;
        markUpdated();
    }
}
