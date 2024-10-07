package hexagonal.service;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.List;
import java.util.Optional;

import hexagonal.adapter.out.TodoTasksDao;
import hexagonal.domain.TodoTask;
import hexagonal.ports.exception.InvalidInputException;
import hexagonal.usecase.CreateTodoTaskUseCase;
import hexagonal.usecase.DeleteTodoTaskUseCase;
import hexagonal.usecase.GetTodoTaskUseCase;
import hexagonal.usecase.ListTodoTasksUseCase;
import hexagonal.usecase.UpdateTodoTaskUseCase;

public class TodoTasksService implements CreateTodoTaskUseCase,
        DeleteTodoTaskUseCase, GetTodoTaskUseCase,
        ListTodoTasksUseCase, UpdateTodoTaskUseCase {

    private final int MAX_BODY_LENGTH = 200;
    private final int MAX_LIST_LENGTH = 100;

    private final TodoTasksDao dao;

    /**
     * Create a new TodoTasksService.
     */
    public TodoTasksService(TodoTasksDao dao) {
        this.dao = dao;
    }

    /**
     * Create a new TodoTask.
     *
     * The body of the TodoTask should be set, and should not be empty
     * and should not be more than MAX_BODY_LENGTH characters.
     *
     * The created, updated and id of the TodoTask are set by this
     * method to the return values of the dao.insert() method.
     * 
     * @throws InvalidInputException if the body is null or empty
     */
    @Override
    public void create(TodoTask task) {
        var body = task.getBody();
        if (body == null || body.length() > MAX_BODY_LENGTH || body.trim().isEmpty())
            throw new InvalidInputException();

        var inserted = dao.insert(body);
        task.setId(inserted.getId());

        task.setCreated(inserted.getCreated());
        task.setUpdated(inserted.getUpdated());
    }

    /**
     * Update a TodoTask.
     *
     * The id of the TodoTask should be set and should not be null.
     *
     * The body of the TodoTask should be set, and should not be empty
     * and should not be more than MAX_BODY_LENGTH characters.
     * 
     * The updated of the TodoTask is set by this method to the return
     * value of the dao.update() method.
     *
     * @throws InvalidInputException if the id is null or the body is null or empty
     */
    @Override
    public void update(TodoTask task) {

        var id = task.getId();
        if (id == null)
            throw new InvalidInputException();

        var body = task.getBody();
        if (body == null || body.length() > MAX_BODY_LENGTH || body.trim().isEmpty())
            throw new InvalidInputException();

        dao.update(task);
    }

    /**
     * Get a list of TodoTasks.
     * 
     * The offset and limit parameters are used to implement pagination.
     * 
     * The offset is the number of TodoTasks to skip from the beginning of
     * the list. If the offset is negative, it is treated as 0.
     * 
     * The limit is the number of TodoTasks to return. If the limit is negative,
     * it is treated as 0. If the limit is more than MAX_LIST_LENGTH, it is
     * treated as MAX_LIST_LENGTH.
     * 
     * The list returned is ordered by the id of the TodoTask in ascending
     * order.
     * 
     * @return a list of TodoTasks
     */
    @Override
    public List<TodoTask> list(Integer offset, Integer limit) {

        offset = Optional.ofNullable(offset).orElse(0);
        limit = Optional.ofNullable(limit).orElse(0);

        offset = max(0, offset);
        limit = max(0, limit);
        limit = min(MAX_LIST_LENGTH, limit);

        return dao.list(offset, limit);
    }

    /**
     * Get a TodoTask.
     *
     * The id of the TodoTask should be set and should not be null.
     *
     * @throws InvalidInputException if the id is null
     */
    @Override
    public TodoTask get(Long id) {
        if (id == null)
            throw new InvalidInputException();

        return dao.get(id);
    }

    /**
     * Delete a TodoTask.
     *
     * The id of the TodoTask should be set and should not be null.
     *
     * @throws InvalidInputException if the id is null
     */
    @Override
    public void delete(Long id) {
        if (id == null)
            throw new InvalidInputException();

        dao.delete(id);
    }
}