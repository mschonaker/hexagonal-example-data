package hexagonal.service.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import hexagonal.domain.Salute;
import hexagonal.domain.User;
import hexagonal.ports.exception.GreetException;
import hexagonal.ports.in.GreetRequest;
import hexagonal.ports.out.GreetResponse;
import hexagonal.service.GreetService;

public class GreetServiceTest {

    @Test
    public void greetTest() throws Exception {
        var service = new GreetService();
        service.greet(new GreetRequest() {

            @Override
            public User readUser() throws GreetException {
                return new User("Test");
            }

        }, new GreetResponse() {

            @Override
            public void writeSalute(Salute salute) throws GreetException {
                assertEquals(salute.message(), "Howdy, Test!");
            }

        });
    }

    @Test
    public void greetExceptionTest() throws Exception {
        var service = new GreetService();

        assertThrows(GreetException.class, () -> service.greet(new GreetRequest() {

            @Override
            public User readUser() throws GreetException {
                return new User("Test");
            }

        }, new GreetResponse() {

            @Override
            public void writeSalute(Salute salute) throws GreetException {
                throw new GreetException();
            }

        }));
    }
}
