package records;

/**
 * A record that represents a pair of two values in different types.
 *
 * @param <A> the type of the first value in the pair
 * @param <B> the type of the second value in the pair
 */
public record Pair<A, B>(A alpha, B beta){}
