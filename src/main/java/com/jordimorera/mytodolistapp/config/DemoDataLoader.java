package com.jordimorera.mytodolistapp.config;

import com.jordimorera.mytodolistapp.persistence.entity.Task;
import com.jordimorera.mytodolistapp.persistence.repository.TaskRepository;
import com.jordimorera.mytodolistapp.service.TaskService;
import com.jordimorera.mytodolistapp.service.dto.TaskInDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Carga unas tareas de ejemplo al arrancar si la base de datos esta vacia.
 * H2 es en memoria, asi que en la demo publica cada reinicio vuelve a este estado inicial.
 * Las fechas son relativas a "ahora" para que la demo siempre tenga sentido.
 */
@Component
public class DemoDataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataLoader.class);

    private final TaskRepository repository;
    private final TaskService taskService;

    public DemoDataLoader(TaskRepository repository, TaskService taskService) {
        this.repository = repository;
        this.taskService = taskService;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        create("Review pull request", "Check the new REST endpoints and their tests", now.plusHours(4));
        create("Upgrade to Spring Boot 4", "Migrate javax -> jakarta and fix deprecations", now.plusDays(3));
        create("Write API documentation", "Document every endpoint in the README", now.plusDays(7));
        create("Prepare demo for the team", "Show the Thymeleaf UI and the REST API side by side", now.plusDays(1));
        Task done = create("Set up CI pipeline", "Build and test on every push", now.minusDays(1));
        taskService.updateTaskAsFinished(done.getId());
        log.info("Demo data loaded: {} tasks", repository.count());
    }

    private Task create(String title, String description, LocalDateTime eta) {
        TaskInDTO dto = new TaskInDTO();
        dto.setTitle(title);
        dto.setDescription(description);
        dto.setEta(eta);
        return taskService.createTask(dto);
    }
}
