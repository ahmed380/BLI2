package com.smehsn.compassinsurance.parser.fragment;

import com.smehsn.compassinsurance.parser.FormValidationException;

import java.util.Map;


public interface FormProvider {
    Map<String, String> validateAndGetForm() throws FormValidationException;
    String getFormTitle();
}
