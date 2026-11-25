# notifications-spring

A Spring Boot auto-configuration library providing notification implementations (starting with templated SMTP email) for Spring Boot applications.

[![CI](https://github.com/mlyngvo/notifications-spring/actions/workflows/ci.yml/badge.svg)](https://github.com/mlyngvo/notifications-spring/actions/workflows/ci.yml)

## Features

- SMTP email delivery backed by Spring Boot Mail (`JavaMailSender`)
- HTML email templating with Thymeleaf and `MessageSource` i18n support
- Multi-brand style and inline image configuration via classpath properties (`mail/style/<brand>/`)
- File attachments and inline (`cid:`) image support via `EmailAttachmentService`
- Safety guardrails including configurable subject prefix/postfix and `fixed-recipients` override for non-production environments
- Zero-config Spring Boot auto-configuration — enable via properties and inject `EmailService`

## Requirements

- Java 21+
- Spring Boot 4.x

## Installation

The library is published to [Maven Central](https://central.sonatype.com/artifact/dev.mlyngvo/notifications-spring).

### Gradle (Kotlin DSL)

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("dev.mlyngvo:notifications-spring:<version>")
}
```

### Maven

```xml
<dependency>
    <groupId>dev.mlyngvo</groupId>
    <artifactId>notifications-spring</artifactId>
    <version>VERSION</version>
</dependency>
```

## Configuration

Add the following to your `application.yml`:

```yaml
notifications:
  email:
    enabled: true
    debug: false
    default-from: "noreply@example.com"
    subject-prefix: ""           # optional prefix prepended to subjects
    subject-postfix: ""          # optional postfix appended to subjects
    fixed-recipients: ""         # optional; redirects all outgoing mail (useful in staging/dev)
    smtp:
      host: "smtp.example.com"
      port: "587"
      user: "smtp-user"
      pass: "smtp-password"
      starttls: true
      ssl: false
```

`EmailAutoConfiguration` is only activated when `notifications.email.enabled=true`.

### Templates & Branding

Templates are resolved from `src/main/resources/mail/` with the `.html` suffix (for example, template name `"welcome/greeting"` resolves to `src/main/resources/mail/welcome/greeting.html`).

Optional branding styles and inline images can be defined under `src/main/resources/mail/style/<brandingSlug>/` (falling back to `default`):

- `mail/style/default/style.properties` — exposed as `${style}` in Thymeleaf templates:
  ```properties
  button=display:inline-block; padding:12px 24px; background-color:#1976d2; color:#fff;
  ```
- `mail/style/default/image.properties` — exposed as `${image}` (with `cid:<key>` `src`) and converted to inline attachments by `emailTemplateService.createAttachments()`:
  ```properties
  logo=mail/style/logo.png
  logo.width=35
  logo.height=35
  logo.type=image/png
  ```

## Usage

### Sending a templated email

Inject `EmailService` and `EmailTemplateService`:

```kotlin
@Service
class WelcomeNotifier(
    private val emailService: EmailService,
    private val emailTemplateService: EmailTemplateService,
) {

    fun sendWelcome(recipient: String, locale: Locale) {
        val variables = emailTemplateService.createVariables()
        variables["userName"] = "Jane"

        val attachments = emailTemplateService.createAttachments()

        emailService.send(
            recipients = arrayOf(recipient),
            subject = "Welcome!",
            templateName = "welcome/greeting",
            variables = variables,
            attachments = attachments,
            locale = locale,
        )
    }
}
```

### Adding attachments

Use `EmailAttachmentService` helper methods to attach files or inline assets:

```kotlin
val pdfAttachment = EmailAttachmentService.from("invoice.pdf", "application/pdf", pdfBytes)
val inlineBanner = EmailAttachmentService.makeInline("banner", "image/png", bannerBytes)
```

## Components

| Class | Description |
|---|---|
| `NotificationsAutoConfiguration` | Root Spring Boot auto-configuration entry point |
| `EmailAutoConfiguration` | Conditional auto-configuration (`notifications.email.enabled=true`) wiring `JavaMailSender`, Thymeleaf `TemplateEngine`, `EmailTemplateService`, and `EmailService` |
| `EmailService` | Composes and sends multipart HTML/text emails with attachments and inline assets |
| `EmailTemplateService` | Renders Thymeleaf templates and loads branding styles and inline image attachments |
| `EmailAttachmentService` | Factory helpers for file and inline (`cid:`) `EmailAttachment` instances |
| `EmailProperties` | `@ConfigurationProperties` binding for `notifications.email.*` |
| `EmailSmtpProperties` | `@ConfigurationProperties` binding for `notifications.email.smtp.*` |

## License

MIT
