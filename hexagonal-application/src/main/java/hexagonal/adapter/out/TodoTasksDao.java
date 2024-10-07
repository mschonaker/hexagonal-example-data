package hexagonal.adapter.out;

import java.util.List;

import hexagonal.domain.TodoTask;

public interface TodoTasksDao {

    void dsl();

    TodoTask insert(String body);

    void delete(Long id);

    void update(TodoTask task);

    TodoTask get(Long id);

    List<TodoTask> list(int offset, int limit);

}
