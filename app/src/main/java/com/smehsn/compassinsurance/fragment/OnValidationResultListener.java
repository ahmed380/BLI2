package com.smehsn.compassinsurance.fragment;

/**
 * Created by sam on 6/9/16.
 */
public interface OnValidationResultListener {
    void onValidationResultListener(boolean success, int firstPosition, String firstErrorMessage);
}
