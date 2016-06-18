package com.smehsn.compassinsurance.data.client;

import com.smehsn.compassinsurance.data.service.CarService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
/**
 * @author Sam
 */
public class CarApiClientProvider {
    private static final String URL = "https://api.edmunds.com/api/";
    private static CarService service;
    static {
        service = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory( GsonConverterFactory.create())
                .build()
                .create(CarService.class);
    }

    public static CarService getService(){
        return service;
    }
}
