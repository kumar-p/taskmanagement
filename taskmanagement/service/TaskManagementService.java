package taskmanagement.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import taskmanagement.dto.*;
import taskmanagement.entity.Comment;
import taskmanagement.entity.Task;
import taskmanagement.entity.UserAccount;
import taskmanagement.enums.TaskStatus;
import taskmanagement.exception.ForbiddenOperationException;
import taskmanagement.exception.ResourceNotFoundException;
import taskmanagement.repository.CommentRepository;
import taskmanagement.repository.TaskRepository;
import taskmanagement.repository.UserAccountRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TaskManagementService {
    private static final String UNASSIGNED = "none";

    private final TaskRepository taskRepository;
    private final UserAccountRepository userAccountRepository;
    private final CommentRepository commentRepository;

    public TaskManagementService(TaskRepository taskRepository,
                                 UserAccountRepository userAccountRepository,
                                 CommentRepository commentRepository) {
        this.taskRepository = taskRepository;
        this.userAccountRepository = userAccountRepository;
        this.commentRepository = commentRepository;
    }

    private static TaskDto getTaskDto(Task task) {
        return new TaskDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getAuthor(),
                task.getAssignee() == null ? UNASSIGNED : task.getAssignee()
        );
    }

    private TaskDto getTaskDtoWithCommentCount(Task task) {
        long commentCount = commentRepository.countByTaskId(task.getId());
        return new TaskDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getAuthor(),
                task.getAssignee() == null ? UNASSIGNED : task.getAssignee(),
                commentCount
        );
    }

    private static CommentDto getCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getTaskId(),
                comment.getText(),
                comment.getAuthor()
        );
    }

    public TaskDto addTask(TaskRequest taskRequest, String author) {
        Task task = new Task(
                taskRequest.title(),
                taskRequest.description(),
                TaskStatus.CREATED,
                author
        );
        Task savedTask = taskRepository.save(task);
        return getTaskDto(savedTask);
    }

    /**
     * Returns tasks, newest first, optionally filtered by author and/or assignee.
     * A {@code null} filter is ignored. The assignee is matched case-insensitively.
     * Each task in the list includes total_comments.
     */
    @Transactional(readOnly = true)
    public List<TaskDto> getTasks(String author, String assignee) {
        return taskRepository.findByFilters(author, assignee).stream()
                .map(this::getTaskDtoWithCommentCount)
                .toList();
    }

    public Optional<TaskDto> getTaskById(long id) {
        return taskRepository.findById(id).map(TaskManagementService::getTaskDto);
    }

    /**
     * Assigns the task to a registered user, or removes the assignee when {@code "none"} is passed.
     * Does not change the task's status. Only the task's author may do this.
     *
     * @param currentUser email of the authenticated user performing the assignment
     * @throws ResourceNotFoundException   if the task or the target assignee does not exist
     * @throws ForbiddenOperationException if {@code currentUser} is not the task's author
     */
    @Transactional
    public TaskDto assignTask(long taskId, AssignRequest assignRequest, String currentUser) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found."));

        if (!task.getAuthor().equalsIgnoreCase(currentUser)) {
            throw new ForbiddenOperationException("Only the task author can assign this task.");
        }

        String requested = assignRequest.assignee().trim();
        if (UNASSIGNED.equalsIgnoreCase(requested)) {
            task.setAssignee(null);
        } else {
            // Use the stored (normalized) email of the assignee.
            String assignee = userAccountRepository.findByEmailIgnoreCase(requested)
                    .map(UserAccount::getEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("Assignee not found."));
            task.setAssignee(assignee);
        }
        return getTaskDto(task); // managed entity, flushed on commit
    }

    /**
     * Updates the status of a task. Only the task's author or assignee may do this.
     *
     * @param currentUser email of the authenticated user performing the update
     * @throws ResourceNotFoundException   if the task does not exist
     * @throws ForbiddenOperationException if {@code currentUser} is neither the author nor the assignee
     */
    @Transactional
    public TaskDto updateStatus(long taskId, StatusRequest statusRequest, String currentUser) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found."));

        boolean isAuthor = task.getAuthor().equalsIgnoreCase(currentUser);
        boolean isAssignee = task.getAssignee() != null && task.getAssignee().equalsIgnoreCase(currentUser);
        if (!isAuthor && !isAssignee) {
            throw new ForbiddenOperationException("Only the task author or assignee can change the status.");
        }

        task.setStatus(statusRequest.status());
        return getTaskDto(task); // managed entity, flushed on commit
    }

    /**
     * Adds a comment to a task. Any authenticated user can comment on any task.
     *
     * @throws ResourceNotFoundException if the task does not exist
     */
    @Transactional
    public void addComment(long taskId, CommentRequest commentRequest, String author) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task not found.");
        }

        Comment comment = new Comment(taskId, commentRequest.text(), author);
        commentRepository.save(comment);
    }

    /**
     * Retrieves all comments for a task, ordered newest first.
     *
     * @throws ResourceNotFoundException if the task does not exist
     */
    @Transactional(readOnly = true)
    public List<CommentDto> getComments(long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task not found.");
        }

        return commentRepository.findAllByTaskIdOrderByIdDesc(taskId).stream()
                .map(TaskManagementService::getCommentDto)
                .toList();
    }
}
