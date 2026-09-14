package com.nexvary.andalus

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

private const val V2_SETTINGS_ROUTE = "settings"
private const val PREFS_NAME = "andalus_ui"
private const val PREF_PALETTE = "palette"

private data class AndalusPalette(
    val id: String,
    val label: String,
    val roseMist: Color,
    val rose: Color,
    val accent: Color,
    val gold: Color,
    val darkGold: Color,
    val royal: Color,
    val royalDark: Color,
    val cream: Color,
    val ink: Color,
    val softInk: Color,
)

private val RoseRoyal = AndalusPalette(
    id = "rose",
    label = "Rose Royal",
    roseMist = Color(0xFFFFF1F3),
    rose = Color(0xFFF1BCC4),
    accent = Color(0xFFB95F73),
    gold = Color(0xFFC89535),
    darkGold = Color(0xFF8C6420),
    royal = Color(0xFF213A78),
    royalDark = Color(0xFF172451),
    cream = Color(0xFFFFFBF5),
    ink = Color(0xFF2D2430),
    softInk = Color(0xFF6D5A64),
)

private val SapphireRoyal = AndalusPalette(
    id = "sapphire",
    label = "Sapphire Gold",
    roseMist = Color(0xFFF4F1F7),
    rose = Color(0xFFD8C7E6),
    accent = Color(0xFF745A91),
    gold = Color(0xFFD2A13A),
    darkGold = Color(0xFF8D671D),
    royal = Color(0xFF173F88),
    royalDark = Color(0xFF0C285F),
    cream = Color(0xFFFFFCF7),
    ink = Color(0xFF211F2E),
    softInk = Color(0xFF625B70),
)

private val EmeraldRoyal = AndalusPalette(
    id = "emerald",
    label = "Emerald Andalus",
    roseMist = Color(0xFFF3F8F5),
    rose = Color(0xFFCEE6D9),
    accent = Color(0xFF4E8A70),
    gold = Color(0xFFC99A34),
    darkGold = Color(0xFF84631C),
    royal = Color(0xFF176A59),
    royalDark = Color(0xFF0E453B),
    cream = Color(0xFFFFFCF5),
    ink = Color(0xFF20302B),
    softInk = Color(0xFF5A6A64),
)

private val Palettes = listOf(RoseRoyal, SapphireRoyal, EmeraldRoyal)
private val LocalAndalusPalette = staticCompositionLocalOf { RoseRoyal }

private val V2AndalusArch = GenericShape { size, _ ->
    moveTo(0f, size.height)
    lineTo(0f, size.height * 0.36f)
    quadraticBezierTo(size.width * 0.08f, 0f, size.width * 0.50f, 0f)
    quadraticBezierTo(size.width * 0.92f, 0f, size.width, size.height * 0.36f)
    lineTo(size.width, size.height)
    close()
}

@Composable
fun AndalusStudioAppV2() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }
    var paletteId by rememberSaveable { mutableStateOf(prefs.getString(PREF_PALETTE, RoseRoyal.id) ?: RoseRoyal.id) }
    val palette = Palettes.firstOrNull { it.id == paletteId } ?: RoseRoyal

    CompositionLocalProvider(LocalAndalusPalette provides palette) {
        MaterialTheme(
            colorScheme = lightColorScheme(
                primary = palette.gold,
                onPrimary = palette.royalDark,
                secondary = palette.royal,
                onSecondary = palette.cream,
                background = palette.roseMist,
                onBackground = palette.ink,
                surface = palette.cream,
                onSurface = palette.ink,
                surfaceVariant = palette.rose.copy(alpha = 0.38f),
                outline = palette.gold,
            ),
        ) {
            AndalusStudioContent(
                paletteId = palette.id,
                onPaletteChanged = { selected ->
                    paletteId = selected
                    prefs.edit().putString(PREF_PALETTE, selected).apply()
                },
            )
        }
    }
}

