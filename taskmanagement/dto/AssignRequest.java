package taskmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Request body for assigning or unassigning a task.
 * Accepts either a valid email address or the literal {@code "none"} to remove the assignee.
 */
public record AssignRequest(
        @NotBlank
        @Pattern(
                regexp = "^(none|[\\w.+-]+@[\\w.-]+\\.[a-zA-Z]{2,})$",
                flags = Pattern.Flag.CASE_INSENSITIVE,
                message = "Assignee must be a valid email address or 'none'."
        )
        String assignee) {
}
