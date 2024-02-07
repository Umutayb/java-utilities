import collections.Pair;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonObject;
import context.ContextStore;
import org.junit.Assert;
import petstore.PetStore;
import petstore.PetStoreServices;
import petstore.models.Pet;
import org.junit.Test;
import utils.*;
import utils.reflection.ReflectionUtilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static utils.EmailUtilities.Inbox.EmailField.CONTENT;
import static utils.EmailUtilities.Inbox.EmailField.SUBJECT;
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
    public void filterEmailTest() {
        ContextStore.loadProperties("test.properties");
        EmailUtilities emailUtilities = new EmailUtilities(ContextStore.get("host"));
        emailUtilities.sendEmail(
                "Test filter two",
                "content",
                ContextStore.get("test-email"),
                ContextStore.get("sender-test-email"),
                ContextStore.get("test-email-master-password"),
                null
        );
        emailUtilities.sendEmail(
                "Test filter apple",
                "content",
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
        EmailUtilities.Inbox inbox = new EmailUtilities.Inbox(
                "pop.gmail.com",
                "995",
                ContextStore.get("test-email"),
                ContextStore.get("test-email-application-password"),
                "ssl"
        );
        // TODO: assertions and change return value of load method
        inbox.load(true, true, false, Pair.of(SUBJECT, "Test filter"),Pair.of(CONTENT, "content"));
    }
}
