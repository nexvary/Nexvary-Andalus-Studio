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
        val expectedHosts = mapOf(
            NexvaryLinks.WEBSITE to "nexvary.com",
            NexvaryLinks.FACEBOOK to "www.facebook.com",
            NexvaryLinks.YOUTUBE to "www.youtube.com",
            NexvaryLinks.X to "x.com",
        )

        expectedHosts.forEach { (value, expectedHost) ->
            val uri = URI(value)
            assertEquals("https", uri.scheme)
            assertNotNull(uri.host)
            assertEquals(expectedHost, uri.host)
            assertFalse(value.any(Char::isWhitespace))
        }

        val mail = URI(NexvaryLinks.EMAIL)
        assertEquals("mailto", mail.scheme)
        assertTrue(mail.schemeSpecificPart.contains("info@nexvary.com"))
    }

    @Test
    fun publicDestinationsRemainExactlyTheApprovedNexvaryLinks() {
        assertEquals("https://nexvary.com/", NexvaryLinks.WEBSITE)
        assertEquals("https://www.facebook.com/share/14p9krEn5ij/", NexvaryLinks.FACEBOOK)
        assertEquals("mailto:info@nexvary.com", NexvaryLinks.EMAIL)
        assertEquals("https://www.youtube.com/@NexvaryInc", NexvaryLinks.YOUTUBE)
        assertEquals("https://x.com/Nexvary", NexvaryLinks.X)
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
                assertTrue(it.badge.isNotBlank())
            }

            val ui = StudioLocalization.ui(language)
            assertTrue(ui.home.isNotBlank())
            assertTrue(ui.services.isNotBlank())
            assertTrue(ui.about.isNotBlank())
            assertTrue(ui.back.isNotBlank())
            assertTrue(ui.language.isNotBlank())
            assertTrue(ui.aboutTitle.isNotBlank())
            assertTrue(ui.contactTitle.isNotBlank())
        }
    }

    @Test
    fun supportedLanguageSetIsExactlyTheReleaseSet() {
        val supported = AppLanguage.entries.map { it.code }.toSet()
        assertEquals(
            setOf("ar", "en", "tr", "es", "de", "it", "fr", "ur", "fa", "ru"),
            supported,
        )
    }

    @Test
    fun rtlLanguagesAreExactlyArabicUrduAndPersian() {
        val rtlCodes = AppLanguage.entries.filter { it.rtl }.map { it.code }.toSet()
        assertEquals(setOf("ar", "ur", "fa"), rtlCodes)
    }
}
