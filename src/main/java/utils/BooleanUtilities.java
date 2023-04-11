package utils;

import java.util.Random;

/**
 * Methods for working with boolean values.
 */
public class BooleanUtilities {

    private final Printer log = new Printer(BooleanUtilities.class);

    /**
     * Generates and returns a random boolean value.
     *
     * @return The randomly generated boolean value.
     */
    public Boolean generateRandomBoolean(){
        return new Random().nextBoolean();
    }

}
