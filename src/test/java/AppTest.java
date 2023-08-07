import com.fasterxml.jackson.core.JsonProcessingException;
import models.Pet;
import org.junit.Test;
import static utils.MappingUtilities.Json.*;
import utils.Printer;

public class AppTest {

    static Printer printer = new Printer(AppTest.class);

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
