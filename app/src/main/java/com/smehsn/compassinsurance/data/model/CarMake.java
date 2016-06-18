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

}
