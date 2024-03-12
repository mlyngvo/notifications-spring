package com.mlyngvo.email

import java.util.Locale

internal class EmailRequest(
    val from: String? = null,
    val replyTo: String? = null,
    val recipients: Array<String>,
    val cc: Array<String>? = null,
    val bcc: Array<String>? = null,
    val subject: String,
    val templateName: String? = null,
    val text: String? = null,
    val variables: Map<String, Any>? = null,
    val attachments: List<EmailAttachment>? = null,
    val locale: Locale? = null,
)
