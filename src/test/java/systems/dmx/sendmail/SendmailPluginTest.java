package systems.dmx.sendmail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import systems.dmx.core.osgi.PluginActivator;
import systems.dmx.core.service.CoreService;
import systems.dmx.core.service.accesscontrol.PrivilegedAccess;

import java.lang.reflect.Field;
import java.util.logging.Level;

import static org.mockito.Mockito.*;

class SendmailPluginTest {

    private final CoreService dmx = mock();

    private final Configuration configuration = mock();

    private final SendMailFromToUseCase sendMailFromToUseCase = mock();

    private final SendmailPlugin subject = new SendmailPlugin();

    @BeforeEach
    void beforeEach() throws Exception {
        // Silence logger during unit tests
        SendmailPlugin.logger.setLevel(Level.OFF);

        // Sets internal properties
        set(subject, "dmx", dmx);

        subject.configuration = configuration;
        subject.sendMailFromToUseCase = sendMailFromToUseCase;
    }

    private void set(Object o, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = PluginActivator.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(o, value);
    }

    @Test
    @DisplayName("doEmailUser() should send mail with correct arguments")
    void doEmailUser_correct_arguments() {
        // given:
        PrivilegedAccess privilegedAccess = mock();
        when(dmx.getPrivilegedAccess()).thenReturn(privilegedAccess);

        String fromUsername = "From User";
        String fromUserEmailAdress = "fromuser@source";
        when(privilegedAccess.getEmailAddress(fromUsername)).thenReturn(fromUserEmailAdress);

        String toUsername = "To User";
        String toUserEmailAdress = "touser@source";
        when(privilegedAccess.getEmailAddress(toUsername)).thenReturn(toUserEmailAdress);

        String mailSubject = "some subject";
        String mailMessage = "some message";
        String mailHtmlMessage = "some html message";

        // when:
        subject.doEmailUser(fromUsername, toUsername, mailSubject, mailMessage, mailHtmlMessage);

        // then:
        verify(sendMailFromToUseCase).invoke(
                fromUserEmailAdress,
                fromUsername,
                toUserEmailAdress,
                toUsername,
                mailSubject,
                mailMessage,
                mailHtmlMessage);
    }

    @Test
    @DisplayName("doEmailRecipientAs() should send mail with correct arguments")
    void doEmailRecipientAs_correct_arguments() {
        // given:
        String fromEmailAdress = "from@source";
        String fromName = "From Source";

        String mailSubject = "some subject";
        String mailMessage = "some message";
        String mailHtmlMessage = "some html message";
        String recipientEmailAddress = "recipient@somewhere";

        // when:
        subject.doEmailRecipientAs(fromEmailAdress, fromName, mailSubject, mailMessage, mailHtmlMessage, recipientEmailAddress);

        // then:
        verify(sendMailFromToUseCase).invoke(
                fromEmailAdress,
                fromName,
                recipientEmailAddress,
                null,
                mailSubject,
                mailMessage,
                mailHtmlMessage);
    }
    @Test
    @DisplayName("doEmailSystemMailbox() should send mail with correct arguments")
    void doEmailSystemMailbox_correct_arguments() {
        // given:
        String mailSubject = "some subject";
        String mailMessage = "some message";
        String mailHtmlMessage = "some html message";

        String systemFromEmailAddress = "systemfromemailaddress";
        when(configuration.getSystemFromEmailAddress()).thenReturn(systemFromEmailAddress);

        String systemFromName = "master control programm";
        when(configuration.getSystemFromName()).thenReturn(systemFromName);

        String systemAdminEmailAddress = "admin@system";
        when(configuration.getSystemAdminEmailAddress()).thenReturn(systemAdminEmailAddress);

        // when:
        subject.doEmailSystemMailbox(mailSubject, mailMessage, mailHtmlMessage);

        // then:
        verify(sendMailFromToUseCase).invoke(
                systemFromEmailAddress,
                systemFromName,
                systemAdminEmailAddress,
                null,
                mailSubject,
                mailMessage,
                mailHtmlMessage);
    }

    @Test
    @DisplayName("doEmailRecipient() should send mail with correct arguments")
    void doEmailRecipient_correct_arguments() {
        // given:
        String mailSubject = "some subject";
        String mailMessage = "some message";
        String mailHtmlMessage = "some html message";
        String recipientEmailAddress = "recipient@somewhere";

        String systemFromEmailAddress = "systemfromemailaddress";
        when(configuration.getSystemFromEmailAddress()).thenReturn(systemFromEmailAddress);

        String systemFromName = "master control programm";
        when(configuration.getSystemFromName()).thenReturn(systemFromName);

        // when:
        subject.doEmailRecipient(mailSubject, mailMessage, mailHtmlMessage, recipientEmailAddress);

        // then:
        verify(sendMailFromToUseCase).invoke(
                systemFromEmailAddress,
                systemFromName,
                recipientEmailAddress,
                null,
                mailSubject,
                mailMessage,
                mailHtmlMessage);
    }
}
