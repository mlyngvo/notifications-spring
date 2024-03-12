package com.mlyngvo.email

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("notifications.email")
data class EmailProperties(
    val enabled: Boolean = false,
    val subjectPrefix: String? = null,
    val subjectPostfix: String? = null,
    val defaultFrom: String = "",
    val fixedRecipients: String? = null
)
