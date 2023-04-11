package api_assured.exceptions;

/**
 * This class represents an exception that is thrown when a call fails.
 */
public class FailedCallException extends RuntimeException {

    /**
     * Constructs a FailedCallException with the specified runtime exception.
     * @param errorMessage The runtime exception to be associated with this exception.
     */
    public FailedCallException(String errorMessage) {super(errorMessage);}

    public FailedCallException(RuntimeException errorMessage) {super(errorMessage);}
}
