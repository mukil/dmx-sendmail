package systems.dmx.sendmail;

import systems.dmx.core.osgi.PluginActivator;
import systems.dmx.sendmail.util.SendgridWebApiV3;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Malte Reißig <malte@dmx.berlin>
 */
public class SendmailPlugin extends PluginActivator implements SendmailService {

    static final Logger logger = Logger.getLogger(SendmailPlugin.class.getName());

    Configuration configuration;

    SendMailFromToUseCase sendMailFromToUseCase;

    @Override
    public void init() {
        try {
            configuration = Configuration.loadFromPluginProperties();
            sendMailFromToUseCase = new SendMailFromToUseCase(
                    configuration.getSendmailType(),
                    new JavaMail(configuration),
                    new SendgridWebApiV3(configuration.getSendgridApiKey())
            );
            logAndTestConfiguration();
        } catch (IOException ex) {
            Logger.getLogger(SendmailPlugin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void logAndTestConfiguration() throws IOException {
        logger.info("\n  dmx.sendmail.system_from_name: " + configuration.getSystemFromName() + "\n"
                + "  dmx.sendmail.system_from_mailbox: " + configuration.getSystemFromEmailAddress() + "\n"
                + "  dmx.sendmail.system_admin_mailbox: " + configuration.getSystemAdminEmailAddress() + "\n"
                + "  dmx.sendmail.type: " + configuration.getSendmailType());

        logger.info("\n  dmx.sendmail.greeting_enabled: " + configuration.isGreetingEnabled());
        logger.info("\n  dmx.sendmail.greeting_subject: " + (configuration.getGreetingSubject().equals(Configuration.DEFAULT_GREETING_SUBJECT) ?
                "<built-in subject>" : "<custom subject>"));
        logger.info("\n  dmx.sendmail.greeting_message: " + (configuration.getGreetingMessage().equals(Configuration.DEFAULT_GREETING_MESSAGE) ?
                "<built-in message>" : "<custom message>"));
        logger.info("\n  dmx.sendmail.greeting_html_message: "
                + (configuration.getGreetingHtmlMessage() == null ? "<not explicitly set>" : "<custom message>"));

        if (configuration.getSendmailType().equals("smtp")) {
            logger.info("\n  dmx.sendmail.smtp_host: " + configuration.getSmtpHost() + "\n"
                    + "  dmx.sendmail.smtp_username: " + configuration.getSmtpUsername() + "\n"
                    + "  dmx.sendmail.smtp_password: PASSWORD HIDDEN FOR LOG" + "\n"
                    + "  dmx.sendmail.smtp_port: " + configuration.getSmtpPort() + "\n"
                    + "  dmx.sendmail.smtp_security: " + configuration.getSmtpSecurity() + "\n"
                    + "  dmx.sendmail.smtp_debug: " + configuration.isSmtpDebugEnabled());
        } else if (configuration.getSendmailType().equals("sendgrid")) {
            if (configuration.getSendgridApiKey().isEmpty()) {
                logger.severe("Configuration Error: DMX Sendmail is configured to send mails via "
                        + "Sendgrid API but has no\"dmx.sendmail.sendgrid_api_key\" value set");
            } else {
                logger.info("dmx.sendmail.sendgrid_api_key: API KEY HIDDEN FOR LOG");
            }
        } else {
            logger.severe("Configuration Error: DMX Sendmail has an invalid \"dmx.sendmail.type\" value set");
        }
    }

    @Override
    public void doEmailUser(String username, String subject, String message, String htmlMessage) {
        String userMailbox = dmx.getPrivilegedAccess().getEmailAddress(username);
        if (userMailbox != null) {
            sendMailTo(userMailbox, subject, message, htmlMessage);
        } else {
            logger.severe("Sending email notification to user not possible, \""
                    + username + "\" has not signed-up with an Email Address");
        }
    }

    @Override
    public void doEmailUser(String fromUsername, String toUsername, String subject, String message, String htmlMessage) {
        String senderMailbox = dmx.getPrivilegedAccess().getEmailAddress(fromUsername);
        String recipientMailbox = dmx.getPrivilegedAccess().getEmailAddress(toUsername);
        if (recipientMailbox != null && senderMailbox != null) {
            sendMailFromTo(senderMailbox, fromUsername, recipientMailbox, toUsername, subject, message, htmlMessage);
        } else {
            logger.severe("Sending email notification to user not possible. Either \""
                    + toUsername + "\" or \"" + fromUsername + "\" has not signed-up with an Email Address");
        }
    }

    @Override
    public void doEmailRecipientAs(String from, String fromName,
                                   String subject, String message, String htmlMessage, String recipientMail) {
        sendMailFromTo(from, fromName, recipientMail, null, subject, message, htmlMessage);
    }

    @Override
    public void doEmailRecipient(String subject, String message, String htmlMessage, String recipientMail) {
        sendMailTo(recipientMail, subject, message, htmlMessage);
    }

    @Override
    public void doEmailSystemMailbox(String subject, String message, String htmlMessage) {
        sendMailTo(configuration.getSystemAdminEmailAddress(), subject, message, htmlMessage);
    }

    private void sendMailTo(String recipient, String subject, String textMessage, String htmlMessage) {
        sendMailFromTo(configuration.getSystemFromEmailAddress(), configuration.getSystemFromName(), recipient, null, subject, textMessage, htmlMessage);
    }

    private void sendMailFromTo(String sender, String senderName, String recipientMailbox,
                                String recipientName, String subject, String textMessage, String htmlMessage) {
        sendMailFromToUseCase.invoke(sender, senderName, recipientMailbox, recipientName, subject, textMessage, htmlMessage);
    }

}
