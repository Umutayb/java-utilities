package api_assured.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FieldMonitoringInputPackage<SuccessModel> {
    String fieldName;
    String expectedValue;
    CallInputPackage<SuccessModel> callInputPackage;
}
