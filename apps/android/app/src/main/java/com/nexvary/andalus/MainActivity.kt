package com.nexvary.andalus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

private val Navy = Color(0xFF06131F)
private val Panel = Color(0xFF0D2630)
private val Emerald = Color(0xFF1D735F)
private val Gold = Color(0xFFD6B85E)
private val Ivory = Color(0xFFF3ECD8)
private val Muted = Color(0xFF91AAA5)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AndalusTheme { StudioApp() } }
    }
}

@Composable
private fun AndalusTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Gold,
            secondary = Emerald,
            background = Navy,
            surface = Panel,
            onPrimary = Navy,
            onBackground = Ivory,
            onSurface = Ivory,
        ),
        content = content,
    )
}

@Composable
private fun StudioApp() {
    var destination by rememberSaveable { mutableStateOf(StudioRoutes.HOME) }
    var languageCode by rememberSaveable { mutableStateOf(AppLanguage.AR.code) }
    val language = AppLanguage.entries.firstOrNull { it.code == languageCode } ?: AppLanguage.AR
    val ui = StudioLocalization.ui(language)
    val layoutDirection = if (language.rtl) LayoutDirection.Rtl else LayoutDirection.Ltr
    val isFeature = destination in StudioRoutes.serviceIds

    BackHandler(enabled = isFeature) { destination = StudioRoutes.SERVICES }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Navy,
            topBar = {
                StudioTopBar(
                    language = language,
                    ui = ui,
                    onLanguageChanged = { languageCode = it.code },
                )
            },
            bottomBar = {
                if (!isFeature) {
                    PrimaryNavigation(
                        destination = destination,
                        ui = ui,
                        onNavigate = { destination = it },
                    )
                }
            },
        ) { innerPadding ->
            when (destination) {
                StudioRoutes.HOME -> HomeScreen(
                    padding = innerPadding,
                    ui = ui,
                    onStart = { destination = StudioRoutes.SERVICES },
                )
                StudioRoutes.SERVICES -> ServicesScreen(
                    padding = innerPadding,
                    language = language,
                    ui = ui,
                    onOpen = { destination = it },
                )
                StudioRoutes.ABOUT -> AboutScreen(padding = innerPadding, ui = ui)
                else -> FeatureScreen(
                    key = destination,
                    padding = innerPadding,
                    language = language,
                    ui = ui,
                    onBack = { destination = StudioRoutes.SERVICES },
                )
            }
        }
    }
}

@Composable
private fun StudioTopBar(
    language: AppLanguage,
    ui: UiCopy,
    onLanguageChanged: (AppLanguage) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Surface(color = Navy) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .safeDrawingPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Nexvary Andalus Studio", color = Gold, fontWeight = FontWeight.Bold)
                Text(ui.stage, color = Muted, style = MaterialTheme.typography.labelSmall)
            }
            Box {
                TextButton(
                    onClick = { expanded = true },
                    modifier = Modifier.testTag("language-picker"),
                ) {
                    Icon(Icons.Outlined.Language, contentDescription = ui.language, tint = Gold)
                    Text(" ${language.nativeName}", color = Ivory)
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    AppLanguage.entries.forEach { item ->
                        DropdownMenuItem(
                            modifier = Modifier.testTag("lang-${item.code}"),
                            text = { Text(item.nativeName) },
                            onClick = {
                                expanded = false
                                onLanguageChanged(item)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PrimaryNavigation(destination: String, ui: UiCopy, onNavigate: (String) -> Unit) {
    NavigationBar(containerColor = Panel) {
        NavigationBarItem(
            modifier = Modifier.testTag("nav-home"),
            selected = destination == StudioRoutes.HOME,
            onClick = { onNavigate(StudioRoutes.HOME) },
            icon = { Icon(Icons.Outlined.Home, contentDescription = ui.home) },
            label = { Text(ui.home) },
        )
        NavigationBarItem(
            modifier = Modifier.testTag("nav-services"),
            selected = destination == StudioRoutes.SERVICES,
            onClick = { onNavigate(StudioRoutes.SERVICES) },
            icon = { Icon(Icons.Outlined.Build, contentDescription = ui.services) },
            label = { Text(ui.services) },
        )
        NavigationBarItem(
            modifier = Modifier.testTag("nav-about"),
            selected = destination == StudioRoutes.ABOUT,
            onClick = { onNavigate(StudioRoutes.ABOUT) },
            icon = { Icon(Icons.Outlined.Info, contentDescription = ui.about) },
            label = { Text(ui.about) },
        )
    }
}

@Composable
private fun HomeScreen(padding: PaddingValues, ui: UiCopy, onStart: () -> Unit) {
    PageFrame(padding = padding, testTag = "screen-home") {
        Text(
            "Nexvary Andalus Studio",
            modifier = Modifier.fillMaxWidth(),
            color = Gold,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start,
        )
        Text(ui.tagline, color = Ivory, textAlign = TextAlign.Start)
        Spacer(Modifier.height(10.dp))
        Text(ui.homeIntro, color = Muted, textAlign = TextAlign.Start)
        Spacer(Modifier.height(20.dp))
        StatusCard(ui)
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = onStart,
            modifier = Modifier.fillMaxWidth().testTag("home-start-services"),
            colors = ButtonDefaults.buttonColors(containerColor = Gold, contentColor = Navy),
        ) {
            Text(ui.startDesigning, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun StatusCard(ui: UiCopy) {
    Surface(color = Panel, shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusLine(Icons.Outlined.Star, ui.releaseReady)
            StatusLine(Icons.Outlined.Search, ui.testedNavigation)
            StatusLine(Icons.Outlined.Lock, ui.secureImports)
        }
    }
}

@Composable
private fun StatusLine(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Icon(icon, contentDescription = null, tint = Emerald, modifier = Modifier.size(20.dp))
        Text(text, color = Ivory)
    }
}

@Composable
private fun ServicesScreen(
    padding: PaddingValues,
    language: AppLanguage,
    ui: UiCopy,
    onOpen: (String) -> Unit,
) {
    val services = StudioLocalization.services(language)
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Navy)
            .testTag("screen-services")
            .padding(padding)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Text(
                ui.services,
                modifier = Modifier.fillMaxWidth().widthIn(max = 760.dp),
                color = Gold,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
            )
        }
        items(StudioRoutes.serviceIds, key = { it }) { key ->
            val copy = requireNotNull(services[key])
            ServiceCard(key = key, copy = copy, onClick = { onOpen(key) })
        }
        item { Spacer(Modifier.height(12.dp)) }
    }
}

@Composable
private fun ServiceCard(key: String, copy: ServiceCopy, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 760.dp)
            .testTag("service-$key")
            .border(1.dp, Gold.copy(alpha = 0.24f), RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = Panel),
        shape = RoundedCornerShape(18.dp),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(color = Navy, shape = RoundedCornerShape(12.dp)) {
                    Box(Modifier.size(44.dp), contentAlignment = Alignment.Center) {
                        Icon(serviceIcon(key), contentDescription = null, tint = Gold, modifier = Modifier.size(24.dp))
                    }
                }
                Column(Modifier.weight(1f)) {
                    Text(copy.title, color = Ivory, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Start)
                    Text(copy.badge, color = Gold, style = MaterialTheme.typography.labelSmall)
                }
            }
            Text(copy.subtitle, color = Muted, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Start)
        }
    }
}

