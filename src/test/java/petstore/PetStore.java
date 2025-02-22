package petstore;

import api_assured.ApiUtilities;
import api_assured.ServiceGenerator;
import okhttp3.ResponseBody;
import petstore.models.Pet;
import retrofit2.Call;
import retrofit2.Response;
import utils.StringUtilities;
import java.util.List;

import static utils.StringUtilities.*;

public class PetStore extends ApiUtilities {

    PetStoreServices petStoreServices = new ServiceGenerator()
            .setRequestLogging(true)
            .printHeaders(true)
            .generate(PetStoreServices.class);

    static PetStoreServices.GetPdf getPdf = new ServiceGenerator()
            .setRequestLogging(true)
            .printHeaders(true)
            .generate(PetStoreServices.GetPdf.class);

    public List<Pet> getPetsByStatus(PetStoreServices.PetStatus status){
        log.info("Getting pets by status: " + highlighted(StringUtilities.Color.BLUE, status.name()));
        Call<List<Pet>> petByStatusCall = petStoreServices.getPet(status);
        return getResponseForCode(30, 200, petByStatusCall, true).body();
    }

    public Pet postPet(Pet pet){
        log.info("Post pet");
        Call<Pet> postPetCall = petStoreServices.postPet(pet);
        return monitorFieldValueFromResponse(30, "available", postPetCall, "status", true).body();
    }

    public Pet getPetById(Long petId){
        log.info("Getting pet by petId: " + highlighted(StringUtilities.Color.BLUE, String.valueOf(petId)));
        Call<Pet> petByIdCall = petStoreServices.getPetById(petId);
        return getResponseForCode(30, 200, petByIdCall, true).body();
    }

    public class GetPdf {
        public static Response<ResponseBody> getPdf() {
            log.info("Getting a subscription invoice");
            Call<ResponseBody> getPdfResponse = getPdf.getPdf();
            return getResponse(getPdfResponse, true, true);
        }
    }
}
