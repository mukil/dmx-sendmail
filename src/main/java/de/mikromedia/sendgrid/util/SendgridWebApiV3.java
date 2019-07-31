package de.mikromedia.sendgrid.util;

import java.net.MalformedURLException;
import java.util.logging.Logger;
import org.codehaus.jettison.json.JSONException;

/**
 *
 * @author Malte Reißig
 */
public class SendgridWebApiV3 {

    private static Logger log = Logger.getLogger(SendgridWebApiV3.class.getName());

    private String apiKey = "";
    private String defaultFrom = "";
    private String defaultFromName = "";

    protected final static String TO = "to";
    protected final static String CC = "cc";
    protected final static String BCC = "bcc";
    
    protected final static String PERSONALIZATIONS = "personalizations";
    protected final static String MAILBOX = "email";
    protected final static String NAME = "name";
    protected final static String SUBJECT = "subject";

    public SendgridWebApiV3(String sendgridApiKey) throws MalformedURLException {
        this.apiKey = sendgridApiKey;
    }

    public SendgridWebApiV3(String sendgridApiKey, String from, String fromName) throws MalformedURLException {
        this.apiKey = sendgridApiKey;
        this.defaultFrom = from;
        this.defaultFromName = fromName;
    }

    public SendgridMail newMailFromTo(String from, String fromName, String recipients, String recipientName,
            String subject, String message) throws JSONException {
        SendgridMail sendgridMail = new SendgridMail(this.apiKey, from, fromName);
        if (recipients.contains(";")) {
            String[] recipientList = recipients.split(";");
            for (int i = 0; i < recipientList.length; i++) {
                String recipient = recipientList[i];
                sendgridMail.addRecipient(recipient, null, TO, subject);
            }
        } else {
            sendgridMail.addRecipient(recipients, recipientName, TO, subject);
        }
        sendgridMail.addHTMLTextMessage(message);;
        return sendgridMail;
    }
    
    public SendgridMail newMailTo(String recipient, String subject, String message) throws JSONException {
        SendgridMail sendgridMail = new SendgridMail(this.apiKey, defaultFrom, defaultFromName);
        sendgridMail.addRecipient(recipient, null, TO, subject);
        sendgridMail.addHTMLTextMessage(message);;
        return sendgridMail;
    }

}
