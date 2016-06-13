package com.smehsn.compassinsurance.context;

import android.app.Application;

import com.smehsn.compassinsurance.beans.DealerInfoProvider;

import java.util.HashMap;
import java.util.Map;

public class AppContext extends Application{


    private Map<String, Object> beans = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        initContext();
    }


    private void initContext() {
        beans.put(DealerInfoProvider.BEAN_KEY, new DealerInfoProvider(this));
    }

    public Object get(String key){
        if (!beans.containsKey(key))
            throw new IllegalArgumentException("Context doesn't contain key: " + key);
        return beans.get(key);
    }


}
