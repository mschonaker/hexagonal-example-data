package hexagonal.usecase;

import hexagonal.domain.TodoTask;

public interface GetTodoTaskUseCase {

    TodoTask get(Long id);

}
