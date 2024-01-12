package api_assured.models;

import lombok.*;
import retrofit2.Call;

@Data
@AllArgsConstructor
public class CallInputPackage<SuccessModel> {
    String serviceName;
    int expectedCode;
    Call<SuccessModel> call;
    boolean strict;
    boolean printBody;
    boolean printLastCallBody;
}
