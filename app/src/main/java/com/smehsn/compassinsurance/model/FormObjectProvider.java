package com.smehsn.compassinsurance.model;


import com.smehsn.compassinsurance.fragment.ValidationException;

public interface FormObjectProvider {
    Object getFormModelObject() throws ValidationException;
    int getPagePosition();
}
