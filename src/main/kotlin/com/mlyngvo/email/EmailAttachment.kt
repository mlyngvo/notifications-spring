package com.mlyngvo.email

import java.io.InputStream

data class EmailAttachment(
    val fileName: String?,
    val inputStream: InputStream,
    val mimeType: String,
    val contentId: String? = null,
)
