package com.jordimorera.mytodolistapp.persistence.repository;


import com.jordimorera.mytodolistapp.persistence.entity.Task;
import com.jordimorera.mytodolistapp.persistence.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByTaskStatus(TaskStatus status);

    @Modifying
    @Query(value = "UPDATE TASK SET FINISHED=true WHERE ID=:id", nativeQuery = true)
    void markTaskAsFinished(@Param("id") Long id);

    /**
     * Marca como LATE las tareas pendientes cuya ETA ya ha pasado.
     * clearAutomatically: las lecturas posteriores ven el estado actualizado, no el cacheado.
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Task t SET t.taskStatus = :late WHERE t.finished = false AND t.eta < :now AND t.taskStatus <> :late")
    int markOverdueTasksAsLate(@Param("now") LocalDateTime now, @Param("late") TaskStatus late);
}
