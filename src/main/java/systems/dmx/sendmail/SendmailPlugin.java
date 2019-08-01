package systems.dmx.sendmail;

import systems.dmx.sendmail.util.SendgridWebApiV3;
import systems.dmx.sendmail.util.SendgridMail;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.internet.InternetAddress;
import org.apache.commons.mail.HtmlEmail;
import systems.dmx.core.osgi.PluginActivator;
import systems.dmx.core.service.CoreService;
import systems.dmx.core.util.JavaUtils;

/**
 * 
 * @author Malte Reißig <malte@dmx.berlin>
 */
public class SendmailPlugin extends PluginActivator implements SendmailService {

    private static Logger log = Logger.getLogger(SendmailPlugin.class.getName());

    // Sender Information
    private String SYSTEM_FROM_NAME = null;
    private String SYSTEM_FROM_MAILBOX = null;
    // Recipient Information
    private String SYSTEM_ADMIN_MAILBOX = null;
    // Plugin Configuration
    private String SENDMAIL_TYPE = null; // smtp | sendgrid
    // SMTP Configuration
    private String SMTP_HOST = null; // localhost | ip/hostname  
    private String SMTP_USERNAME = null; // empty | username
    private String SMTP_PASSWORD = null; // empty | password
    private int SMTP_PORT = -1; // empty | port
    private String SMTP_SECURITY = null; // empty | tls | smtps
    // Sendgrid API Configuration
    private String SENDGRID_API_KEY = null; // empty

