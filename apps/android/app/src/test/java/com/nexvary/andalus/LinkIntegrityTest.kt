package com.nexvary.andalus

import java.net.URI
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LinkIntegrityTest {
    @Test
    fun publicLinksUseExpectedSecureSchemesAndHosts() {
        val httpsLinks = listOf(
            NexvaryLinks.WEBSITE,
            NexvaryLinks.FACEBOOK,
            NexvaryLinks.YOUTUBE,
            NexvaryLinks.X,
        )
        httpsLinks.forEach { value ->
            val uri = URI(value)
            assertEquals("https", uri.scheme)
            assertNotNull(uri.host)
            assertFalse(value.any(Char::isWhitespace))
        }
        val mail = URI(NexvaryLinks.EMAIL)
        assertEquals("mailto", mail.scheme)
        assertTrue(mail.schemeSpecificPart.contains("info@nexvary.com"))
    }

    @Test
    fun everyInternalRouteIsUniqueAndEveryServiceHasCopyInAllLanguages() {
        assertEquals(StudioRoutes.allInternalRoutes.size, StudioRoutes.allInternalRoutes.distinct().size)
        AppLanguage.entries.forEach { language ->
            val services = StudioLocalization.services(language)
            assertEquals(StudioRoutes.serviceIds.toSet(), services.keys)
            services.values.forEach {
                assertTrue(it.title.isNotBlank())
                assertTrue(it.subtitle.isNotBlank())
            }
        }
    }

    @Test
    fun rtlLanguagesAreExactlyArabicUrduAndPersian() {
        val rtlCodes = AppLanguage.entries.filter { it.rtl }.map { it.code }.toSet()
        assertEquals(setOf("ar", "ur", "fa"), rtlCodes)
    }
}
