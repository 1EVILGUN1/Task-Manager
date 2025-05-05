package service.task.manager.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;
import service.task.manager.model.enums.Status;
import service.task.manager.model.enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@ToString(exclude = "subtasks")
@Setter
@Entity
@Table(name = "epic")
public class Epic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "epic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Subtask> subtasks = new ArrayList<>();

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    private Duration duration;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Setter(AccessLevel.NONE)
    @Enumerated(EnumType.STRING)
    private TaskType type = TaskType.EPIC;

    @PrePersist
    @PreUpdate
    public void calculateEndTime() {
        if (startTime != null && duration != null) {
            endTime = startTime.plus(duration);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Epic epic)) return false;
        return Objects.equals(id, epic.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}