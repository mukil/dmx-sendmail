package de.mikromedia.sendgrid;

/**
 * 
 * @author Malte Reißig
 */
public interface SendgridService {

    public void doEmailUser(String username, String subject, String message);

    public void doEmailUser(String fromUsername, String toUsername, String subject, String message);

    public void doEmailRecipient(String subject, String message, String recipientEmail);

    public void doEmailRecipientAs(String fromEmail, String fromName, String subject,
            String message, String recipientEmail);

    public void doEmailSystemMailbox(String subject, String message);

}
