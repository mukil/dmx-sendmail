## DMX Sendmail

This DMX Plugin is a wrapper to the SMTP Protocol and the [Sendgrid Web API v3](https://sendgrid.com/docs/API_Reference/Web_API_v3/index.html). It's core feature is a service which allows other plugins to send emails to the `SYSTEM_ADMIN_MAILBOX` or to one or many other recipients with the sender being `SYSTEM_FROM_NAME` and `SYSTEM_FROM_MAILBOX`.

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

DMX Sendmail software is available freely under the GNU Affero General Public License, version 3.

All third party components incorporated into the DMX Sendmail Software are licensed under the original license provided by the owner of the applicable component.

## Release Notes

**1.2.0**, Upcoming

- Changed license from GPL to AGPL
- Renamed artifact to `dmx-sendmail`
- Adapted to DMX 5.0-beta-4
- Support for sending mails via SMTP or Sendgrid

**1.0**, Feb 12, 2017

* Allows to send simple HTML Mails (From and To) via Sendgrid

## Authors

Copyright (C) 2016-2018 Malte Reißig
Copyright (C) 2019 DMX Systems
