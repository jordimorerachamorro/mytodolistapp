package com.jordimorera.mytodolistapp.service;


import com.jordimorera.mytodolistapp.exceptions.ToDoExceptions;
import com.jordimorera.mytodolistapp.mapper.TaskInDTOToTask;
import com.jordimorera.mytodolistapp.persistence.entity.Task;
import com.jordimorera.mytodolistapp.persistence.entity.TaskStatus;
import com.jordimorera.mytodolistapp.persistence.repository.TaskRepository;
import com.jordimorera.mytodolistapp.service.dto.TaskInDTO;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository repository;
    private final TaskInDTOToTask mapper;

    public TaskService(TaskRepository repository, TaskInDTOToTask mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public Task createTask(TaskInDTO taskInDTO) {
        Task task = mapper.map(taskInDTO);
        return this.repository.save(task);
    }

    public Task updateTask(Long id, TaskInDTO taskInDTO) {
        Task task = this.repository.findById(id)
                .orElseThrow(() -> new ToDoExceptions("Task not found", HttpStatus.NOT_FOUND));
        task.setTitle(taskInDTO.getTitle());
        task.setDescription(taskInDTO.getDescription());
        task.setEta(taskInDTO.getEta());
        if (!task.isFinished()) {
            // Si se mueve la fecha limite, el estado debe reflejarlo (p.ej. LATE -> ON_TIME)
            task.setTaskStatus(TaskStatus.forEta(task.getEta(), LocalDateTime.now()));
        }
        return this.repository.save(task);
    }

    @Transactional
    public List<Task> findAll() {
        refreshOverdueTasks();
        return this.repository.findAll();
    }

    @Transactional
    public List<Task> findAllByTaskStatus(TaskStatus status) {
        refreshOverdueTasks();
        return this.repository.findAllByTaskStatus(status);
    }

    /**
     * El paso del tiempo convierte tareas ON_TIME en LATE. Se recalcula antes de cada lectura:
     * una sola UPDATE, siempre consistente y sin necesidad de un job programado.
     */
    private void refreshOverdueTasks() {
        this.repository.markOverdueTasksAsLate(LocalDateTime.now(), TaskStatus.LATE);
    }

    @Transactional
    public void updateTaskAsFinished(Long id) {
        Optional<Task> optionalTask = this.repository.findById(id);
        if (optionalTask.isEmpty()) {
            throw new ToDoExceptions("Task not found", HttpStatus.NOT_FOUND);
        }

        this.repository.markTaskAsFinished(id);
    }

    public void deleteById(Long id) {
        Optional<Task> optionalTask = this.repository.findById(id);
        if (optionalTask.isEmpty()) {
            throw new ToDoExceptions("Task not found", HttpStatus.NOT_FOUND);
        }

        this.repository.deleteById(id);
    }

}
