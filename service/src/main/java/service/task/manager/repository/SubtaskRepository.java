package service.task.manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.task.manager.model.Subtask;

@Repository
public interface SubtaskRepository extends JpaRepository<Subtask, Long> {
}