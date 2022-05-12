package utils;

import java.util.Random;

public class BooleanUtilities {

    private final Printer log = new Printer(BooleanUtilities.class);

    public Boolean generateRandomBoolean(){
        return new Random().nextBoolean();
    }

}
