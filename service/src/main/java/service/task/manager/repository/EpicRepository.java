package service.task.manager.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import service.task.manager.model.Epic;

@Repository
public interface EpicRepository extends CrudRepository<Epic, Long> {
}