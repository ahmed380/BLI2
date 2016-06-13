package com.smehsn.compassinsurance.parser;

public class FormValidationException extends Throwable {
    private String errorMessage;
    public FormValidationException(String firstErrorMessage) {
        this.errorMessage = firstErrorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
