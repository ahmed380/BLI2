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

}