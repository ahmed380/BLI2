package com.smehsn.compassinsurance.parser;


public interface FormValidator {
    boolean isValid(String arg);
    String errorMessage(String label);
}
