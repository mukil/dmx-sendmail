
> The `dm4-sendgrid` GitHub repo is a mirror repo. The master repo is `dmx-sendmail`, hosted at the DMX company's [GitLab](https://git.dmx.systems/dmx-plugins/dmx-sendmail). DeepaMehta was rebranded as DMX. DeepaMehta 5 and DMX is the same; same code; same features; similar free software license (AGPL-3.0 vs GPL-3.0). The master branch represents DeepaMehta 5. For DeepaMehta 4 switch to the [dm4 branch](/tree/dm4).

## DMX Sendmail

This DMX Plugin can be used to send e-mails via SMTP or via [Sendgrid Web API v3](https://sendgrid.com/docs/API_Reference/Web_API_v3/index.html). It's core feature is a service which allows other plugins to send emails to the `SYSTEM_ADMIN_MAILBOX` or to one or many other recipients with the sender being `SYSTEM_FROM_NAME` and `SYSTEM_FROM_MAILBOX`.

## Configuration

To configure the dmx-sendmail plugin you can set the following system properties, e.g. trough adding them to the `config.properties` file of your DMX installation (`dmx-platform/conf/config.properties`). When running dmx-platform from sources configuration needs to be done in the platforms `pom.xml`.

The plugins default configuration is represented by the values before the `|` in the following section:

```
## Sendmail Plugin ## 
dmx.sendmail.system_admin_mailbox = root@dlocalhost | admin@domain.tld
dmx.sendmail.system_from_mailbox = dmx@localhost | your@system.com
dmx.sendmail.system_from_name = DMX Sendmail | Your System Name
dmx.sendmail.type = smtp | sendgrid

## Sendmail SMTP Configuration
dmx.sendmail.smtp_host = localhost | ip/hostname
dmx.sendmail.smtp_username = empty | username
dmx.sendmail.smtp_password = empty | password
dmx.sendmail.smtp_port = 25 | port
dmx.sendmail.smtp_security = empty | tls | smtps
dmx.sendmail.smtp_debug = true | false

## Sendmail Sendgrid Configuration
dmx.sendmail.sendgrid_api_key = empty
```

## License

DMX Sendmail software is available freely under the GNU Affero General Public License, version 3.

All third party components incorporated into the DMX Sendmail Software are licensed under the original license provided by the owner of the applicable component.

## Release Notes

**2.0.0**, Dec 15, 2019

- Changed license from GPL to AGPL
- Renamed artifact to `dmx-sendmail`
- Adapted to DMX 5.0-beta-4+
- Support for sending mails via SMTP or Sendgrid

**1.2**, Aug 11, 2019

* Bugfix release for kiezatlas.berlin

**1.0**, Feb 12, 2017

* Allows to send simple HTML Mails (From and To) via Sendgrid

## Authors

Copyright (C) 2016-2018 Malte Reißig<br/>
Copyright (C) 2019 DMX Systems
