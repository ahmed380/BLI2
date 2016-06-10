package com.smehsn.compassinsurance.fragment;


public interface FormObjectProvider {
    Object getFormModelObject() throws ValidationException;
    int getPagePosition();
}
