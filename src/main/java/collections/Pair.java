package collections;

/**
 * A record that represents a pair of two values in different types.
 *
 * @param <A> the type of the first value in the pair
 * @param <B> the type of the second value in the pair
 */
public record Pair<A, B>(A alpha, B beta){

    /**
     * Creates a new Pair object with the provided elements.
     *
     * @param alpha the first element of the pair.
     * @param beta  the second element of the pair.
     * @param <A>   the type of the first element.
     * @param <B>   the type of the second element.
     * @return a new Pair object containing the provided elements.
     */
    public static <A, B> Pair<A, B> of(A alpha, B beta){
        return new Pair<>(alpha, beta);
    }
}
