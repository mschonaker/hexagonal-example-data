package hexagonal.service.test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.sql.Timestamp;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import hexagonal.adapter.out.TodoTasksDao;
import hexagonal.domain.TodoTask;
import hexagonal.ports.exception.InvalidInputException;
import hexagonal.service.TodoTasksService;

public class TodoTasksServiceTest {

    @Test
    public void testCreate() {

        var dao = mock(TodoTasksDao.class);

        var now = new Timestamp(System.currentTimeMillis());
        var inserted = new TodoTask();
        inserted.setId(1L);
        inserted.setBody("body");
        inserted.setUpdated(now);
        inserted.setCreated(now);
        doReturn(inserted).when(dao).insert("body");
        verifyNoMoreInteractions(dao);

        var service = new TodoTasksService(dao);

        var task = new TodoTask();
        task.setBody("body");

        service.create(task);

        assert task.getId() != null;
    }

    @Test
    public void testCreateNullBody() {

        var dao = mock(TodoTasksDao.class);
        verifyNoMoreInteractions(dao);

        var service = new TodoTasksService(dao);

        var task = new TodoTask();
        task.setBody(null);
        assertThrows(InvalidInputException.class, () -> service.create(task));
    }

    @Test
    public void testUpdate() {

        var dao = mock(TodoTasksDao.class);

        var now = new Timestamp(System.currentTimeMillis());
        var updated = new TodoTask(1L, "body", now, now);
        doNothing().when(dao).update(updated);
        verifyNoMoreInteractions(dao);

        var service = new TodoTasksService(dao);

        var task = new TodoTask();
        task.setId(1L);
        task.setBody("body");
        task.setUpdated(now);
        task.setCreated(now);

        service.update(task);

        assert task.getId() != null;
    }

    @Test
    public void testUpdateNullId() {

        var dao = mock(TodoTasksDao.class);
        verifyNoMoreInteractions(dao);

        var service = new TodoTasksService(dao);

        var task = new TodoTask();
        task.setId(null);
        assertThrows(InvalidInputException.class, () -> service.update(task));
    }

    @Test
    public void testUpdateNullBody() {

        var dao = mock(TodoTasksDao.class);
        verifyNoMoreInteractions(dao);

        var service = new TodoTasksService(dao);

        var task = new TodoTask();
        task.setId(1L);
        task.setBody(null);
        assertThrows(InvalidInputException.class, () -> service.update(task));
    }

    @Test
    public void testDelete() {

        var dao = mock(TodoTasksDao.class);
        doNothing().when(dao).delete(1L);
        verifyNoMoreInteractions(dao);

        var service = new TodoTasksService(dao);

        service.delete(1L);
    }

    @Test
    public void testDeleteNullId() {

        var dao = mock(TodoTasksDao.class);
        verifyNoMoreInteractions(dao);

        var service = new TodoTasksService(dao);

        assertThrows(InvalidInputException.class, () -> service.delete(null));
    }

    @Test
    public void testGet() {

        var dao = mock(TodoTasksDao.class);

        var now = new Timestamp(System.currentTimeMillis());
        var task = new TodoTask();
        task.setId(1L);
        task.setBody("body");
        task.setUpdated(now);
        task.setCreated(now);
        doReturn(task).when(dao).get(1L);
        verifyNoMoreInteractions(dao);

        var service = new TodoTasksService(dao);

        var result = service.get(1L);

        assert result.getId() != null;
    }

    @Test
    public void testGetNullId() {

        var dao = mock(TodoTasksDao.class);
        verifyNoMoreInteractions(dao);

        var service = new TodoTasksService(dao);

        assertThrows(InvalidInputException.class, () -> service.get(null));
    }

    @Test
    public void testList() {

        var dao = mock(TodoTasksDao.class);

        var now = new Timestamp(System.currentTimeMillis());
        var task = new TodoTask();
        task.setId(1L);
        task.setBody("body");
        task.setUpdated(now);
        task.setCreated(now);
        doReturn(Arrays.asList(task)).when(dao).list(0, 100);
        verifyNoMoreInteractions(dao);

        var service = new TodoTasksService(dao);

        var result = service.list(0, 100);

        assert result.size() == 1;
    }

    @Test
    public void testListNegativeOffset() {

        var dao = mock(TodoTasksDao.class);
        verifyNoMoreInteractions(dao);

        var service = new TodoTasksService(dao);

        var result = service.list(-1, 100);
        assert result.size() == 0;
    }

    @Test
    public void testListNegativeLimit() {

        var dao = mock(TodoTasksDao.class);
        verifyNoMoreInteractions(dao);

        var service = new TodoTasksService(dao);

        var result = service.list(0, -1);
        assert result.size() == 0;
    }

    @Test
    public void testListTooLargeLimit() {

        var dao = mock(TodoTasksDao.class);
        verifyNoMoreInteractions(dao);

        var service = new TodoTasksService(dao);

        var result = service.list(0, 101);
        assert result.size() == 0;
    }
}