@Composable
private fun AndalusStudioContent(
    paletteId: String,
    onPaletteChanged: (String) -> Unit,
) {
    var destination by rememberSaveable { mutableStateOf(StudioRoutes.HOME) }
    var languageCode by rememberSaveable { mutableStateOf(AppLanguage.AR.code) }
    val language = AppLanguage.entries.firstOrNull { it.code == languageCode } ?: AppLanguage.AR
    val ui = StudioLocalization.ui(language)
    val layoutDirection = if (language.rtl) LayoutDirection.Rtl else LayoutDirection.Ltr
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val isFeature = destination in StudioRoutes.serviceIds

    BackHandler(enabled = destination != StudioRoutes.HOME) {
        destination = if (isFeature) StudioRoutes.SERVICES else StudioRoutes.HOME
    }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Box(Modifier.fillMaxSize().testTag("palette-$paletteId")) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    V2Drawer(destination, ui) {
                        destination = it
                        scope.launch { drawerState.close() }
                    }
                },
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.Transparent,
                    topBar = {
                        V2TopBar(
                            language = language,
                            ui = ui,
                            onMenu = { scope.launch { drawerState.open() } },
                            onLanguageChanged = { languageCode = it.code },
                        )
                    },
                    bottomBar = {
                        if (!isFeature && destination != V2_SETTINGS_ROUTE) {
                            V2PrimaryNavigation(destination, ui) { destination = it }
                        }
                    },
                ) { padding ->
                    when (destination) {
                        StudioRoutes.HOME -> V2HomeScreen(
                            padding = padding,
                            ui = ui,
                            language = language,
                            onStart = { destination = StudioRoutes.SERVICES },
                            onOpen = { destination = it },
                        )
                        StudioRoutes.SERVICES -> V2ServicesScreen(padding, language, ui) { destination = it }
                        StudioRoutes.ABOUT -> V2AboutScreen(padding, ui)
                        V2_SETTINGS_ROUTE -> V2SettingsScreen(
                            padding = padding,
                            language = language,
                            ui = ui,
                            paletteId = paletteId,
                            onPaletteChanged = onPaletteChanged,
                            onLanguageChanged = { languageCode = it.code },
                            onBack = { destination = StudioRoutes.HOME },
                        )
                        else -> V2FeatureScreen(
                            key = destination,
                            padding = padding,
                            language = language,
                            ui = ui,
                            onBack = { destination = StudioRoutes.SERVICES },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun V2Backdrop(content: @Composable () -> Unit) {
    val c = LocalAndalusPalette.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(c.roseMist, c.rose.copy(alpha = 0.72f), c.roseMist))),
    ) {
        Text("✦", color = c.gold.copy(alpha = 0.18f), style = MaterialTheme.typography.displayLarge, modifier = Modifier.align(Alignment.TopStart).padding(22.dp))
        Text("❈", color = c.royal.copy(alpha = 0.11f), style = MaterialTheme.typography.displayLarge, modifier = Modifier.align(Alignment.CenterEnd).padding(18.dp))
        Text("✦", color = c.gold.copy(alpha = 0.16f), style = MaterialTheme.typography.displayLarge, modifier = Modifier.align(Alignment.BottomStart).padding(26.dp))
        content()
    }
}

