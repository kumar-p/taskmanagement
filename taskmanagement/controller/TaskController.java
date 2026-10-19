package taskmanagement.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import taskmanagement.dto.*;
import taskmanagement.service.TaskManagementService;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
class TaskController {
    private final TaskManagementService taskManagementService;

    TaskController(TaskManagementService taskManagementService) {
        this.taskManagementService = taskManagementService;
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> getTasks(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String assignee) {
        List<TaskDto> taskDtoList = taskManagementService.getTasks(author, assignee);
        return ResponseEntity.ok(taskDtoList);
    }

    @PostMapping
    public ResponseEntity<TaskDto> createTask(
            @RequestBody @Valid TaskRequest taskRequest,
            Authentication authentication) {
        TaskDto taskDto = taskManagementService.addTask(taskRequest, authentication.getName());
        return ResponseEntity.ok(taskDto);
    }

    @PutMapping("{taskId}/assign")
    public ResponseEntity<TaskDto> assignTask(
            @PathVariable long taskId,
            @Valid @RequestBody AssignRequest assignRequest,
            Authentication authentication) {
        TaskDto task = taskManagementService.assignTask(taskId, assignRequest, authentication.getName());
        return ResponseEntity.ok(task);
    }

    @PutMapping("{taskId}/status")
    public ResponseEntity<TaskDto> updateStatus(
            @PathVariable long taskId,
            @Valid @RequestBody StatusRequest statusRequest,
            Authentication authentication) {
        TaskDto task = taskManagementService.updateStatus(taskId, statusRequest, authentication.getName());
        return ResponseEntity.ok(task);
    }

    @PostMapping("{taskId}/comments")
    public ResponseEntity<Void> addComment(
            @PathVariable long taskId,
            @Valid @RequestBody CommentRequest commentRequest,
            Authentication authentication) {
        taskManagementService.addComment(taskId, commentRequest, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("{taskId}/comments")
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable long taskId) {
        List<CommentDto> comments = taskManagementService.getComments(taskId);
        return ResponseEntity.ok(comments);
    }
}
