package com.mlyngvo.email

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("notifications.email.smtp")
data class EmailSmtpProperties(
    val host: String = "localhost",
    val port: String = "25",
    val user: String = "user",
    val pass: String = "pass",
    val starttls: Boolean = true,
)
