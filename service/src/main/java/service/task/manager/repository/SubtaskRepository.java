package service.task.manager.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import service.task.manager.model.Subtask;

@Repository
public interface SubtaskRepository extends CrudRepository<Subtask, Long> {
}