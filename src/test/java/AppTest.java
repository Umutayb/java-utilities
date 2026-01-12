import collections.Pair;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.JsonObject;
import context.ContextStore;
import enums.ZoneIds;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import utils.*;
import utils.arrays.ArrayUtilities;
import utils.email.EmailUtilities;
import utils.mapping.MappingUtilities;
import utils.reflection.ReflectionUtilities;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static utils.arrays.ArrayUtilities.*;
import static utils.email.EmailUtilities.Inbox.EmailField.CONTENT;
import static utils.email.EmailUtilities.Inbox.EmailField.SUBJECT;
import static utils.StringUtilities.contextCheck;

public class AppTest {

    static Printer printer = new Printer(AppTest.class);

    @Before
    public void before(){
        ContextStore.loadProperties("test.properties", "secret.properties");
    }

    @Test
    public void getRandomItemTest() {
        List<Integer> numList = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9);
        int randomNumber = ArrayUtilities.getRandomItemFrom(numList);
        Assert.assertTrue(String.format("Random number %d is not part of the list!", randomNumber), numList.contains(randomNumber));
        printer.success("getRandomItemTest successful!");
    }

    @Test
    public void dataGeneratorPetTest() {
        printer.info("Test!");
    }

    @Test
    public void localisationCapabilityTest() {
        JsonObject localisationJson = FileUtilities.Json.parseJsonFile("src/test/resources/localisation.json");
        ContextStore.put("localisation-json", localisationJson);
        ContextStore.put("localised-elements", true);

        assert localisationJson != null;
        for (String key : localisationJson.keySet()) {
            Assert.assertEquals(
                    "Translation does not match the expected value!",
                    localisationJson.get(key).getAsString(),
                    contextCheck(key)
            );
        }
        printer.success("The localisationCapabilityTest() test passed!");
    }

    @Test
	public void getPDFFileTextTest() throws IOException {
        URL url = new URL("https://sandbox.mabl.com/downloads/mabl_dash.pdf");
        String fileDestinationPath = "src/test/resources/filePDF.pdf";
        String pdfText = FileUtilities.getPDFFileText(url, fileDestinationPath);

        assert pdfText != null;
        Assert.assertTrue(
                "PDF text does not contain the expected value!",
                pdfText.contains("Run Settings")
        );
        printer.success("The getPDFFileTextTest() test passed!" + pdfText);
    }

    @Test
    public void getSimpleDateStringFromTest() {
        String offsetDateTimeString = "2024-01-25T14:00:00+01:00";
        String dateFormat = "yyyy-MM-dd";
        String simpleDateFormatString = DateUtilities.getSimpleDateStringFrom(offsetDateTimeString, dateFormat);
        Assert.assertEquals(
                "Date string does not match the expected value!",
                "2024-01-25",
                simpleDateFormatString
        );
        printer.success("The getSimpleDateFormatStringFromTest() test passed!");
    }

    @Test
    public void getCurrentDateTest() {
        String simpleDateFormatString = DateUtilities.getCurrentDate(ZoneIds.EUROPE_PARIS);
        Assert.assertTrue(
                "Date string does not match!",
                Pattern.matches("(20)\\d{2}-(0[1-9]|1[1,2])-(0[1-9]|[12][0-9]|3[01])", simpleDateFormatString)
        );
        printer.success("The getSimpleDateFormatStringFromTest() test passed!");
    }

    @Test
    public void cleanEmailTest() {
        EmailUtilities.Inbox inbox = new EmailUtilities.Inbox("pop.gmail.com",
                "995",
                ContextStore.get("test-email"),
                ContextStore.get("test-email-application-password"),
                "ssl");

        String emailTestContent = "username:xyz";
        String emailSubject = "Test subject of email for deletion";
        EmailUtilities emailUtilities = new EmailUtilities(ContextStore.get("host"));
        emailUtilities.sendEmail(
                emailSubject,
                emailTestContent,
                ContextStore.get("test-email"),
                ContextStore.get("sender-test-email"),
                ContextStore.get("test-email-master-password"),
                null);

        inbox.load(30, 1, false, false, false,
                List.of(Pair.of(SUBJECT, emailSubject)));
        Assert.assertEquals("Unexpected number of emails found!", 1, inbox.getMessages().size());

        new EmailUtilities.Inbox("imap.gmail.com",
                "993",
                ContextStore.get("test-email"),
                ContextStore.get("test-email-application-password"),
                "ssl").clearInbox();

        EmailUtilities.Inbox newInbox = new EmailUtilities.Inbox("pop.gmail.com",
                "995",
                ContextStore.get("test-email"),
                ContextStore.get("test-email-application-password"),
                "ssl");
        newInbox.load(SUBJECT, emailSubject, false, true, true);

        Assert.assertEquals("Unexpected number of emails found!", 0, newInbox.getMessages().size());
        printer.success("cleanEmailTest() is successful!");
    }

	@Test
    public void filterEmailTest() {
        EmailUtilities.Inbox inbox = new EmailUtilities.Inbox(
                "pop.gmail.com",
                "995",
                ContextStore.get("test-email"),
                ContextStore.get("test-email-application-password"),
                "ssl"
        );

        new EmailUtilities.Inbox("imap.gmail.com",
                "993",
                ContextStore.get("test-email"),
                ContextStore.get("test-email-application-password"),
                "ssl").clearInbox();

        String emailTestContent = "username:xyz";
        EmailUtilities emailUtilities = new EmailUtilities(ContextStore.get("host"));
        emailUtilities.sendEmail(
                "Test filter banana",
                emailTestContent,
                ContextStore.get("test-email"),
                ContextStore.get("sender-test-email"),
                ContextStore.get("test-email-master-password"),
                null
        );
        emailUtilities.sendEmail(
                "Test filter apple",
                emailTestContent,
                ContextStore.get("test-email"),
                ContextStore.get("sender-test-email"),
                ContextStore.get("test-email-master-password"),
                null
        );
        emailUtilities.sendEmail(
                "Test filter orange",
                "test",
                ContextStore.get("test-email"),
                ContextStore.get("sender-test-email"),
                ContextStore.get("test-email-master-password"),
                null
        );

        inbox.load(
                30,
                2,
                true,
                true,
                false,
                List.of(Pair.of(SUBJECT, "Test filter"), Pair.of(CONTENT, emailTestContent))
        );

        Assert.assertEquals("Unexpected number of emails found!", 2, inbox.getMessages().size());
        Assert.assertTrue("Unexpected content!", inbox.getMessageBy(SUBJECT, "Test filter banana").getMessageContent().contains(emailTestContent));
        Assert.assertTrue("Unexpected content!", inbox.getMessageBy(SUBJECT, "Test filter apple").getMessageContent().contains(emailTestContent));
        printer.success("Sending and receiving emails tests are successful!");
    }

    @Test
    public void lastItemOfTest() {
        List<Integer> integers = List.of(1, 2, 3, 4, 5);
        Assert.assertTrue("Integer was not the last member!", isLastMemberOf(integers, 5));
        printer.success("The lastItemOfTest() test pass!");
    }

    @Test
    public void partitionTest() {
        List<Integer> integers = List.of(1, 2, 3, 4, 5);
        List<List<Integer>> partitionLists = List.of(
                List.of(1,2),
                List.of(3,4),
                List.of(5)
        );
        for (int i = 0; i < getPartitionCount(integers.size(), 2); i++) {
            List<Integer> partition = getListPartition(integers, 2, i);
            Assert.assertEquals(
                    "getListPartition() returned an unexpected partition!",
                    partition,
                    partitionLists.get(i)
            );
        }
        printer.success("The partitionTest() test pass!");
    }

    @Test
    public void partitionCountTest() {
        List<Integer> integers = List.of(1, 2, 3, 4, 5);
        Assert.assertEquals(
                "The getPartitionCount() method returns an incorrect value!",
                3,
                getPartitionCount(integers.size(), 2)
        );
        printer.success("The partitionCountTest() test pass!");
    }

    @Test
    public void dateFormatTest() {
       String date = "2025-6-20";
       String expectedDate = "2025-06-20";

       Assert.assertEquals(
               "Fixed date format did not match the expected one!",
               expectedDate,
               DateUtilities.reformatDateString(date, "yyyy-MM-dd")
       );
        printer.success("The dateFormatTest() test pass!");
    }

    @Test
    public void testSubtractionWithContextAndLiteral() {
        ContextStore.put("val", "20");
        String input = "MATH -> SUB ( CONTEXT-val , 5 )";
        String result = contextCheck(input);
        Assert.assertEquals("15", result);
        printer.success("The testSubtractionWithContextAndLiteral() test pass!");
    }

    @Test
    public void testAdditionWithContextOperands() {
        ContextStore.put("a", "10");
        ContextStore.put("b", "5");
        String input = "MATH->ADD(CONTEXT-a, CONTEXT-b)";
        String result = contextCheck(input);
        Assert.assertEquals("15", result);
        printer.success("The testAdditionWithContextOperands() test pass!");
    }

    @Test
    public void testInvalidOperation() {
        ContextStore.put("a", "3");
        String input = "MATH->MUL(2,CONTEXT-a)";
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            contextCheck(input);
        });
        Assert.assertTrue(exception.getMessage().contains("Unsupported"));
        printer.success("The testInvalidOperation() test pass!");
    }

    @Test
    public void testNonNumericContextValue() {
        ContextStore.put("bad", "abc");

        String input = "MATH->ADD(CONTEXT-bad, 2)";
        assertThrows(NumberFormatException.class, () -> {
            contextCheck(input);
        });
        printer.success("The testNonNumericContextValue() test pass!");
    }

    @Test
    public void testBadNumberOfArguments() {
        ContextStore.put("abc", "10");
        String input = "MATH->ADD(CONTEXT-abc,2,3)";
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            contextCheck(input);
        });
        Assert.assertTrue(exception.getMessage().contains("Exactly two arguments required"));
        printer.success("The testBadNumberOfArguments() test pass!");
    }
}
