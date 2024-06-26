package systems.dmx.sendmail;

import org.apache.commons.mail.HtmlEmail;
import systems.dmx.core.service.CoreService;
import systems.dmx.core.util.JavaUtils;

import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

class JavaMail {

    private static final Logger log = Logger.getLogger(JavaMail.class.getName());

    Configuration configuration;

    JavaMail(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * @param recipient   String of Email Addresses message is sent to.
     *                    Multiple recipients can be separated by ";". **Must not** be NULL.
     * @param subject     String Subject text for the message.
     * @param textMessage plain text content of the message, or null.
     * @param htmlMessage HTML content of the message, or null.
     */
    void send(String from, String fromName, String recipient, String recipientName, String subject, String textMessage, String htmlMessage) {
        // Hot Fix: Classloader issue we have in OSGi since using Pax web
        Thread.currentThread().setContextClassLoader(JavaMail.class.getClassLoader());
        log.info("BeforeSend: Set classloader to " + Thread.currentThread().getContextClassLoader().toString());
        HtmlEmail email = new HtmlEmail(); // Include in configurations options?
        email.setDebug(configuration.isSmtpDebugEnabled());
        email.setHostName(configuration.getSmtpHost());
        email.setSmtpPort(configuration.getSmtpPort());
        if (configuration.getSmtpSecurity().equals("smtps")) {
            email.setSslSmtpPort(String.valueOf(configuration.getSmtpPort()));
            email.setSSLOnConnect(true);
            log.info("Set SSLOnConnect...");
        } else if (configuration.getSmtpSecurity().equals("tls")) {
            email.setSslSmtpPort(String.valueOf(configuration.getSmtpPort()));
            email.setSSLOnConnect(true);
            email.setStartTLSEnabled(true);
            log.info("Set SSLOnConnect + StartTLSEnabled...");
        }
        // SMTP Auth
        if (!configuration.getSmtpUsername().isEmpty() && !configuration.getSmtpPassword().isEmpty()) {
            log.info("Using SMTP Authentication...");
            email.setAuthentication(configuration.getSmtpUsername(), configuration.getSmtpPassword());
        }
        try {
            email.setFrom(from, fromName);
            email.setSubject(subject);
            // send either plaintext or html or multipart message depending on given texts
            if (textMessage != null) {
                email.setTextMsg(textMessage);
            }
            if (htmlMessage != null) {
                email.setHtmlMsg(new String(htmlMessage.getBytes("UTF-8"), 0));
                // https://stackoverflow.com/questions/56150300/encode-to-utf-8-encode-character-eg-%C3%B6-to-%C3%83
            }
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
