package com.smehsn.compassinsurance.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Sam
 */
public class CarMake {

    @SerializedName("id")
    private Integer apiId;

    @SerializedName("name")
    private String  name;

    @SerializedName("niceName")
    private String niceName;

    @SerializedName("models")
    private List<CarModel> models;

    public Integer getApiId() {
        return apiId;
    }

    public void setApiId(Integer apiId) {
        this.apiId = apiId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNiceName() {
        return niceName;
    }

    public void setNiceName(String niceName) {
        this.niceName = niceName;
    }

    public List<CarModel> getModels() {
        return models;
    }

    public void setModels(List<CarModel> models) {
        this.models = models;
    }
}
