package utils;

import java.util.Random;

public class BooleanUtilities {

    Printer log = new Printer(BooleanUtilities.class);

    public Boolean generateRandomBoolean(){
        return new Random().nextBoolean();
    }

}
