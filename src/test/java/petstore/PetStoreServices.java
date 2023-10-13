package petstore;

import petstore.models.Pet;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

public interface PetStoreServices {

    enum PetStatus {
        available,
        pending,
        sold
    }

    String BASE_URL = "https://petstore.swagger.io/v2/";

    @GET("pet/findByStatus")
    Call<List<Pet>> getPet(@Query("status") PetStatus status);
}
