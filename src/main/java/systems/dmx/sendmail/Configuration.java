package systems.dmx.sendmail;

class Configuration {

    // Sender Information
    private final String systemFromName;
    private final String systemFromEmailAddress;

    // Recipient Information
    private final String systemAdminEmailAddress;
    // Plugin Configuration
    private final String sendmailType;

    // SMTP Configuration
    private final String smtpHost;
    private final String smtpUsername;
    private final String smtpPassword;
    private final int smtpPort;
    private final String smtpSecurity;
    private final boolean smtpDebugEnabled;

    private final String sendgridApiKey;

    private final boolean greetingEnabled;

    private final String greetingSubject;
    static final String DEFAULT_GREETING_SUBJECT = "Sendmail Plugin Activated";
    private final String greetingMessage;
    private final String greetingHtmlMessage;

    static final String DEFAULT_GREETING_MESSAGE = "Hello dear, this is your new email sending service.\n\n" +
            "We hope you can enjoy the comforts!";

    Configuration(
            String systemFromName,
            String systemFromEmailAddress,
            String systemAdminEmailAddress,
            String sendmailType,
            String smtpHost,
            String smtpUsername,
            String smtpPassword,
            int smtpPort,
            String smtpSecurity,
            boolean smtpDebugEnabled,
            String sendgridApiKey,
            boolean greetingEnabled,
            String greetingSubject,
            String greetingMessage,
            String greetingHtmlMessage) {
        this.systemFromName = systemFromName;
        this.systemFromEmailAddress = systemFromEmailAddress;
        this.systemAdminEmailAddress = systemAdminEmailAddress;
        this.sendmailType = sendmailType;
        this.smtpHost = smtpHost;
        this.smtpUsername = smtpUsername;
        this.smtpPassword = smtpPassword;
        this.smtpPort = smtpPort;
        this.smtpSecurity = smtpSecurity;
        this.smtpDebugEnabled = smtpDebugEnabled;
        this.sendgridApiKey = sendgridApiKey;
        this.greetingEnabled = greetingEnabled;
        this.greetingSubject = greetingSubject;
        this.greetingMessage = greetingMessage;
        this.greetingHtmlMessage = greetingHtmlMessage;
    }

    static Configuration loadFromPluginProperties() {
        String sendmailTypeArg = System.getProperty("dmx.sendmail.type", "smtp").toLowerCase().trim();

        String systemFromName = System.getProperty("dmx.sendmail.system_from_name");
        String systemFromNameArg = (systemFromName == null) ? "DMX Sendmail" : systemFromName.trim();

        String systemFromEmailAddressArg = System.getProperty("dmx.sendmail.system_from_mailbox", "dmx@localhost").trim();

        String systemAdminEmailAddressArg = System.getProperty("dmx.sendmail.system_admin_mailbox", "root@localhost").trim();

        boolean greetingEnabledArg = Boolean.parseBoolean(System.getProperty("dmx.sendmail.greeting_enabled", "false"));
        String greetingSubjectArg = System.getProperty("dmx.sendmail.greeting_subject", DEFAULT_GREETING_SUBJECT);
        String greetingMessageArg = System.getProperty("dmx.sendmail.greeting_message", DEFAULT_GREETING_MESSAGE);
        String greetingHtmlMessageArg = System.getProperty("dmx.sendmail.greeting_html_message", null);

        String smtpHostArg = System.getProperty("dmx.sendmail.smtp_host", "localhost").trim();

        String smtpUsernameArg = System.getProperty("dmx.sendmail.smtp_username", "").trim();

        String smtpPasswordArg = System.getProperty("dmx.sendmail.smtp_password", "").trim();

        int smtpPortArg = Integer.parseInt(System.getProperty("dmx.sendmail.smtp_port", "25"));

        String smtpSecurityArg = System.getProperty("dmx.sendmail.smtp_security", "").trim();

        boolean smtpDebugEnabledArg = Boolean.parseBoolean(System.getProperty("dmx.sendmail.smtp_debug", "false"));

        String sendgridApiKeyArg = System.getProperty("dmx.sendmail.sendgrid_api_key", null);

        return new Configuration(
                systemFromNameArg,
                systemFromEmailAddressArg,
                systemAdminEmailAddressArg,
                sendmailTypeArg,
                smtpHostArg,
                smtpUsernameArg,
                smtpPasswordArg,
                smtpPortArg,
                smtpSecurityArg,
                smtpDebugEnabledArg,
                sendgridApiKeyArg,
                greetingEnabledArg,
                greetingSubjectArg,
                greetingMessageArg,
                greetingHtmlMessageArg
        );
    }

    public String getSystemFromName() {
        return systemFromName;
    }

    public String getSystemFromEmailAddress() {
        return systemFromEmailAddress;
    }

    public String getSystemAdminEmailAddress() {
        return systemAdminEmailAddress;
    }

    public String getSendmailType() {
        return sendmailType;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public String getSmtpUsername() {
        return smtpUsername;
    }

    public String getSmtpPassword() {
        return smtpPassword;
    }

    public int getSmtpPort() {
        return smtpPort;
    }

    public String getSmtpSecurity() {
        return smtpSecurity;
    }

    public boolean isSmtpDebugEnabled() {
        return smtpDebugEnabled;
    }

    public String getSendgridApiKey() {
        return sendgridApiKey;
    }

    public boolean isGreetingEnabled() {
        return greetingEnabled;
    }

    public String getGreetingSubject() {
        return greetingSubject;
    }

    public String getGreetingMessage() {
        return greetingMessage;
    }

    public String getGreetingHtmlMessage() {
        return greetingHtmlMessage;
    }
}
