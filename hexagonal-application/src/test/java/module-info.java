module hexagonal.application.test {

    requires org.junit.jupiter.api;

    requires hexagonal.application;

    // Required by JUnit.
    exports hexagonal.service.test;

}
