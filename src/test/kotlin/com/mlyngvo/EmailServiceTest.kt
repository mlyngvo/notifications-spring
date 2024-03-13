package com.mlyngvo

import com.mlyngvo.email.EmailService
import com.mlyngvo.email.EmailTemplateService
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.Locale

@SpringBootTest
class EmailServiceTest @Autowired constructor (
    private val emailService: EmailService,
    private val emailTemplateService: EmailTemplateService,
    private val mockSmtpServer: MockSmtpServer,
) {

    @Test
    fun `Can send email`() {
        val variables = emailTemplateService.createVariables()
        emailService.send(arrayOf("recipient@your-domain.com"), "Test email", "test/test-email", variables, emailTemplateService.createAttachments(), Locale.ENGLISH)

        await().until {
            mockSmtpServer.hasRecipients("recipient@your-domain.com")
        }
    }
}