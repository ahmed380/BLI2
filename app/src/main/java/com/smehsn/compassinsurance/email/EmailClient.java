package com.smehsn.compassinsurance.email;

import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


public class EmailClient extends javax.mail.Authenticator {


    private static EmailClient myInstance;

    private String    userEmail;
    private String    password;
    private Multipart multipart;


    public static synchronized EmailClient getInstance(String accountEmail, String accountPassword) {
        if (myInstance == null)
            myInstance = new EmailClient(accountEmail, accountPassword);
        return myInstance;
    }


    private EmailClient(String userEmail, String password) {
        super();
        this.userEmail = userEmail;
        this.password = password;

        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);
    }


    public boolean sendSync(Email email)  {
        try {
            String recipients[] = email.getRecipientAddresses();
            File attachments[] = email.getAttachments();
            Properties props = createProperties();
            Session session = Session.getInstance(props, this);

            multipart = new MimeMultipart();
            MimeMessage msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress(userEmail));

            InternetAddress[] addressTo = new InternetAddress[recipients.length];
            for (int i = 0; i < recipients.length; i++) {
                addressTo[i] = new InternetAddress(recipients[i]);
            }
            msg.setRecipients(MimeMessage.RecipientType.TO, addressTo);
            msg.setSubject(email.getSubject());
            msg.setSentDate(new Date());

            // setup message body
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(email.getBody(), "text/html");
            multipart.addBodyPart(messageBodyPart);

            for (File attachment : attachments) {
                MimeBodyPart imagePart = new MimeBodyPart();
                imagePart.attachFile(attachment);
                imagePart.setContentID("<" + "image" + ">");
                imagePart.setDisposition(MimeBodyPart.INLINE);
                multipart.addBodyPart(imagePart);
            }

            // Put parts in message
            msg.setContent(multipart);

            // send email
            Transport.send(msg);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }


    public void sendAsync(Email email, final OnEmailSentListener listener) {
        new AsyncTask<Email, Void, Boolean>(){

            @Override
            protected Boolean doInBackground(Email... params) {
                return sendSync(params[0]);
            }

            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                listener.onEmailSent(success);
            }
        }.execute(email);
    }


    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(userEmail, password);
    }

    private Properties createProperties() {
        String port = "465";
        String sport = "465";
        String host = "smtp.gmail.com";

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.socketFactory.port", sport);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        return props;
    }


    public interface OnEmailSentListener {
        public void onEmailSent(boolean success);
    }



}