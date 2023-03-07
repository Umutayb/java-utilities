package utils;

import java.util.Random;

public class BooleanUtilities {

    private final Printer log = new Printer(BooleanUtilities.class);

    /**
     * Generates a random boolean
     * @return returns randomly generated the boolean
     */
    public Boolean generateRandomBoolean(){
        return new Random().nextBoolean();
    }

}
