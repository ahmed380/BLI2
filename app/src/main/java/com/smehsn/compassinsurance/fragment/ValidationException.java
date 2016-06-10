package com.smehsn.compassinsurance.fragment;


public class ValidationException extends Exception{
    private int pagePosition;
    private String validationErrorMessage;


    public ValidationException(int positionInViewPager, String errorMessage) {
        this.pagePosition = positionInViewPager;
        this.validationErrorMessage = errorMessage;
    }


    public int getPagePosition() {
        return pagePosition;
    }

    public String getValidationErrorMessage() {
        return validationErrorMessage;
    }
}
