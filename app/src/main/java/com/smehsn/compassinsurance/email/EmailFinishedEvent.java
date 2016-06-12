package com.smehsn.compassinsurance.email;


public class EmailFinishedEvent {

    private Email requestedEmail;
    private boolean success;

    public EmailFinishedEvent(Email requestedEmail, boolean success) {
        this.requestedEmail = requestedEmail;
        this.success = success;
    }

    public Email getRequestedEmail() {
        return requestedEmail;
    }

    public boolean isSuccess() {
        return success;
    }
}
