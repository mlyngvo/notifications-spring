package com.mlyngvo.email

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("notifications.email")
data class EmailProperties(
    val enabled: Boolean = false,
    val debug: Boolean = false,
    val smtp: EmailSmtpProperties? = null,
    val subjectPrefix: String = "",
    val subjectPostfix: String = "",
    val defaultFrom: String? = null,
    val fixedRecipients: String? = null
)
