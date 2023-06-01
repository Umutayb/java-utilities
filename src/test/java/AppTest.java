import org.junit.Test;
import utils.Printer;

public class AppTest {

    static Printer printer = new Printer(AppTest.class);

    @Test
    public void dataGeneratorPetTest() {
        printer.info("Test!");
    }

}
