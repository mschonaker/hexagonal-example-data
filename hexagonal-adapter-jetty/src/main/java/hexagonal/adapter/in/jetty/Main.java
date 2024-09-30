package hexagonal.adapter.in.jetty;

import java.io.IOException;

import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;

import hexagonal.domain.Salute;
import hexagonal.domain.User;
import hexagonal.ports.exception.GreetException;
import hexagonal.ports.in.GreetRequest;
import hexagonal.ports.out.GreetResponse;
import hexagonal.service.GreetService;
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

    public static Server newServer(int port) throws IOException {

        var service = new GreetService();

        var server = new Server();
        var httpConfig = new HttpConfiguration();
        httpConfig.setFormEncodedMethods("POST");

        var connector = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
        connector.setPort(port);
        server.addConnector(connector);

        var servletContextHandler = new ServletContextHandler();
        servletContextHandler.setContextPath("/");
        servletContextHandler.setMaxFormContentSize(128_000);
        servletContextHandler.addServlet(new HttpServlet() {

            @Override
            protected void doGet(HttpServletRequest request, HttpServletResponse response)
                    throws ServletException, IOException {

                try {
                    service.greet(new GreetRequest() {

                        @Override
                        public User readUser() throws GreetException {
                            return new User(request.getParameter("name"));
                        }

                    }, new GreetResponse() {

                        @Override
                        public void writeSalute(Salute salute) throws GreetException {
                            response.setHeader("Content-Type", "text/plain");
                            try {
                                response.getWriter().print(salute.message());
                            } catch (IOException e) {
                                throw new GreetException(e);
                            }
                        }

                    });
                } catch (GreetException e) {
                    throw new ServletException(e);
                }
            }
        }, "/");

        var handlers = new Handler.Sequence();
        handlers.addHandler(servletContextHandler);
        handlers.addHandler(new DefaultHandler());
        server.setHandler(handlers);

        return server;
    }
}