@Composable
private fun V2TopBar(
    language: AppLanguage,
    ui: UiCopy,
    onMenu: () -> Unit,
    onLanguageChanged: (AppLanguage) -> Unit,
) {
    val c = LocalAndalusPalette.current
    var expanded by remember { mutableStateOf(false) }
    Surface(color = c.cream.copy(alpha = 0.98f), shadowElevation = 6.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().safeDrawingPadding().padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(onClick = onMenu, modifier = Modifier.testTag("menu-button")) {
                Icon(Icons.Outlined.Menu, contentDescription = "Menu", tint = c.royal)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("NEXVARY", color = c.gold, fontWeight = FontWeight.Black, style = MaterialTheme.typography.labelLarge)
                Text("Andalus Studio", color = c.royalDark, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelMedium)
            }
            Box {
                TextButton(onClick = { expanded = true }, modifier = Modifier.testTag("language-picker")) {
                    Icon(Icons.Outlined.Language, contentDescription = ui.language, tint = c.gold)
                    Text(" ${language.nativeName}", color = c.accent)
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
private fun V2Drawer(destination: String, ui: UiCopy, onNavigate: (String) -> Unit) {
    val c = LocalAndalusPalette.current
    ModalDrawerSheet(drawerContainerColor = c.cream, modifier = Modifier.widthIn(max = 330.dp)) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("✦ NEXVARY ✦", color = c.gold, fontWeight = FontWeight.Black)
            Text("Andalus Studio", color = c.royalDark, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            V2OrnamentStrip()
            Spacer(Modifier.height(6.dp))
            V2DrawerItem(ui.home, destination == StudioRoutes.HOME, "drawer-home", Icons.Outlined.Home) { onNavigate(StudioRoutes.HOME) }
            V2DrawerItem(ui.services, destination == StudioRoutes.SERVICES, "drawer-services", Icons.Outlined.Build) { onNavigate(StudioRoutes.SERVICES) }
            V2DrawerItem(ui.about, destination == StudioRoutes.ABOUT, "drawer-about", Icons.Outlined.Info) { onNavigate(StudioRoutes.ABOUT) }
            V2DrawerItem(v2SettingsLabel(), destination == V2_SETTINGS_ROUTE, "drawer-settings", Icons.Outlined.Settings) { onNavigate(V2_SETTINGS_ROUTE) }
            HorizontalDivider(color = c.gold.copy(alpha = 0.35f), modifier = Modifier.padding(vertical = 8.dp))
            Text("Andalus Royal UI", color = c.softInk, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun V2DrawerItem(label: String, selected: Boolean, tag: String, icon: ImageVector, onClick: () -> Unit) {
    NavigationDrawerItem(
        label = { Text(label) },
        selected = selected,
        onClick = onClick,
        icon = { Icon(icon, contentDescription = null) },
        modifier = Modifier.testTag(tag),
    )
}

@Composable
private fun V2PrimaryNavigation(destination: String, ui: UiCopy, onNavigate: (String) -> Unit) {
    val c = LocalAndalusPalette.current
    NavigationBar(containerColor = c.cream, tonalElevation = 10.dp) {
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
private fun V2HomeScreen(
    padding: PaddingValues,
    ui: UiCopy,
    language: AppLanguage,
    onStart: () -> Unit,
    onOpen: (String) -> Unit,
) {
    val c = LocalAndalusPalette.current
    V2Backdrop {
        Column(
            modifier = Modifier.fillMaxSize().testTag("screen-home").padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(modifier = Modifier.fillMaxWidth().widthIn(max = 760.dp)) {
                V2Hero(ui)
                Spacer(Modifier.height(16.dp))
                V2SectionTitle(v2HomeSectionLabel(language), ui.homeIntro)
                Spacer(Modifier.height(10.dp))
                V2QuickActions(language, onOpen)
                Spacer(Modifier.height(14.dp))
                Button(
                    onClick = onStart,
                    modifier = Modifier.fillMaxWidth().testTag("home-start-services"),
                    colors = ButtonDefaults.buttonColors(containerColor = c.gold, contentColor = c.royalDark),
                    shape = RoundedCornerShape(18.dp),
                    contentPadding = PaddingValues(vertical = 15.dp),
                ) { Text(ui.startDesigning, fontWeight = FontWeight.Black) }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun V2Hero(ui: UiCopy) {
    val c = LocalAndalusPalette.current
    Surface(
        modifier = Modifier.fillMaxWidth().height(248.dp).border(2.dp, c.gold, V2AndalusArch),
        shape = V2AndalusArch,
        color = c.royal,
        shadowElevation = 10.dp,
    ) {
        Box(Modifier.background(Brush.verticalGradient(listOf(c.royal, c.royalDark)))) {
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text("✦  ❈  ✦", color = c.gold, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text("Nexvary Andalus Studio", color = c.cream, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                Spacer(Modifier.height(8.dp))
                Text(ui.tagline, color = c.rose, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                Spacer(Modifier.height(12.dp))
                Surface(color = c.gold, shape = RoundedCornerShape(50)) {
                    Text("ANDALUS ROYAL", color = c.royalDark, fontWeight = FontWeight.Black, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp))
                }
            }
        }
    }
}

@Composable
private fun V2SectionTitle(title: String, subtitle: String) {
    val c = LocalAndalusPalette.current
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("✦", color = c.gold, style = MaterialTheme.typography.titleLarge)
            Text(title, color = c.royalDark, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
        }
        Text(subtitle, color = c.softInk, textAlign = TextAlign.Start)
    }
}

@Composable
private fun V2QuickActions(language: AppLanguage, onOpen: (String) -> Unit) {
    val services = StudioLocalization.services(language)
    val keys = listOf("projects", "plan", "3d", "patterns")
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        if (maxWidth < 400.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                keys.forEach { key -> V2QuickCard(key, services.getValue(key), Modifier.fillMaxWidth()) { onOpen(key) } }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                keys.chunked(2).forEach { pair ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        pair.forEach { key -> V2QuickCard(key, services.getValue(key), Modifier.weight(1f)) { onOpen(key) } }
                    }
                }
            }
        }
    }
}

@Composable
private fun V2QuickCard(key: String, copy: ServiceCopy, modifier: Modifier, onClick: () -> Unit) {
    val c = LocalAndalusPalette.current
    Card(
        onClick = onClick,
        modifier = modifier.testTag("quick-$key").border(1.dp, c.gold.copy(alpha = 0.85f), RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = c.cream.copy(alpha = 0.97f)),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Surface(color = c.rose.copy(alpha = 0.55f), shape = RoundedCornerShape(14.dp)) {
                Box(Modifier.size(42.dp), contentAlignment = Alignment.Center) { Icon(v2ServiceIcon(key), contentDescription = null, tint = c.royal) }
            }
            Text(copy.title, color = c.royalDark, fontWeight = FontWeight.Bold, maxLines = 2)
            Text(copy.badge, color = c.darkGold, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun V2ServicesScreen(padding: PaddingValues, language: AppLanguage, ui: UiCopy, onOpen: (String) -> Unit) {
    val c = LocalAndalusPalette.current
    val services = StudioLocalization.services(language)
    V2Backdrop {
        LazyColumn(
            modifier = Modifier.fillMaxSize().testTag("screen-services").padding(padding).padding(horizontal = 14.dp),
            contentPadding = PaddingValues(vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Column(Modifier.fillMaxWidth().widthIn(max = 760.dp)) {
                    Text("✦ ${ui.services} ✦", color = c.royalDark, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                    Text(ui.tagline, color = c.softInk, style = MaterialTheme.typography.bodySmall)
                    V2OrnamentStrip()
                }
            }
            items(StudioRoutes.serviceIds, key = { it }) { key -> V2ServiceCard(key, services.getValue(key)) { onOpen(key) } }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun V2ServiceCard(key: String, copy: ServiceCopy, onClick: () -> Unit) {
    val c = LocalAndalusPalette.current
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().widthIn(max = 760.dp).testTag("service-$key").border(1.dp, c.gold.copy(alpha = 0.72f), RoundedCornerShape(22.dp)),
        colors = CardDefaults.cardColors(containerColor = c.cream.copy(alpha = 0.98f)),
        shape = RoundedCornerShape(22.dp),
    ) {
        Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(13.dp)) {
            Surface(color = c.royal, shape = RoundedCornerShape(16.dp)) {
                Box(Modifier.size(52.dp), contentAlignment = Alignment.Center) { Icon(v2ServiceIcon(key), contentDescription = null, tint = c.gold, modifier = Modifier.size(27.dp)) }
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(copy.title, color = c.royalDark, fontWeight = FontWeight.Black)
                Text(copy.subtitle, color = c.softInk, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                Text(copy.badge, color = c.accent, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun v2ServiceIcon(key: String): ImageVector = when (key) {
    "projects" -> Icons.Outlined.Folder
    "ai" -> Icons.Outlined.Star
    "assets" -> Icons.Outlined.Search
    "exports" -> Icons.Outlined.Share
    "ar" -> Icons.Outlined.Public
    "library" -> Icons.Outlined.Info
    else -> Icons.Outlined.Build
}

@Composable
private fun V2FeatureScreen(key: String, padding: PaddingValues, language: AppLanguage, ui: UiCopy, onBack: () -> Unit) {
    val c = LocalAndalusPalette.current
    val copy = StudioLocalization.services(language).getValue(key)
    V2PageFrame(padding, "screen-$key") {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(copy.title, color = c.royalDark, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                Text(copy.badge, color = c.accent, style = MaterialTheme.typography.labelMedium)
            }
            OutlinedButton(onClick = onBack, modifier = Modifier.testTag("back-button")) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = ui.back, tint = c.royal)
                Text(" ${ui.back}", color = c.royal)
            }
        }
        V2OrnamentStrip()
        Text(copy.subtitle, color = c.ink)
        Spacer(Modifier.height(6.dp))
        if (key == "ai") {
            V2ArchitecturalLocks(language, ui)
        } else {
            V2FeatureWorkspace(key, copy)
        }
    }
}

@Composable
private fun V2FeatureWorkspace(key: String, copy: ServiceCopy) {
    val c = LocalAndalusPalette.current
    val tokens = when (key) {
        "projects" -> listOf("PROJECTS", "VERSIONS", "ARCHIVE")
        "plan" -> listOf("2D", "GRID", "LAYERS")
        "3d" -> listOf("3D", "SCENE", "EXPORT")
        "patterns" -> listOf("PATTERN", "REPEAT", "LIBRARY")
        "assets" -> listOf("ASSETS", "SEARCH", "TAGS")
        "materials" -> listOf("BOM", "MATERIALS", "COST")
        "exports" -> listOf("PDF", "IMAGE", "PROJECT")
        "ar" -> listOf("AR", "SCALE", "ANCHOR")
        "library" -> listOf("LIBRARY", "FAVORITES", "TEMPLATES")
        else -> listOf(copy.badge)
    }
    Card(
        colors = CardDefaults.cardColors(containerColor = c.cream.copy(alpha = 0.97f)),
        shape = RoundedCornerShape(22.dp),
        modifier = Modifier.fillMaxWidth().border(1.dp, c.gold.copy(alpha = 0.62f), RoundedCornerShape(22.dp)).testTag("workspace-$key"),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(v2ServiceIcon(key), contentDescription = null, tint = c.royal)
                Text(copy.title, color = c.royalDark, fontWeight = FontWeight.Black)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                tokens.forEach { token ->
                    Surface(color = c.rose.copy(alpha = 0.46f), shape = RoundedCornerShape(50), border = androidx.compose.foundation.BorderStroke(1.dp, c.gold.copy(alpha = 0.55f))) {
                        Text(token, color = c.royalDark, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun V2ArchitecturalLocks(language: AppLanguage, ui: UiCopy) {
    val c = LocalAndalusPalette.current
    val initial = listOf(true, true, true, false, true, true)
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(ui.lockIntro, color = c.softInk)
        StudioLocalization.lockLabels(language).forEachIndexed { index, label ->
            var checked by remember(label) { mutableStateOf(initial[index]) }
            Row(
                modifier = Modifier.fillMaxWidth().background(c.cream, RoundedCornerShape(18.dp)).border(1.dp, c.gold.copy(alpha = 0.45f), RoundedCornerShape(18.dp)).padding(horizontal = 14.dp, vertical = 10.dp).testTag("lock-$index"),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(label, color = c.ink, modifier = Modifier.weight(1f))
                Switch(checked = checked, onCheckedChange = { checked = it })
            }
        }
    }
}

@Composable
private fun V2AboutScreen(padding: PaddingValues, ui: UiCopy) {
    val c = LocalAndalusPalette.current
    val uriHandler = LocalUriHandler.current
    V2PageFrame(padding, "screen-about") {
        Text("✦ ${ui.aboutTitle} ✦", color = c.royalDark, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
        V2OrnamentStrip()
        Card(
            colors = CardDefaults.cardColors(containerColor = c.cream.copy(alpha = 0.98f)),
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier.fillMaxWidth().border(1.dp, c.gold.copy(alpha = 0.72f), RoundedCornerShape(22.dp)),
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(ui.aboutBody, color = c.ink, textAlign = TextAlign.Start)
                Text(ui.contactTitle, color = c.royal, fontWeight = FontWeight.Black)
            }
        }
        V2ExternalLink(ui.website, NexvaryLinks.WEBSITE, "social-website", Icons.Outlined.Public, uriHandler::openUri)
        V2ExternalLink(ui.facebook, NexvaryLinks.FACEBOOK, "social-facebook", Icons.Outlined.Share, uriHandler::openUri)
        V2ExternalLink(ui.email, NexvaryLinks.EMAIL, "social-email", Icons.Outlined.Email, uriHandler::openUri)
        V2ExternalLink(ui.youtube, NexvaryLinks.YOUTUBE, "social-youtube", Icons.Outlined.Star, uriHandler::openUri)
        V2ExternalLink(ui.x, NexvaryLinks.X, "social-x", Icons.Outlined.Share, uriHandler::openUri)
        Spacer(Modifier.height(18.dp))
    }
}

@Composable
private fun V2ExternalLink(label: String, uri: String, tag: String, icon: ImageVector, open: (String) -> Unit) {
    val c = LocalAndalusPalette.current
    OutlinedButton(
        onClick = { open(uri) },
        modifier = Modifier.fillMaxWidth().testTag(tag),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = c.royal),
    ) {
        Icon(icon, contentDescription = null, tint = c.gold)
        Text("  $label", fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun V2SettingsScreen(
    padding: PaddingValues,
    language: AppLanguage,
    ui: UiCopy,
    paletteId: String,
    onPaletteChanged: (String) -> Unit,
    onLanguageChanged: (AppLanguage) -> Unit,
    onBack: () -> Unit,
) {
    val c = LocalAndalusPalette.current
    V2PageFrame(padding, "screen-settings") {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(v2SettingsLabel(), color = c.royalDark, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f))
            OutlinedButton(onClick = onBack, modifier = Modifier.testTag("settings-back")) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = ui.back)
                Text(" ${ui.back}")
            }
        }
        V2OrnamentStrip()
        V2SettingsCard(v2LanguageSettingsLabel(language)) {
            Text(ui.language, color = c.softInk)
            AppLanguage.entries.forEach { item ->
                TextButton(onClick = { onLanguageChanged(item) }, modifier = Modifier.fillMaxWidth().testTag("settings-lang-${item.code}")) {
                    Text(
                        if (item == language) "✦ ${item.nativeName}" else item.nativeName,
                        color = if (item == language) c.royal else c.ink,
                        fontWeight = if (item == language) FontWeight.Black else FontWeight.Normal,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start,
                    )
                }
            }
        }
        V2SettingsCard(v2ThemeSettingsLabel(language)) {
            val active = Palettes.firstOrNull { it.id == paletteId } ?: RoseRoyal
            Text(active.label, color = c.royalDark, fontWeight = FontWeight.Black, modifier = Modifier.testTag("theme-current"))
            Text(v2ThemeHint(language), color = c.softInk, style = MaterialTheme.typography.bodySmall)
            Palettes.forEach { option ->
                V2PaletteChoice(option, selected = option.id == paletteId) { onPaletteChanged(option.id) }
            }
        }
        V2SettingsCard(v2SecuritySettingsLabel(language)) {
            Text(ui.secureImports, color = c.ink)
            Text("HTTPS • Secure imports • Local project state", color = c.softInk, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.height(18.dp))
    }
}

@Composable
private fun V2PaletteChoice(option: AndalusPalette, selected: Boolean, onClick: () -> Unit) {
    val c = LocalAndalusPalette.current
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().testTag("theme-${option.id}").border(if (selected) 2.dp else 1.dp, if (selected) c.royal else c.gold.copy(alpha = 0.50f), RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = c.cream),
        shape = RoundedCornerShape(18.dp),
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                V2PaletteDot(option.rose)
                V2PaletteDot(option.gold)
                V2PaletteDot(option.royal)
            }
            Text(option.label, color = c.ink, fontWeight = if (selected) FontWeight.Black else FontWeight.SemiBold, modifier = Modifier.weight(1f))
            if (selected) Text("✓", color = c.royal, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun V2PaletteDot(color: Color) {
    Box(Modifier.size(26.dp).background(color, RoundedCornerShape(50)).border(1.dp, Color.Black.copy(alpha = 0.16f), RoundedCornerShape(50)))
}

@Composable
private fun V2SettingsCard(title: String, content: @Composable () -> Unit) {
    val c = LocalAndalusPalette.current
    Card(
        colors = CardDefaults.cardColors(containerColor = c.cream.copy(alpha = 0.98f)),
        shape = RoundedCornerShape(22.dp),
        modifier = Modifier.fillMaxWidth().border(1.dp, c.gold.copy(alpha = 0.60f), RoundedCornerShape(22.dp)),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, color = c.accent, fontWeight = FontWeight.Black)
            content()
        }
    }
}

@Composable
private fun V2PageFrame(padding: PaddingValues, tag: String, content: @Composable () -> Unit) {
    V2Backdrop {
        Column(
            modifier = Modifier.fillMaxSize().testTag(tag).padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(modifier = Modifier.fillMaxWidth().widthIn(max = 760.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) { content() }
        }
    }
}

@Composable
private fun V2OrnamentStrip() {
    val c = LocalAndalusPalette.current
    Text("✦  ❈  ✦  ❈  ✦", color = c.gold, modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.titleMedium)
}

@Composable
private fun v2SettingsLabel(): String = if (LocalLayoutDirection.current == LayoutDirection.Rtl) "الإعدادات" else "Settings"

private fun v2HomeSectionLabel(language: AppLanguage): String = when (language) {
    AppLanguage.AR -> "ابدأ من هنا"
    AppLanguage.UR -> "یہاں سے شروع کریں"
    AppLanguage.FA -> "از اینجا شروع کنید"
    AppLanguage.TR -> "Buradan başla"
    AppLanguage.ES -> "Empieza aquí"
    AppLanguage.DE -> "Hier starten"
    AppLanguage.IT -> "Inizia qui"
    AppLanguage.FR -> "Commencer ici"
    AppLanguage.RU -> "Начните здесь"
    AppLanguage.EN -> "Start here"
}

private fun v2LanguageSettingsLabel(language: AppLanguage): String = when (language) {
    AppLanguage.AR -> "اللغة والاتجاه"
    AppLanguage.UR -> "زبان اور سمت"
    AppLanguage.FA -> "زبان و جهت"
    AppLanguage.TR -> "Dil ve yön"
    AppLanguage.ES -> "Idioma y dirección"
    AppLanguage.DE -> "Sprache und Richtung"
    AppLanguage.IT -> "Lingua e direzione"
    AppLanguage.FR -> "Langue et direction"
    AppLanguage.RU -> "Язык и направление"
    AppLanguage.EN -> "Language & direction"
}

private fun v2ThemeSettingsLabel(language: AppLanguage): String = when (language) {
    AppLanguage.AR -> "الهوية الأندلسية"
    AppLanguage.UR -> "اندلسی شناخت"
    AppLanguage.FA -> "هویت اندلسی"
    AppLanguage.TR -> "Endülüs kimliği"
    AppLanguage.ES -> "Identidad andalusí"
    AppLanguage.DE -> "Andalusische Identität"
    AppLanguage.IT -> "Identità andalusa"
    AppLanguage.FR -> "Identité andalouse"
    AppLanguage.RU -> "Андалусская айдентика"
    AppLanguage.EN -> "Andalus identity"
}

private fun v2ThemeHint(language: AppLanguage): String = when (language) {
    AppLanguage.AR -> "اختر الهوية اللونية؛ يطبق التغيير فورًا على كامل التطبيق ويُحفظ تلقائيًا."
    AppLanguage.UR -> "رنگوں کی شناخت منتخب کریں؛ تبدیلی پوری ایپ پر فوراً لاگو اور محفوظ ہوگی۔"
    AppLanguage.FA -> "هویت رنگی را انتخاب کنید؛ تغییر بلافاصله روی کل برنامه اعمال و ذخیره می‌شود."
    AppLanguage.TR -> "Renk kimliğini seçin; değişiklik tüm uygulamaya anında uygulanır ve kaydedilir."
    AppLanguage.ES -> "Elige la identidad de color; el cambio se aplica al instante y se guarda."
    AppLanguage.DE -> "Wähle die Farbidentität; die Änderung wird sofort angewendet und gespeichert."
    AppLanguage.IT -> "Scegli l'identità colore; la modifica viene applicata subito e salvata."
    AppLanguage.FR -> "Choisissez l’identité couleur; le changement s’applique immédiatement et est enregistré."
    AppLanguage.RU -> "Выберите цветовую тему; изменение применяется сразу и сохраняется."
    AppLanguage.EN -> "Choose a color identity; it applies app-wide immediately and is saved automatically."
}

private fun v2SecuritySettingsLabel(language: AppLanguage): String = when (language) {
    AppLanguage.AR -> "الأمان والخصوصية"
    AppLanguage.UR -> "سیکیورٹی اور پرائیویسی"
    AppLanguage.FA -> "امنیت و حریم خصوصی"
    AppLanguage.TR -> "Güvenlik ve gizlilik"
    AppLanguage.ES -> "Seguridad y privacidad"
    AppLanguage.DE -> "Sicherheit und Datenschutz"
    AppLanguage.IT -> "Sicurezza e privacy"
    AppLanguage.FR -> "Sécurité et confidentialité"
    AppLanguage.RU -> "Безопасность и конфиденциальность"
    AppLanguage.EN -> "Security & privacy"
}
