package systems.dmx.sendmail;

import systems.dmx.sendmail.util.SendgridMail;
import systems.dmx.sendmail.util.SendgridWebApiV3;

import java.util.logging.Logger;

class SendMailFromToUseCase {

    private static final Logger log = Logger.getLogger(SendMailFromToUseCase.class.getName());

    String sendmailType;

    JavaMail javaMail;

    SendgridWebApiV3 sendgridWebApi;

    SendMailFromToUseCase(String sendMailType, JavaMail javaMail, SendgridWebApiV3 sendgridWebApi) {
        this.sendmailType = sendMailType;
        this.javaMail = javaMail;
        this.sendgridWebApi = sendgridWebApi;
    }

    void invoke(String sender, String senderName, String recipientMailbox, String recipientName, String subject, String textMessage, String htmlMessage) {
        if (textMessage == null && htmlMessage == null) {
            throw new IllegalArgumentException("Either textMessage or htmlMessage must not be null but never both!");
        }
        try {
            // Send mail using the Sendgrid API
            if (sendmailType.equals("sendgrid")) {
                SendgridMail mail = sendgridWebApi.newMailFromTo(sender, senderName, recipientMailbox, recipientName, subject, textMessage, htmlMessage);
                mail.send();
                // Send mail using the SMTP Protocol
            } else if (sendmailType.equals("smtp")) {
                log.info("### recipient=" + recipientMailbox + ", subject=" + subject + ", textMessage=" + textMessage + ", htmlMessage=" + htmlMessage);
                javaMail.send(sender, senderName, recipientMailbox, recipientName, subject, textMessage, htmlMessage);
            } else {
                throw new IllegalStateException("Unknown sendmail type: " + sendmailType);
            }
        } catch (Exception json) {
            throw new RuntimeException("Sending mail via " + sendmailType + " failed", json);
        }
    }

}
