package records;

/**
 * A record that represents a bundle of two values of different types.
 *
 * @param <A> the type of the first value in the bundle
 * @param <B> the type of the second value in the bundle
 */
public record Pair<A, B>(A alpha, B beta){}
