package systems.dmx.sendmail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import systems.dmx.sendmail.util.SendgridMail;
import systems.dmx.sendmail.util.SendgridWebApiV3;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SendMailFromToUseCaseTest {

    private final SendgridWebApiV3 sendgridWebApiV3 = mock();

    private final JavaMail javaMail = mock();

    private SendMailFromToUseCase createSubject(String sendmailType) {
        return new SendMailFromToUseCase(sendmailType, javaMail, sendgridWebApiV3);
    }

    @Test
    @DisplayName("invoke() should send using SendGrid when sendmailType is 'sendgrid'")
    void invoke_should_use_sendgrid_when_type_is_sendgrid() throws Exception {
        // given:
        SendMailFromToUseCase subject = createSubject("sendgrid");

        SendgridMail mail = mock();
        doNothing().when(mail).send();

        when(sendgridWebApiV3.newMailFromTo(any(), any(), any(), any(), any(), any(), any())).thenReturn(mail);

        String sender = "sender@mail";
        String senderName = "Sender Name";
        String recipientMailbox = "recipient@email";
        String recipientName = "Reci Pient";
        String emailSubject = "Hello World";
        String textMessage = "Big message";
        String htmlMessage = "<html>Big message</html>";

        // when:
        subject.invoke(sender, senderName, recipientMailbox, recipientName, emailSubject, textMessage, htmlMessage);

        // then:
        verify(sendgridWebApiV3).newMailFromTo(sender, senderName, recipientMailbox, recipientName, emailSubject, textMessage, htmlMessage);
        verify(mail).send();
    }

    @Test
    @DisplayName("invoke() should send using SMTP when sendmailType is 'smtp'")
    void invoke_should_use_SMTP_when_type_is_smtp() throws Exception {
        // given:
        SendMailFromToUseCase subject = createSubject("smtp");

        doNothing().when(javaMail).send(any(), any(), any(), any(), any(), any(), any());

        String sender = "sender@mail";
        String senderName = "Sender Name";
        String recipientMailbox = "recipient@email";
        String recipientName = "Reci Pient";
        String emailSubject = "Hello World";
        String textMessage = "Big message";
        String htmlMessage = "<html>Big message</html>";

        // when:
        subject.invoke(sender, senderName, recipientMailbox, recipientName, emailSubject, textMessage, htmlMessage);

        // then:
        verify(javaMail).send(sender, senderName, recipientMailbox, recipientName, emailSubject, textMessage, htmlMessage);
    }

    @Test
    @DisplayName("invoke() should throw exception when sendmailType is neither 'smtp' nor 'sendgrid'")
    void invoke_should_throw() throws Exception {
        // given:
        SendMailFromToUseCase subject = createSubject("someothervalue");

        String sender = "sender@mail";
        String senderName = "Sender Name";
        String recipientMailbox = "recipient@email";
        String recipientName = "Reci Pient";
        String emailSubject = "Hello World";
        String textMessage = "Big message";
        String htmlMessage = "<html>Big message</html>";

        // when / then:
        assertThatThrownBy(
                () -> subject.invoke(
                        sender,
                        senderName,
                        recipientMailbox,
                        recipientName,
                        emailSubject,
                        textMessage,
                        htmlMessage)
        )
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(IllegalStateException.class);
    }
}