import com.fasterxml.jackson.core.JsonProcessingException;
import models.Pet;
import org.junit.Test;
import static utils.MappingUtilities.Json.*;

import utils.ArrayUtilities;
import utils.Printer;

import java.util.Collections;
import java.util.List;

public class AppTest {

    static Printer printer = new Printer(AppTest.class);

    @Test
    public void getRandomItemTest() {
        List<Integer> numList = List.of(1,2,3,4,5,6,7,8,9);
        System.out.println(ArrayUtilities.getRandomItemFrom(numList));
    }

    @Test
    public void dataGeneratorPetTest() {
        printer.info("Test!");
    }

    @Test
    public void stringToObjectTest() throws JsonProcessingException {
        Pet pet = fromJsonString("{\"id\" : null, \"category\" : {\"id\" : null, \"name\" : \"Cats\"},\"name\" : \"Whiskers\", \"photoUrls\" : [ \"https://example.com/cat.jpg\" ],\"tags\" : [ {\"id\" : 123456789, \"name\" : \"Furry\"}, {\"id\" : 987654321, \"name\" : \"Playful\"} ],\"status\" : \"Available\"}", Pet.class);
        System.out.println(getJsonStringFor(pet));
    }

}
