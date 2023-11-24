import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Assert;
import petstore.PetStore;
import petstore.PetStoreServices;
import petstore.models.Pet;
import org.junit.Test;
import utils.ArrayUtilities;
import utils.Printer;
import utils.ReflectionUtilities;

import java.util.ArrayList;
import java.util.List;

import static utils.MappingUtilities.Json.*;

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
        System.out.println(getJsonStringFor(pet));
    }

    @Test
    public void petStatusTest() {
        PetStore petStore = new PetStore();
        petStore.getPetsByStatus(PetStoreServices.PetStatus.pending);
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
}
