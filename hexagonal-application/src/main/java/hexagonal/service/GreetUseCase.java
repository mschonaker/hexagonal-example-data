package hexagonal.service;

import hexagonal.ports.exception.GreetException;
import hexagonal.ports.in.GreetRequest;
import hexagonal.ports.out.GreetResponse;

public interface GreetUseCase {

    void greet(GreetRequest request, GreetResponse response) throws GreetException;

}