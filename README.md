## DMX Sendmail

This DMX Plugin is a wrapper to the SMTP Protocol and the [Sendgrid Web API v3](https://sendgrid.com/docs/API_Reference/Web_API_v3/index.html). It's core feature is sending emails.


## Configuration

```
// Plugin Configuration
dmx.sendmail.system_mailbox = your@domain.tld
dmx.sendmail.system_from_mailbox = no-reply@domain.tld
dmx.sendmail.system_from_name = Your System Name
dmx.sendmail.type = smtp

// SMTP Configuration
dmx.sendmail.smtp_host = localhost | ip/hostname
dmx.sendmail.smtp_username = empty | username
dmx.sendmail.smtp_password = empty | password
dmx.sendmail.smtp_port = empty | port
dmx.sendmail.smtp_security = empty | tls | smtps

// Sendgrid Configuration
dmx.sendmail.sendgrid_api_key = empty
```

## License

This is free software and comes to you without any warrant and under the terms of the GNU General Public License 3.0.

## Release Notes

**2.0.0**, Upcoming

**1.1** - 

**1.0**, Feb 12, 2017

* Allows to send simple HTML Mails (From and To)

## Author

Malte Reißig (2016, 2017)

