package taskmanagement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import taskmanagement.enums.TaskStatus;

public record TaskDto(
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        long id,
        String title,
        String description,
        TaskStatus status,
        String author,
        String assignee,
        @JsonProperty("total_comments")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        Long totalComments
) {
        public TaskDto(long id, String title, String description, TaskStatus status, String author, String assignee) {
                this(id, title, description, status, author, assignee, null);
        }
}
