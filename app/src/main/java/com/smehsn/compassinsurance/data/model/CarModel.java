package com.smehsn.compassinsurance.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Sam
 */
public class CarModel {

    @SerializedName("id")
    private Integer apiId;

    @SerializedName("name")
    private String name;

    @SerializedName("niceName")
    private String niceName;

    @SerializedName("years.year")
    private List<Integer> years;

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

    public List<Integer> getYears() {
        return years;
    }

    public void setYears(List<Integer> years) {
        this.years = years;
    }
}