package hexagonal.adapter.out;

import java.util.List;

import hexagonal.domain.TodoTask;

public interface TodoTasksDao {

    void dsl();

    /**
     * @return an incomplete TodoTask, doesn't include the body.
     */
    TodoTask insert(String body);

    void delete(Long id);

    void update(TodoTask task);

    TodoTask get(Long id);

    List<TodoTask> list(int offset, int limit);

}
