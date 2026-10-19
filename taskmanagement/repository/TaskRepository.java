package taskmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import taskmanagement.entity.Task;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Optional<Task> findById(long id);

    /**
     * Finds tasks, newest first. Each filter is optional: a {@code null} argument means "don't filter".
     * The assignee is matched case-insensitively.
     */
    @Query("""
            select t from Task t
            where (:author is null or t.author = :author)
              and (:assignee is null or lower(t.assignee) = lower(:assignee))
            order by t.id desc
            """)
    List<Task> findByFilters(@Param("author") String author, @Param("assignee") String assignee);
}
