package collections;

/**
 * A record that represents a bundle of three values of different types.
 *
 * @param <A> the type of the first value in the bundle
 * @param <B> the type of the second value in the bundle
 * @param <T> the type of the third value in the bundle
 */
public record Bundle<A, B, T>(A alpha, B beta, T theta) {}
