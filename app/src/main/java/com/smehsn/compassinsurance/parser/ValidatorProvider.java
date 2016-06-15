package com.smehsn.compassinsurance.parser;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;


public class ValidatorProvider {
    protected static Map<String, FormValidator> validatorMap = new HashMap<>();
    static {
        validatorMap.put("required", new RequiredValidator());
        validatorMap.put("vin_validator", new VinNumberValidator());
        validatorMap.put("email_validator", new EmailValidator());
    }

    public static FormValidator get(String name){
        if (!validatorMap.containsKey(name))
            throw new IllegalArgumentException("No validator with name " + name);
        return validatorMap.get(name);
    }

    private static final class RequiredValidator implements FormValidator{
        @Override
        public boolean isValid(String arg) {
            return !TextUtils.isEmpty(arg) && arg.trim().length() != 0;
        }
        @Override
        public String errorMessage(String label) {
            return "Field " + label + " is required";
        }
    }

    private static final class VinNumberValidator implements FormValidator{

        @Override
        public boolean isValid(String arg) {
            return arg.replace(" ", "").length() == 17;
        }

        @Override
        public String errorMessage(String label) {
            return "Vin number should be 17 character long";
        }
    }

    private static final class EmailValidator implements FormValidator{

        @Override
        public boolean isValid(String arg) {
            return true;
        }

        @Override

        public String errorMessage(String label) {
            return null;
        }
    }
}
