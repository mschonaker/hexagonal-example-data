package hexagonal.usecase;

import hexagonal.domain.TodoTask;

public interface CreateTodoTaskUseCase {

    void create(TodoTask task);

}
