package com.smehsn.compassinsurance.fragment;


public interface FormObjectProvider {
    void validateForm(OnValidationResultListener result);
    Object getFormModelObject();
}
