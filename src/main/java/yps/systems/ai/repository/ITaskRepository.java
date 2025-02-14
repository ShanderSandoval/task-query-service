package yps.systems.ai.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import yps.systems.ai.model.Task;

@Repository
public interface ITaskRepository extends MongoRepository<Task, String> {
}
