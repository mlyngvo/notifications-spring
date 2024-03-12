package com.mlyngvo.email

import com.mlyngvo.Logger
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.support.PropertiesLoaderUtils
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.io.IOException
import java.util.Locale
import java.util.stream.Collectors

@Service
class EmailTemplateService(
    private val templateEngine: TemplateEngine
): Logger() {

    private val DEFAULT_BRANDING_SLUG = "default"
    private val STYLE_TYPE = "style"
    private val IMAGE_TYPE = "image"

    fun processTemplate(templateName: String, context: Context): String =
        templateEngine.process(templateName, context)

    fun processTemplate(templateName: String, locale: Locale?, variables: Map<String, Any>?): String =
        processTemplate(templateName, Context(locale, variables))

    fun createAttachments(): List<EmailAttachment> =
        createAttachments(DEFAULT_BRANDING_SLUG)

    fun createAttachments(brandingSlug: String) =
        getImageEnv(brandingSlug).entries
            .mapNotNull {
                try {
                    EmailAttachmentService.makeInline(
                        it.key,
                        it.value["type"]!!,
                        ClassPathResource(it.value["resourcePath"]!!).inputStream
                    )
                } catch (exception: Exception) {
                    log.error("Could not inline attachment ${it.key}.")
                    null
                }
            }

    fun createVariables() =
        createVariables(DEFAULT_BRANDING_SLUG)

    fun createVariables(brandingSlug: String) =
        hashMapOf<String, Any>()
            .let {
                it["style"] = getStyleEnv(brandingSlug)
                it["image"] = getImageEnv(brandingSlug)
                it
            }

    private fun getStyleEnv(brandingSlug: String) =
        getMergedProperties(brandingSlug, STYLE_TYPE)

    private fun getImageEnv(brandingSlug: String): Map<String, Map<String, String>> {
        val properties = getMergedProperties(brandingSlug, IMAGE_TYPE)
        return properties.keys
            .filter { !it.contains(".") }
            .associateWith {
                val image = hashMapOf<String, String>()
                image["src"] = "cid:$it"
                image["width"] = properties["$it.width"] ?: ""
                image["height"] = properties["$it.height"] ?: ""
                image["type"] = properties["$it.type"] ?: ""
                image["resourcePath"] = properties[it] ?: ""
                image
            }
            .toMap()
    }

    private fun getMergedProperties(brandingSlug: String, type: String): Map<String, String> {
        val properties = HashMap(getProperties(DEFAULT_BRANDING_SLUG, type))

        if (DEFAULT_BRANDING_SLUG != brandingSlug) {
            properties.putAll(getProperties(brandingSlug, type))
        }

        return properties
    }

    private fun getProperties(brandingSlug: String, type: String): Map<String, String> {
        try {
            val resource = ClassPathResource("mail/style/$brandingSlug/$type.properties")
            val properties = PropertiesLoaderUtils.loadProperties(resource)
            return properties.stringPropertyNames()
                .associateWith { properties.getProperty(it) }
        } catch (exception: IOException) {
            log.error("Could not read $type properties. Return empty map.")
            return emptyMap()
        }
    }

}