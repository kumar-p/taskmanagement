package taskmanagement.dto;

import jakarta.validation.constraints.NotNull;
import taskmanagement.enums.TaskStatus;

public record StatusRequest(@NotNull TaskStatus status) { }
