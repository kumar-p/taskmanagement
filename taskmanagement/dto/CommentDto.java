package taskmanagement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public record CommentDto(
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        long id,
        @JsonProperty("task_id")
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        long taskId,
        String text,
        String author
) {
}
