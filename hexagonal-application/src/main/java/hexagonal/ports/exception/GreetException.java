package hexagonal.ports.exception;

public class GreetException extends Exception {

    public GreetException() {
        super();
    }

    public GreetException(Exception e) {
        super(e);
    }

}
