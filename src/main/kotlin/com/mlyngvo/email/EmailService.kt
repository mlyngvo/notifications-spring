package com.mlyngvo.email

import com.mlyngvo.Logger
import jakarta.activation.DataHandler
import jakarta.activation.DataSource
import jakarta.mail.Message
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeBodyPart
import jakarta.mail.internet.MimeMultipart
import jakarta.mail.internet.MimeUtility
import jakarta.mail.util.ByteArrayDataSource
import org.springframework.mail.javamail.JavaMailSender
import java.nio.charset.StandardCharsets
import java.util.Locale

class EmailService(
    private val javaMailSender: JavaMailSender,
    private val emailProperties: EmailProperties,
    private val emailTemplateService: EmailTemplateService,
) : Logger() {

    private val SUBJECT_MAX_LENGTH = 140

    fun send(
        recipients: Array<String>,
        subject: String,
        templateName: String,
        variables: Map<String, Any>?,
        attachments: List<EmailAttachment>,
        locale: Locale
    ) =
        send(null, null, recipients, null, null, subject, templateName, variables, attachments, locale)

    fun send(
        from: String,
        replyTo: String,
        recipients: Array<String>,
        subject: String,
        templateName: String,
        variables: Map<String, Any>?,
        attachments: List<EmailAttachment>,
        locale: Locale
    ) =
        send(from, replyTo, recipients, null, null, subject, templateName, variables, attachments, locale)

    fun send(
        from: String?,
        replyTo: String?,
        recipients: Array<String>,
        cc: Array<String>?,
        bcc: Array<String>?,
        subject: String,
        templateName: String,
        variables: Map<String, Any>?,
        attachments: List<EmailAttachment>,
        locale: Locale
    ) =
        prepareAndSend(EmailRequest(
            from = from,
            replyTo = replyTo,
            recipients = recipients,
            cc = cc,
            bcc = bcc,
            subject = subject,
            templateName = templateName,
            variables = variables,
            attachments = attachments,
            locale = locale
        ))

    private fun parseSubject(subject: String) =
        "${emailProperties.subjectPrefix}${subject}${emailProperties.subjectPostfix}"
            .let {
                if (it.length > SUBJECT_MAX_LENGTH) {
                    it.substring(0, SUBJECT_MAX_LENGTH)
                } else {
                    it
                }
            }


    private fun processTemplate(templateName: String, locale: Locale?, variables: Map<String, Any>?) =
        emailTemplateService.processTemplate("$templateName.html", locale, variables)

    private fun createInlineMultipart(contentId: String, dataSource: DataSource) =
        MimeBodyPart()
            .let {
                it.disposition = MimeBodyPart.INLINE
                it.setHeader("Content-ID", "<$contentId>")
                it.dataHandler = DataHandler(dataSource)
                it
            }

    private fun createAttachmentMultipart(fileName: String, dataSource: DataSource) =
        MimeBodyPart()
            .let {
                it.disposition = MimeBodyPart.ATTACHMENT
                it.fileName = MimeUtility.encodeText(fileName)
                it.dataHandler = DataHandler(dataSource)
                it
            }

    private fun prepareAndSend(request: EmailRequest): String? {
        val message = javaMailSender.createMimeMessage()

        message.subject = parseSubject(request.subject)
        message.setFrom(request.from ?: emailProperties.defaultFrom)

        if (request.replyTo?.isNotBlank() == true) {
            message.replyTo = InternetAddress.parse(request.replyTo)
        }

        if (emailProperties.fixedRecipients?.isNotEmpty() == true) {
            message.setRecipients(Message.RecipientType.TO, emailProperties.fixedRecipients)
        } else {
            if (request.recipients.isEmpty()) {
                log.info("Recipient list is empty. No mail will be sent.")
                return null
            }

            message.setRecipients(Message.RecipientType.TO, request.recipients.map { InternetAddress(it) }.toTypedArray())

            if (request.cc?.isNotEmpty() == true) {
                message.setRecipients(Message.RecipientType.CC, request.cc.map { InternetAddress(it) }.toTypedArray())
            }

            if (request.bcc?.isNotEmpty() == true) {
                message.setRecipients(Message.RecipientType.BCC, request.bcc.map { InternetAddress(it) }.toTypedArray())
            }
        }

        val body = MimeMultipart("alternative")
        if (request.templateName?.isNotBlank() == true) {
            val html = MimeBodyPart()
                .let {
                    it.setText(
                        processTemplate(request.templateName, request.locale, request.variables),
                        StandardCharsets.UTF_8.name(),
                        "html"
                    )
                    it
                }
            body.addBodyPart(html)
        }
        if (request.text?.isNotBlank() == true) {
            val text = MimeBodyPart()
                .let {
                    it.setText(
                        request.text,
                        StandardCharsets.UTF_8.name()
                    )
                    it
                }
            body.addBodyPart(text)
        }

        val wrap = MimeBodyPart()
            .let {
                it.setContent(body)
                it
            }

        val content = MimeMultipart("related")
            .let {
                it.addBodyPart(wrap)
                it
            }

        for (attachment in request.attachments?: emptyList()) {
            val datasource = ByteArrayDataSource(attachment.inputStream, attachment.mimeType)
            if (attachment.contentId?.isNotBlank() == true) {
                content.addBodyPart(createInlineMultipart(attachment.contentId, datasource))
            } else if (attachment.fileName?.isNotBlank() == true) {
                content.addBodyPart(createAttachmentMultipart(attachment.fileName, datasource))
            }
        }

        message.setContent(content)

        javaMailSender.send(message)
        log.info("Email sent. Subject: ${message.subject} - TO: ${request.recipients.joinToString(", ")}")

        return message.messageID
    }
}