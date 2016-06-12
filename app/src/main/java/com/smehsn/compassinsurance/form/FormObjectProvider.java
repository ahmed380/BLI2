package com.smehsn.compassinsurance.form;


public interface FormObjectProvider {
    Object getFormModelObject() throws ValidationException;
    int getPagePosition();
    String getTitle();
}
