package yps.systems.ai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import yps.systems.ai.model.Task;
import yps.systems.ai.repository.ITaskRepository;

import java.util.Optional;

@Service
public class TaskEventListenerService {

    private final ITaskRepository taskRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public TaskEventListenerService(ITaskRepository taskRepository, ObjectMapper objectMapper) {
        this.taskRepository = taskRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${env.kafka.topicEvent}")
    public void listen(@Payload String payload, @Header("eventType") String eventType, @Header("source") String source) {
        System.out.println("Processing " + eventType + " event from " + source);
        switch (eventType) {
            case "CREATE_TASK":
                try {
                    Task task = objectMapper.readValue(payload, Task.class);
                    taskRepository.save(task);
                } catch (JsonProcessingException e) {
                    System.err.println("Error parsing Objective JSON: " + e.getMessage());
                }
                break;
            case "UPDATE_TASK":
                try {
                    Task task = objectMapper.readValue(payload, Task.class);
                    Optional<Task> optionalTask = taskRepository.findById(task.getId());
                    optionalTask.ifPresent(existingPerson -> taskRepository.save(task));
                } catch (JsonProcessingException e) {
                    System.err.println("Error parsing Objective JSON: " + e.getMessage());
                }
                break;
            case "DELETE_TASK":
                Optional<Task> optionalTask = taskRepository.findById(payload.replaceAll("\"", ""));
                optionalTask.ifPresent(value -> taskRepository.deleteById(value.getId()));
                break;
            default:
                System.out.println("Unknown event type: " + eventType);
        }
    }

}
