package hexagonal.usecase;

import java.util.List;

import hexagonal.domain.TodoTask;

public interface ListTodoTasksUseCase {

    List<TodoTask> list(Integer offset, Integer limit);

}
