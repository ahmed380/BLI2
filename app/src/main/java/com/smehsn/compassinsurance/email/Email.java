package com.smehsn.compassinsurance.email;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Email {
    private String       subject;
    private String       body;
    private String[]     recipientAddresses;


    private List<File> attachments = new ArrayList<>();
    public Email(){};


    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public String[] getRecipientAddresses() {
        return recipientAddresses;
    }

    public File[] getAttachments() {
        return attachments.toArray(new File[attachments.size()]);
    }


    public Email setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public Email setBody(String body) {
        this.body = body;
        return this;
    }

    public Email setRecipientAddresses(String [] recipientAddresses) {
        this.recipientAddresses = recipientAddresses;
        return this;
    }


    public Email setAttachments(List<File> attachments) {
        for (File f: attachments)
            this.attachments.add(f);
        return this;
    }

    public Email addAttachment(File attachment){
        this.attachments.add(attachment);
        return this;
    }
}
