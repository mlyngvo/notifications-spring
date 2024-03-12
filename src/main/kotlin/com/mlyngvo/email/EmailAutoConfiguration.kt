package com.mlyngvo.email

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.mail.javamail.JavaMailSender
import org.thymeleaf.TemplateEngine
import org.thymeleaf.spring6.SpringTemplateEngine
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import org.thymeleaf.templateresolver.ITemplateResolver
import java.nio.charset.StandardCharsets

@AutoConfiguration
@EnableConfigurationProperties(EmailProperties::class)
@ConditionalOnProperty(prefix = "notifications", name = ["email.enabled"], havingValue = "true")
class EmailAutoConfiguration(
    private val emailProperties: EmailProperties,
    private val emailSender: JavaMailSender,
    private val messageSource: MessageSource,
) {

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
        EmailService(emailSender, emailProperties, emailTemplateService())
}