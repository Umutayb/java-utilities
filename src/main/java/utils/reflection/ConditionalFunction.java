package utils.reflection;

/**
 * A functional interface representing a conditional operation.
 *
 * <p>This interface defines a single abstract method, {@link #execute()}, which returns a boolean value.
 * It is used to represent conditions or predicates that evaluate to true or false.</p>
 *
 * @see java.util.function.Predicate
 */
@FunctionalInterface
public interface ConditionalFunction {
    boolean execute();
}