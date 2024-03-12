package com.mlyngvo.email

import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.URL
import java.util.Base64

class EmailAttachmentService {

    companion object {

        fun from(fileName: String, mimeType: String, inputStream: InputStream) =
            EmailAttachment(fileName, inputStream, mimeType, null)

        fun from(fileName: String, mimeType: String, data: ByteArray) =
            EmailAttachment(fileName, ByteArrayInputStream(data), mimeType, null)

        fun from(url: URL): EmailAttachment {
            val path = url.path
            val fileName = Base64.getEncoder().encode(path.substring(0, 20).encodeToByteArray()).toString()
            return from(url, fileName)
        }

        fun from(url: URL, fileName: String): EmailAttachment =
            url.openConnection()
                .let {
                    val result = EmailAttachment(fileName, it.getInputStream(), it.contentType, null)
                    result
                }

        fun makeInline(contentId: String, mimeType: String, inputStream: InputStream) =
            EmailAttachment(null, inputStream, mimeType, contentId)

        fun makeInline(contentId: String, mimeType: String, data: ByteArray) =
            EmailAttachment(null, ByteArrayInputStream(data), mimeType, contentId)

        fun makeInline(contentId: String, url: URL) =
            url.openConnection()
                .let {
                    val result = EmailAttachment(null, it.getInputStream(), it.contentType, contentId)
                    result
                }
    }
}