    @Override
    public void init() {
        try {
            loadPluginPropertiesConfig();
            // Test the service and our configuration
            log.info("Sending test mail per " + SENDMAIL_TYPE + " on init to \"" + SYSTEM_ADMIN_MAILBOX + "\"");
            doEmailSystemMailbox("Sendmail Plugin Activated", "Hello dear, this is your new email "
               + "sending service.\n\nWe hope you can enjoy the comforts!");
        } catch (IOException ex) {
            Logger.getLogger(SendmailPlugin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadPluginPropertiesConfig() throws IOException {
        Properties pluginProperties = new Properties();
        pluginProperties.load(getStaticResource("/plugin.properties"));
        SYSTEM_FROM_NAME = pluginProperties.getProperty("dmx.sendmail.system_from_name");
        SYSTEM_FROM_MAILBOX = pluginProperties.getProperty("dmx.sendmail.system_from_mailbox");
        SYSTEM_ADMIN_MAILBOX = pluginProperties.getProperty("dmx.sendmail.system_admin_mailbox");
        SENDMAIL_TYPE = pluginProperties.getProperty("dmx.sendmail.type");
        log.info("dmx.sendmail.system_from_name: " + SYSTEM_FROM_NAME + "\n"
            + "\tdmx.sendmail.system_from_mailbox: " + SYSTEM_FROM_MAILBOX + "\n"
            + "\tdmx.sendmail.system_admin_mailbox: " + SYSTEM_ADMIN_MAILBOX + "\n"
            + "\tdmx.sendmail.type: " + SENDMAIL_TYPE);
        SMTP_HOST = pluginProperties.getProperty("dmx.sendmail.smtp_host");
        SMTP_USERNAME = pluginProperties.getProperty("dmx.sendmail.smtp_username");
        SMTP_PASSWORD = pluginProperties.getProperty("dmx.sendmail.smtp_password");
        SMTP_PORT = Integer.parseInt(pluginProperties.getProperty("dmx.sendmail.smtp_port"));
        SMTP_SECURITY = pluginProperties.getProperty("dmx.sendmail.smtp_security");
        if (SENDMAIL_TYPE.toLowerCase().equals("smtp")) {
        log.info("dmx.sendmail.smtp_host: " + SMTP_HOST + "\n"
            + "\tdmx.sendmail.smtp_username: " + SMTP_USERNAME + "\n"
            + "\tdmx.sendmail.smtp_password: PASSWORD HIDDEN FOR LOG" + "\n"
            + "\tdmx.sendmail.smtp_port: " + SMTP_PORT + "\n"
            + "\tdmx.sendmail.smtp_security: " + SMTP_SECURITY);
        } else if (SENDMAIL_TYPE.toLowerCase().equals("sendgrid")) {
            SENDGRID_API_KEY = pluginProperties.getProperty("dmx.sendmail.sendgrid_api_key");
            if (SENDGRID_API_KEY.isEmpty()) {
                log.severe("Configuration Error: DMX Sendmail is configured to send mails via "
                        + "Sendgrid API but has no\"dmx.sendmail.sendgrid_api_key\" value set");
            } else {
                log.info("dmx.sendmail.sendgrid_api_key: API KEY HIDDEN FOR LOG");
            }
        } else {
            log.severe("Configuration Error: DMX Sendmail has an invalid \"dmx.sendmail.type\" value set");
        }
    }

    @Override
    public void doEmailUser(String username, String subject, String message) {
        String userMailbox = dmx.getPrivilegedAccess().getEmailAddress(username);
        if (userMailbox != null) {
            sendMailTo(userMailbox, subject, message);
        } else {
            log.severe("Sending email notification to user not possible, \""
                    +username+"\" has not signed-up with an Email Address");
        }
    }

    @Override
    public void doEmailUser(String fromUsername, String toUsername, String subject, String message) {
        String senderMailbox = dmx.getPrivilegedAccess().getEmailAddress(toUsername);
        String recipientMailbox = dmx.getPrivilegedAccess().getEmailAddress(toUsername);
        if (recipientMailbox != null && senderMailbox != null) {
            sendMailFromTo(senderMailbox, fromUsername, recipientMailbox, toUsername, subject, message);
        } else {
            log.severe("Sending email notification to user not possible. Either \""
                    +toUsername+"\" or \"" + fromUsername + "\" has not signed-up with an Email Address");
        }
    }

    @Override
    public void doEmailRecipientAs(String from, String fromName,
            String subject, String message, String recipientMail) {
        sendMailFromTo(from, fromName, recipientMail, null, subject, message);
    }

    @Override
    public void doEmailRecipient(String subject, String message, String recipientMail) {
        sendMailTo(recipientMail, subject, message);
    }

    @Override
    public void doEmailSystemMailbox(String subject, String message) {
        sendMailTo(SYSTEM_ADMIN_MAILBOX, subject, message);
    }

    private void sendMailTo(String recipient, String subject, String textMessage) {
        sendMailFromTo(SYSTEM_FROM_MAILBOX, SYSTEM_FROM_NAME, recipient, null, subject, textMessage);
    }

    private void sendMailFromTo(String sender, String senderName, String recipientMailbox,
            String recipientName, String subject, String htmlMessage) {
        try {
            // Send mail using the Sendgrid API
            if (SENDMAIL_TYPE.toLowerCase().equals("sendgrid")) {
                SendgridWebApiV3 mailApi = new SendgridWebApiV3(SENDGRID_API_KEY);
                SendgridMail mail = mailApi.newMailFromTo(sender, senderName, recipientMailbox, recipientName, subject, htmlMessage);
                mail.send();
            // Send mail using the SMTP Protocol
            } else if (SENDMAIL_TYPE.toLowerCase().equals("smtp")) {
                sendSystemMail(recipientMailbox, subject, htmlMessage);
            }
        } catch (Exception json) {
            throw new RuntimeException("Sending mail via " + SENDMAIL_TYPE + " failed", json);
        }
    }
    
    /**
     * @param recipient     String of Email Addresses message is sent to.
     *                      Multiple recipients can be separated by ";". **Must not** be NULL.
     * @param subject       String Subject text for the message.
     * @param htmlMessage       String Text content of the message.
     */
    private void sendSystemMail(String recipient, String subject, String htmlMessage) {
        // Hot Fix: Classloader issue we have in OSGi since using Pax web
        Thread.currentThread().setContextClassLoader(SendmailPlugin.class.getClassLoader());
        log.info("BeforeSend: Set classloader to " + Thread.currentThread().getContextClassLoader().toString());
        HtmlEmail email = new HtmlEmail(); // Include in configurations options?
        email.setDebug(true); // Include in configurations options?
        email.setHostName(SMTP_HOST);
        email.setSmtpPort(SMTP_PORT);
        switch (SMTP_SECURITY.toLowerCase()) {
            case "smtps":
                email.setSSLOnConnect(true);
                email.setSSLCheckServerIdentity(true);
                log.info("Set SSLOnConnect + SSLCheckServerIdentity...");
            case "tls":
                email.setStartTLSEnabled(true);
                email.setStartTLSRequired(true);
                log.info("Set TLSEnabled + TLSRequired...");
        }
        if (!SMTP_USERNAME.isEmpty() && !SMTP_PASSWORD.isEmpty()) {
            log.info("Using SMTP Authentication...");
            email.setAuthentication(SMTP_USERNAME, SMTP_PASSWORD);
        }
        try {
            email.setFrom(SYSTEM_FROM_MAILBOX, SYSTEM_FROM_NAME);
            email.setSubject(subject);
            email.setHtmlMsg(htmlMessage);
            email.setTextMsg(JavaUtils.stripHTML(htmlMessage));
            String recipientValue = recipient.trim();
            Collection<InternetAddress> recipients = new ArrayList<InternetAddress>();
            if (recipientValue.contains(";")) {
                for (String recipientPart : recipientValue.split(";")) {
                    recipients.add(new InternetAddress(recipientPart.trim()));
                }
            } else {
                recipients.add(new InternetAddress(recipientValue));
            }
            email.setTo(recipients);
            email.send();
            log.info("Mail was SUCCESSFULLY sent to " + email.getToAddresses() + " mail addresses");
        } catch (Exception ex) {
            throw new RuntimeException("Sending mail per SMTP FAILED", ex);
        } finally {
            // Fix: Classloader issue we have in OSGi since using Pax web
            Thread.currentThread().setContextClassLoader(CoreService.class.getClassLoader());
            log.info("AfterSend: Set Classloader back " + Thread.currentThread().getContextClassLoader().toString());
        }
    }

}
