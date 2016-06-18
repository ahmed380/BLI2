package com.smehsn.compassinsurance.data.service;

import com.smehsn.compassinsurance.data.model.CarMake;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author Sam
 */
public interface CarService {

    @GET("vehicle/v2/makes")
    Call<List<CarMake>> getCarMakeData(@Query("api_key") String apiKey);

}
