package hexagonal.adapter.in.jetty;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;

import javax.sql.DataSource;

import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.h2.jdbcx.JdbcConnectionPool;

import com.fasterxml.jackson.databind.ObjectMapper;

import hexagonal.adapter.out.TodoTasksDao;
import hexagonal.domain.TodoTask;
import hexagonal.ports.exception.InvalidInputException;
import hexagonal.service.TodoTasksService;
import io.github.mschonaker.bundler.Bundler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class Main {

    public static void main(String[] args) throws Exception {
        var server = newServer(8080);

        server.start();
        try {

            System.console().readLine("Hit ENTER to stop");

        } catch (Exception ignore) {

        } finally {
            server.stop();
        }
    }

    public static Server newServer(int port) throws IOException, SQLException {

        DataSource ds = JdbcConnectionPool
                .create("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MYSQL", null,
                        null);

        var writer = new PrintWriter(System.out);
        ds.setLogWriter(writer);

        var dao = Bundler.inflate(TodoTasksDao.class);
        try (var tx = Bundler.writeTransaction(ds)) {
            dao.dsl();
            tx.success();
        }

        var service = new TodoTasksService(dao);

        var server = new Server();
        var httpConfig = new HttpConfiguration();
        httpConfig.setFormEncodedMethods("POST");

        var connector = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
        connector.setPort(port);
        server.addConnector(connector);

        var servletContextHandler = new ServletContextHandler();
        servletContextHandler.setContextPath("/");
        servletContextHandler.setMaxFormContentSize(128_000);

        /*
         * cURL to get all tasks:
         * 
         * curl http://localhost:8080/todo/\?offset\=0\&limit\=10
         * 
         * cURL to get a task:
         * 
         * curl http://localhost:8080/todo/1
         * 
         * cURL to create a task:
         * 
         * curl -X POST http://localhost:8080/todo/ -d '{"body":"Hello, world!"}'
         * 
         * cURL to update a task:
         * 
         * curl -X PUT http://localhost:8080/todo/1 -d '{"body":"Hello, buddy!"}'
         * 
         * cURL to delete a task:
         * 
         * curl -X DELETE http://localhost:8080/todo/1
         */
        servletContextHandler.addServlet(new HttpServlet() {

            @Override
            protected void doGet(HttpServletRequest request, HttpServletResponse response)
                    throws ServletException, IOException {

                // Split the path, discarding empty strings.
                var path = Optional.ofNullable(request.getPathInfo()).orElse("").split("/");
                path = Arrays.stream(path).filter(s -> !s.isEmpty()).toArray(String[]::new);

                // If path is empty, return all tasks.
                if (path.length == 0) {

                    Integer offset = 0;
                    Integer limit = 10;

                    try {
                        // Get the offset and limit from the query string.
                        offset = Optional.ofNullable(request.getParameter("offset")).map(Integer::parseInt).orElse(0);
                        limit = Optional.ofNullable(request.getParameter("limit")).map(Integer::parseInt).orElse(10);
                    } catch (NumberFormatException e) {
                        response.setStatus(400);
                        return;
                    }

                    try (var tx = Bundler.readTransaction(ds)) {

                        var tasks = service.list(offset, limit);

                        tx.success();

                        var mapper = new ObjectMapper();
                        response.addHeader("Content-Type", "application/json");
                        mapper.writeValue(response.getWriter(), tasks);

                        return;

                    } catch (InvalidInputException e) {
                        response.setStatus(400);
                        return;
                    }
                }

                // Otherwise, the first part is the id.
                var id = path[0];
                Long longId = null;
                try {
                    longId = Long.parseLong(id);
                } catch (NumberFormatException e) {
                    response.setStatus(400);
                    return;
                }

                try (var tx = Bundler.readTransaction(ds)) {

                    var task = service.get(longId);

                    tx.success();

                    if (task == null) {
                        response.setStatus(404);
                        return;
                    }

                    var mapper = new ObjectMapper();
                    response.addHeader("Content-Type", "application/json");
                    mapper.writeValue(response.getWriter(), task);

                } catch (NumberFormatException | InvalidInputException e) {
                    response.setStatus(400);
                }
            }

            @Override
            protected void doPost(HttpServletRequest request, HttpServletResponse response)
                    throws ServletException, IOException {

                // The JSON body is the task.
                // And also the response is the task.
                var mapper = new ObjectMapper();
                var task = mapper.readValue(request.getReader(), TodoTask.class);

                try (var tx = Bundler.writeTransaction(ds)) {
                    service.create(task);
                    tx.success();

                    response.addHeader("Content-Type", "application/json");
                    mapper.writeValue(response.getWriter(), task);

                } catch (InvalidInputException e) {
                    response.setStatus(400);
                }
            }

            @Override
            protected void doPut(HttpServletRequest request, HttpServletResponse response)
                    throws ServletException, IOException {

                // The JSON body is the task.
                // The response is empty. Only the HTTP status is set.
                var mapper = new ObjectMapper();
                var task = mapper.readValue(request.getReader(), TodoTask.class);

                try (var tx = Bundler.writeTransaction(ds)) {
                    service.update(task);

                    tx.success();

                    response.setStatus(204);

                } catch (InvalidInputException e) {
                    response.setStatus(400);
                }
            }

            @Override
            protected void doDelete(HttpServletRequest request, HttpServletResponse response)
                    throws ServletException, IOException {

                // Extract last part from the URL.
                var path = Optional.of(request.getPathInfo()).orElse("").split("/");
                path = Arrays.stream(path).filter(s -> !s.isEmpty()).toArray(String[]::new);

                if (path.length == 0) {
                    response.setStatus(400);
                    return;
                }

                var id = path[path.length - 1];

                try (var tx = Bundler.writeTransaction(ds)) {
                    service.delete(Long.parseLong(id));

                    tx.success();

                    response.setStatus(204);
                } catch (NumberFormatException | InvalidInputException e) {
                    response.setStatus(400);
                }
            }

        }, "/todo/*");

        var handlers = new Handler.Sequence();
        handlers.addHandler(servletContextHandler);
        handlers.addHandler(new DefaultHandler());
        server.setHandler(handlers);

        return server;
    }
}