private fun serviceIcon(key: String): ImageVector = when (key) {
    "projects" -> Icons.Outlined.Folder
    "ai" -> Icons.Outlined.Star
    "assets" -> Icons.Outlined.Search
    "exports" -> Icons.Outlined.Share
    "ar" -> Icons.Outlined.Public
    "library" -> Icons.Outlined.Info
    else -> Icons.Outlined.Build
}

@Composable
private fun FeatureScreen(
    key: String,
    padding: PaddingValues,
    language: AppLanguage,
    ui: UiCopy,
    onBack: () -> Unit,
) {
    val copy = StudioLocalization.services(language).getValue(key)
    PageFrame(padding = padding, testTag = "screen-$key") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(Modifier.weight(1f)) {
                Text(copy.title, color = Gold, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(copy.badge, color = Muted, style = MaterialTheme.typography.labelSmall)
            }
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.testTag("back-button"),
            ) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = ui.back)
                Text(" ${ui.back}")
            }
        }
        Spacer(Modifier.height(18.dp))
        Text(copy.subtitle, color = Ivory)
        Spacer(Modifier.height(12.dp))
        if (key == "ai") {
            ArchitecturalLocks(language = language, ui = ui)
        } else {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Panel,
                shape = RoundedCornerShape(18.dp),
            ) {
                Text(ui.featureStatus, color = Muted, modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
private fun ArchitecturalLocks(language: AppLanguage, ui: UiCopy) {
    val initial = listOf(true, true, true, false, true, true)
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(ui.lockIntro, color = Muted)
        StudioLocalization.lockLabels(language).forEachIndexed { index, label ->
            var checked by remember(label) { mutableStateOf(initial[index]) }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Panel, RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                    .testTag("lock-$index"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(label, color = Ivory, modifier = Modifier.weight(1f))
                Switch(checked = checked, onCheckedChange = { checked = it })
            }
        }
    }
}

@Composable
private fun AboutScreen(padding: PaddingValues, ui: UiCopy) {
    val uriHandler = LocalUriHandler.current
    PageFrame(padding = padding, testTag = "screen-about") {
        Text(ui.aboutTitle, color = Gold, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(ui.aboutBody, color = Muted, textAlign = TextAlign.Start)
        Spacer(Modifier.height(18.dp))
        Text(ui.contactTitle, color = Ivory, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(10.dp))
        ExternalLinkButton(ui.website, NexvaryLinks.WEBSITE, "social-website", Icons.Outlined.Public, uriHandler::openUri)
        ExternalLinkButton(ui.facebook, NexvaryLinks.FACEBOOK, "social-facebook", Icons.Outlined.Share, uriHandler::openUri)
        ExternalLinkButton(ui.email, NexvaryLinks.EMAIL, "social-email", Icons.Outlined.Email, uriHandler::openUri)
        ExternalLinkButton(ui.youtube, NexvaryLinks.YOUTUBE, "social-youtube", Icons.Outlined.Star, uriHandler::openUri)
        ExternalLinkButton(ui.x, NexvaryLinks.X, "social-x", Icons.Outlined.Share, uriHandler::openUri)
    }
}

@Composable
private fun ExternalLinkButton(
    label: String,
    uri: String,
    testTag: String,
    icon: ImageVector,
    openUri: (String) -> Unit,
) {
    OutlinedButton(
        onClick = { openUri(uri) },
        modifier = Modifier.fillMaxWidth().testTag(testTag),
    ) {
        Icon(icon, contentDescription = null)
        Text(" $label")
    }
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun PageFrame(
    padding: PaddingValues,
    testTag: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Navy)
            .padding(padding)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 760.dp)
                .verticalScroll(rememberScrollState())
                .testTag(testTag)
                .padding(vertical = 16.dp),
            content = content,
        )
    }
}
