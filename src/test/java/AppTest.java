import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonObject;
import context.ContextStore;
import enums.ZoneIds;
import org.junit.Assert;
import petstore.PetStore;
import petstore.PetStoreServices;
import petstore.models.Pet;
import org.junit.Test;
import utils.*;
import utils.reflection.ReflectionUtilities;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static utils.MappingUtilities.Json.*;
import static utils.StringUtilities.contextCheck;

public class AppTest {

    static Printer printer = new Printer(AppTest.class);

    @Test
    public void getRandomItemTest() {
        List<Integer> numList = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9);
        System.out.println(ArrayUtilities.getRandomItemFrom(numList));
    }

    @Test
    public void dataGeneratorPetTest() {
        printer.info("Test!");
    }

    @Test
    public void stringToObjectTest() throws JsonProcessingException {
        Pet pet = fromJsonString("{\"id\" : null, \"category\" : {\"id\" : null, \"name\" : \"Cats\"},\"name\" : \"Whiskers\", \"photoUrls\" : [ \"https://example.com/cat.jpg\" ],\"tags\" : [ {\"id\" : 123456789, \"name\" : \"Furry\"}, {\"id\" : 987654321, \"name\" : \"Playful\"} ],\"status\" : \"Available\"}", Pet.class);
        printer.success("The stringToObjectTest() test passed!");
    }

    @Test
    public void petStatusTest() {
        PetStore petStore = new PetStore();
        petStore.getPetsByStatus(PetStoreServices.PetStatus.pending);
        printer.success("The petStatusTest() test passed!");
    }

    @Test
    public void petPostTest() {
        PetStore petStore = new PetStore();
        Pet pet = new Pet();
        pet.setName("doggie");
        List<String> photoUrls = List.of("string");
        pet.setPhotoUrls(photoUrls);
        pet.setStatus("available");
        petStore.postPet(pet);
        printer.success("The petPostTest() test passed!");
    }

    @Test
    public void petCompareJsonTest() {
        PetStore petStore = new PetStore();
        List<String> photoUrls = List.of("string1", "string2");
        List<Pet.DataModel> tags = new ArrayList<>();
        Pet.DataModel dataModel = new Pet.DataModel(111222L, "dataModel1");
        tags.add(dataModel);
        Pet.DataModel category = new Pet.DataModel(3333L, "category");
        Pet pet = new Pet(category, "Puppy", photoUrls, tags, "available");
        Pet createdPet = petStore.postPet(pet);
        Pet actualPet = petStore.getPetById(createdPet.getId());
        createdPet.setId(actualPet.getId());
        ReflectionUtilities.objectsMatch(createdPet, actualPet);
        printer.success("The petCompareJsonTest() test passed!");
    }

    @Test
    public void compareJsonPetWithEmptyArrayNegativeTest() {
        PetStore petStore = new PetStore();
        List<String> photoUrls = List.of("string1", "string2");
        List<Pet.DataModel> tags = new ArrayList<>();
        Pet.DataModel dataModel = new Pet.DataModel(111222L, "dataModel1");
        tags.add(dataModel);
        Pet.DataModel category = new Pet.DataModel(3333L, "category");
        Pet pet = new Pet(category, "Puppy", photoUrls, tags, "available");
        Pet createdPet = petStore.postPet(pet);
        Pet actualPet = petStore.getPetById(createdPet.getId());
        createdPet.setId(actualPet.getId());
        createdPet.setPhotoUrls(new ArrayList<>());
        Assert.assertFalse("The compareJsonPetWithEmptyArrayNegativeTest() negative test fails!", ReflectionUtilities.objectsMatch(createdPet, actualPet));
        printer.success("The compareJsonPetWithEmptyArrayNegativeTest() negative test passes!");
    }

    @Test
    public void compareJsonPetWithNullFieldValueInObjectNegativeTest() {
        PetStore petStore = new PetStore();
        List<String> photoUrls = List.of("string1", "string2");
        List<Pet.DataModel> tags = new ArrayList<>();
        Pet.DataModel dataModel = new Pet.DataModel(111222L, "dataModel1");
        tags.add(dataModel);
        Pet.DataModel category = new Pet.DataModel(3333L, "category");
        Pet pet = new Pet(category, "Puppy", photoUrls, tags, "available");
        Pet createdPet = petStore.postPet(pet);
        Pet actualPet = petStore.getPetById(createdPet.getId());
        createdPet.setId(actualPet.getId());
        Pet.DataModel expectedCategory = new Pet.DataModel(3333L, null);
        createdPet.setCategory(expectedCategory);
        Assert.assertFalse("The compareJsonPetWithNullFieldValueInObjectNegativeTest() negative test fails!", ReflectionUtilities.objectsMatch(createdPet, actualPet));
        printer.success("The compareJsonPetWithNullFieldValueInObjectNegativeTest() negative test passes!");
    }

    @Test
    public void localisationCapabilityTest(){
        JsonObject localisationJson = FileUtilities.Json.parseJsonFile("src/test/resources/localisation.json");
        ContextStore.put("localisation-json", localisationJson);
        ContextStore.put("localised-elements", true);

        assert localisationJson != null;
        for (String key:localisationJson.keySet()) {
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
                simpleDateFormatString,
                "2024-01-25"
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
}
