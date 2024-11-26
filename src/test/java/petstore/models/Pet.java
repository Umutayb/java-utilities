package petstore.models;

import java.util.List;

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

    public Pet() {
    }

    public Pet(Long id, DataModel category, String name, List<String> photoUrls, List<DataModel> tags, String status) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.photoUrls = photoUrls;
        this.tags = tags;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DataModel getCategory() {
        return category;
    }

    public void setCategory(DataModel category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
    }

    public List<DataModel> getTags() {
        return tags;
    }

    public void setTags(List<DataModel> tags) {
        this.tags = tags;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class DataModel {
        Long id;
        String name;

        public DataModel(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public DataModel() {
        }
    }
}

