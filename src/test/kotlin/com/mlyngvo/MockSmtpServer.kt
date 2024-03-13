package com.mlyngvo

import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.ServerSetup
import org.springframework.stereotype.Service

@Service
class MockSmtpServer {

    private val greenMail: GreenMail = GreenMail(ServerSetup.SMTP.port(2525))

    init {
        greenMail.setUser("user", "pass")
        greenMail.start()
    }

    fun hasRecipients(email: String): Boolean {
        for (message in greenMail.receivedMessages) {
            for (recipient in message.allRecipients) {
                if (recipient.toString() == email) return true
            }
        }
        return false
    }
}