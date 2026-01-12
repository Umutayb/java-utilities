package exceptions;

/**
 * This class represents an exception that is thrown when an error occurs in JavaUtilities.
 */
public class JavaUtilitiesException extends RuntimeException {

    /**
     * Constructs a JavaUtilitiesException with the specified error message.
     * @param errorMessage The error message to be associated with this exception.
     */
    public JavaUtilitiesException(String errorMessage) {super(errorMessage);}

    public JavaUtilitiesException(RuntimeException errorMessage) {super(errorMessage);}
}
