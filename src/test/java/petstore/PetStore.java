package petstore;

import api_assured.ApiUtilities;
import api_assured.ServiceGenerator;
import context.ContextStore;
import petstore.models.Pet;
import retrofit2.Call;
import retrofit2.Response;
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
        return getResponseForCode(30, 200, petByStatusCall).body();
    }
}
