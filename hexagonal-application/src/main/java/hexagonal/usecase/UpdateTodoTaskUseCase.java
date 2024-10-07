package hexagonal.usecase;

import hexagonal.domain.TodoTask;

public interface UpdateTodoTaskUseCase {

    void update(TodoTask task);

}
