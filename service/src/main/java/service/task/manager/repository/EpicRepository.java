package service.task.manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.task.manager.model.Epic;

@Repository
public interface EpicRepository extends JpaRepository<Epic, Long> {
}