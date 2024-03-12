package com.mlyngvo

import com.mlyngvo.email.EmailService
import com.mlyngvo.email.EmailTemplateService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.Locale

@SpringBootTest
class EmailServiceTest @Autowired constructor (
    private val emailService: EmailService,
    private val emailTemplateService: EmailTemplateService,
) {

//    @Test
//    fun `Can send email`() {
//        val variables = emailTemplateService.createVariables()
//        emailService.send(arrayOf("contact@minhlynguyenvo.com"), "Test email", "test/test-email", variables, emailTemplateService.createAttachments(), Locale.ENGLISH)
//    }
}