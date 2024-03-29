package petstore;

import petstore.models.Pet;
import retrofit2.Call;
import retrofit2.http.*;

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

    @GET("pet/{petId}")
    Call<Pet> getPetById(@Path("petId") Long petId);

    @POST("pet")
    Call<Pet> postPet(@Body Pet pet);
}
