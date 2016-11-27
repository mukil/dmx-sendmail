package de.mikromedia.sendgrid;

import de.mikromedia.sendgrid.util.SendgridWebApiV3;
import de.deepamehta.core.osgi.PluginActivator;
import de.mikromedia.sendgrid.util.SendgridMail;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Malte Reißig
 */
public class SendgridPlugin extends PluginActivator implements SendgridService {

    private static Logger log = Logger.getLogger(SendgridPlugin.class.getName());

    public String SENDGRID_FROM_MAILBOX = null;
    public String SENDGRID_FROM_NAME = null;
    private String SENDGRID_SYSTEM_MAILBOX = null;
    private String SENDGRID_API_KEY = null;

    @Override
    public void init() {
        try {
            loadPluginPropertiesConfig();
            doEmailSystemMailbox("Sendgrid Plugin Activated", "Hello dear, this is your new email "
                    + "sending service.\n\nWe hope you can enjoy the comforts!");
        } catch (IOException ex) {
            Logger.getLogger(SendgridPlugin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadPluginPropertiesConfig() throws IOException {
        Properties pluginProperties = new Properties();
        pluginProperties.load(getStaticResource("/plugin.properties"));
        SENDGRID_FROM_MAILBOX = pluginProperties.getProperty("dm4.sendgrid.from_mailbox");
        SENDGRID_FROM_NAME = pluginProperties.getProperty("dm4.sendgrid.from_name");
        log.info("Sendgrid Plugin From: " + SENDGRID_FROM_NAME + "<" + SENDGRID_FROM_MAILBOX+">");
        SENDGRID_SYSTEM_MAILBOX = pluginProperties.getProperty("dm4.sendgrid.system_mailbox");
        log.info("Sendgrid System Mailbox: " + SENDGRID_SYSTEM_MAILBOX);
        SENDGRID_API_KEY = pluginProperties.getProperty("dm4.sendgrid.api_key");
    }

    @Override
    public void doEmailUser(String username, String subject, String message) {
        String userMailbox = dm4.getAccessControl().getEmailAddress(username);
        if (userMailbox != null) {
            sendPlainMailTo(userMailbox, subject, message);
        } else {
            log.severe("Sending email notification to user not possible, \""
                    +username+"\" has not signed-up with an Email Address");
        }
    }

    @Override
    public void doEmailUser(String fromUsername, String toUsername, String subject, String message) {
        String senderMailbox = dm4.getAccessControl().getEmailAddress(toUsername);
        String recipientMailbox = dm4.getAccessControl().getEmailAddress(toUsername);
        if (recipientMailbox != null && senderMailbox != null) {
            sendPlainMailFromTo(senderMailbox, fromUsername, recipientMailbox, toUsername, subject, message);
        } else {
            log.severe("Sending email notification to user not possible. Either \""
                    +toUsername+"\" or \"" + fromUsername + "\" has not signed-up with an Email Address");
        }
    }
    
    @Override
    public void doEmailSystemMailbox(String subject, String message) {
        sendPlainMailTo(SENDGRID_SYSTEM_MAILBOX, subject, message);
    }

    private void sendPlainMailFromTo(String sender, String senderName, String recipientMailbox, String recipientName, String subject, String textMessage) {
        try {
            SendgridWebApiV3 mailApi = new SendgridWebApiV3(SENDGRID_API_KEY);
            SendgridMail mail = mailApi.newMailFromTo(sender, senderName, recipientMailbox, recipientName, subject, textMessage);
            mail.send();
        } catch (Exception json) {
            throw new RuntimeException("Sending mail via SendgridWebAPIv3 failed", json);
        }
    }
    
    private void sendPlainMailTo(String recipient, String subject, String textMessage) {
        try {
            SendgridWebApiV3 mailApi = new SendgridWebApiV3(SENDGRID_API_KEY);
            SendgridMail mail = mailApi.newMailFromTo(SENDGRID_FROM_MAILBOX, SENDGRID_FROM_NAME, recipient, null, subject, textMessage);
            mail.send();
        } catch (Exception json) {
            throw new RuntimeException("Sending mail via SendgridWebAPIv3 failed", json);
        }
    }

}
