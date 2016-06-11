package com.smehsn.compassinsurance.model;

public class PhotoAttachment {
    private String vinNumberPhoto;
    private String drivingLicensePhoto;

    public PhotoAttachment(){}

    public PhotoAttachment(String drivingLicensePhoto, String vinNumberPhoto) {
        this.drivingLicensePhoto = drivingLicensePhoto;
        this.vinNumberPhoto = vinNumberPhoto;
    }

    public void setVinNumberPhoto(String vinNumberPhoto) {
        this.vinNumberPhoto = vinNumberPhoto;
    }

    public void setDrivingLicensePhoto(String drivingLicensePhoto) {
        this.drivingLicensePhoto = drivingLicensePhoto;
    }
}
