package org.skypro.Entity.EntityForMyBase;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;

import java.util.List;

public class DataFromMethodGet {

    @JsonProperty("data")
    private final List<ObjectFromMethodGet> objectsFromMethodGet;

    public DataFromMethodGet(List<ObjectFromMethodGet> objectsFromMethodGet) {
        this.objectsFromMethodGet = objectsFromMethodGet;
    }

    public List<ObjectFromMethodGet> getObjectsFromMethodGet() {
        return objectsFromMethodGet;
    }
}
