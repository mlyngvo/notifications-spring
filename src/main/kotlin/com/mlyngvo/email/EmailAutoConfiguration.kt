package com.mlyngvo.email

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.thymeleaf.TemplateEngine
import org.thymeleaf.spring6.SpringTemplateEngine
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import org.thymeleaf.templateresolver.ITemplateResolver
import java.nio.charset.StandardCharsets

@Configuration
@EnableConfigurationProperties(EmailProperties::class)
@ConditionalOnProperty(prefix = "notifications", name = ["email.enabled"], havingValue = "true")
class EmailAutoConfiguration {

    @Bean
    fun javaMailSender(): JavaMailSender =
        JavaMailSenderImpl()

    private fun htmlTemplateResolver(): ITemplateResolver =
        ClassLoaderTemplateResolver()
            .let {
                it.order = 2
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
                it
            }

    @Bean
    fun emailTemplateService(): EmailTemplateService =
        EmailTemplateService(templateEngine())
}