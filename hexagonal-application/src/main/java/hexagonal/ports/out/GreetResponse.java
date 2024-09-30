package hexagonal.ports.out;

import hexagonal.domain.Salute;
import hexagonal.ports.exception.GreetException;

public interface GreetResponse {

    void writeSalute(Salute salute) throws GreetException;

}
