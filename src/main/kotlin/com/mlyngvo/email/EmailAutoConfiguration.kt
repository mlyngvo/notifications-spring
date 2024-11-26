package com.mlyngvo.email

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.thymeleaf.TemplateEngine
import org.thymeleaf.spring6.SpringTemplateEngine
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import org.thymeleaf.templateresolver.ITemplateResolver
import java.nio.charset.StandardCharsets
import java.util.*


@AutoConfiguration
@EnableConfigurationProperties(EmailProperties::class, EmailSmtpProperties::class)
@ConditionalOnProperty(prefix = "notifications", name = ["email.enabled"], havingValue = "true")
class EmailAutoConfiguration(
    private val emailProperties: EmailProperties,
    private val messageSource: MessageSource,
) {

    @Bean
    fun javaEmailSender(): JavaMailSender =
        JavaMailSenderImpl()
            .let {
                if (emailProperties.smtp != null) {
                    it.host = emailProperties.smtp.host
                    it.port = Integer.parseInt(emailProperties.smtp.port)
                    it.username = emailProperties.smtp.user
                    it.password = emailProperties.smtp.pass
                    it.defaultEncoding = StandardCharsets.UTF_8.name()
                    it.protocol = "smtp"

                    val mailProperties = Properties()
                    mailProperties["mail.smtp.auth"] = true
                    mailProperties["mail.smtp.starttls.enable"] = emailProperties.smtp.starttls
                    mailProperties["mail.smtp.debug"] = emailProperties.debug
                    it.javaMailProperties = mailProperties
                }
                it
            }


    private fun htmlTemplateResolver(): ITemplateResolver =
        ClassLoaderTemplateResolver()
            .let {
                it.order = 1
                it.prefix = "mail/"
                it.suffix = ".html"
                it.templateMode = TemplateMode.HTML
                it.characterEncoding = StandardCharsets.UTF_8.name()
                it.isCacheable = true
                it
            }

    @Bean
    fun templateEngine(): TemplateEngine =
        SpringTemplateEngine()
            .let {
                it.addTemplateResolver(htmlTemplateResolver())
                it.setTemplateEngineMessageSource(messageSource)
                it
            }

    @Bean
    fun emailTemplateService(): EmailTemplateService =
        EmailTemplateService(templateEngine())

    @Bean
    fun emailService(): EmailService =
        EmailService(javaEmailSender(), emailProperties, emailTemplateService())
}
