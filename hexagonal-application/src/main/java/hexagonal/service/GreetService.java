package hexagonal.service;

import hexagonal.domain.Salute;
import hexagonal.ports.exception.GreetException;
import hexagonal.ports.in.GreetRequest;
import hexagonal.ports.out.GreetResponse;

public class GreetService implements GreetUseCase {

    @Override
    public void greet(GreetRequest request, GreetResponse response) throws GreetException {
        var user = request.readUser();
        var name = user.name();
        var sb = new StringBuilder()
                .append("Howdy, ")
                .append(name)
                .append("!");

        response.writeSalute(new Salute(sb.toString()));
    }
}