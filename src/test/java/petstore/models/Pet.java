package petstore.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Pet {
    Long id;
    DataModel category;
    String name;
    List<String> photoUrls;
    List<DataModel> tags;
    String status;

    public Pet(DataModel category, String name, List<String> photoUrls, List<DataModel> tags, String status) {
        this.category = category;
        this.name = name;
        this.photoUrls = photoUrls;
        this.tags = tags;
        this.status = status;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DataModel {
        Long id;
        String name;
    }
}

