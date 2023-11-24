package petstore;

import api_assured.ApiUtilities;
import api_assured.ServiceGenerator;
import petstore.models.Pet;
import retrofit2.Call;
import utils.StringUtilities;
import java.util.List;

public class PetStore extends ApiUtilities {

    PetStoreServices petStoreServices = new ServiceGenerator()
            .setRequestLogging(true)
            .printHeaders(true)
            .generate(PetStoreServices.class);

    public List<Pet> getPetsByStatus(PetStoreServices.PetStatus status){
        log.info("Getting pets by status: " + strUtils.highlighted(StringUtilities.Color.BLUE, status.name()));
        Call<List<Pet>> petByStatusCall = petStoreServices.getPet(status);
        return getResponseForCode(30, 200, petByStatusCall, true).body();
    }

    public Pet postPet(Pet pet){
        log.info("Post pet");
        Call<Pet> postPetCall = petStoreServices.postPet(pet);
        return monitorFieldValueFromResponse(30, "available", postPetCall,
                "status", true).body();
    }

    public Pet getPetById(Long petId){
        log.info("Getting pet by petId: " + strUtils.highlighted(StringUtilities.Color.BLUE, String.valueOf(petId)));
        Call<Pet> petByIdCall = petStoreServices.getPetById(petId);
        return getResponseForCode(30, 200, petByIdCall, true).body();
    }
}
