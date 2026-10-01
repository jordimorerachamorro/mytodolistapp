package com.jordimorera.mytodolistapp.persistence.entity;

import java.time.LocalDateTime;

public enum TaskStatus {
    ON_TIME, LATE;

    /**
     * Estado de una tarea pendiente segun su fecha limite: LATE si la ETA ya ha pasado.
     */
    public static TaskStatus forEta(LocalDateTime eta, LocalDateTime now) {
        return eta != null && eta.isBefore(now) ? LATE : ON_TIME;
    }
}
