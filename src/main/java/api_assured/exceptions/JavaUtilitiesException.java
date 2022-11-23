package api_assured.exceptions;

public class JavaUtilitiesException extends RuntimeException {

    public JavaUtilitiesException(String errorMessage) {super(errorMessage);}

    public JavaUtilitiesException(RuntimeException errorMessage) {super(errorMessage);}
}
