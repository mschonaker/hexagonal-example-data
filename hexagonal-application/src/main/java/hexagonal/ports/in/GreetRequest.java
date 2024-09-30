package hexagonal.ports.in;

import hexagonal.domain.User;
import hexagonal.ports.exception.GreetException;

public interface GreetRequest {

    User readUser() throws GreetException;

}
