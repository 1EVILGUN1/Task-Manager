package service.task.manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.task.manager.model.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
}