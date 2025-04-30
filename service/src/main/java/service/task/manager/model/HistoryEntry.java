package service.task.manager.model;

import lombok.*;
import service.task.manager.model.enums.TaskType;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class HistoryEntry {
    private TaskType type;
    private Long id;
}
