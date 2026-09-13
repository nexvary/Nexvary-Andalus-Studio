package com.nexvary.andalus

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
import androidx.compose.material.icons.outlined.ArrowBack
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

private val RoseMist = Color(0xFFFFF1F3)
private val RoyalRose = Color(0xFFF1BCC4)
private val DeepRose = Color(0xFFB95F73)
private val RoyalGold = Color(0xFFC89535)
private val DarkGold = Color(0xFF8C6420)
private val RoyalBlue = Color(0xFF213A78)
private val MidnightBlue = Color(0xFF172451)
private val Plum = Color(0xFF54234D)
private val Cream = Color(0xFFFFFBF5)
private val Ink = Color(0xFF2D2430)
private val SoftInk = Color(0xFF6D5A64)
private const val SETTINGS_ROUTE = "settings"

private val AndalusArchShape = GenericShape { size, _ ->
    moveTo(0f, size.height)
    lineTo(0f, size.height * 0.36f)
    quadraticBezierTo(size.width * 0.08f, 0f, size.width * 0.50f, 0f)
    quadraticBezierTo(size.width * 0.92f, 0f, size.width, size.height * 0.36f)
    lineTo(size.width, size.height)
    close()
}

@Composable
fun RoyalAndalusTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = RoyalGold,
            onPrimary = MidnightBlue,
            secondary = RoyalBlue,
            onSecondary = Cream,
            background = RoseMist,
            onBackground = Ink,
            surface = Cream,
            onSurface = Ink,
            surfaceVariant = RoyalRose.copy(alpha = 0.36f),
            outline = RoyalGold,
        ),
        content = content,
    )
}

@Composable
fun RoyalStudioApp() {
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
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                RoyalDrawer(
                    destination = destination,
                    ui = ui,
                    onNavigate = {
                        destination = it
                        scope.launch { drawerState.close() }
                    },
                )
            },
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    RoyalTopBar(
                        language = language,
                        ui = ui,
                        onMenu = { scope.launch { drawerState.open() } },
                        onLanguageChanged = { languageCode = it.code },
                    )
                },
                bottomBar = {
                    if (!isFeature && destination != SETTINGS_ROUTE) {
                        RoyalPrimaryNavigation(
                            destination = destination,
                            ui = ui,
                            onNavigate = { destination = it },
                        )
                    }
                },
            ) { innerPadding ->
                when (destination) {
                    StudioRoutes.HOME -> RoyalHomeScreen(
                        padding = innerPadding,
                        ui = ui,
                        language = language,
                        onStart = { destination = StudioRoutes.SERVICES },
                        onOpen = { destination = it },
                    )
                    StudioRoutes.SERVICES -> RoyalServicesScreen(
                        padding = innerPadding,
                        language = language,
                        ui = ui,
                        onOpen = { destination = it },
                    )
                    StudioRoutes.ABOUT -> RoyalAboutScreen(padding = innerPadding, ui = ui)
                    SETTINGS_ROUTE -> RoyalSettingsScreen(
                        padding = innerPadding,
                        language = language,
                        ui = ui,
                        onLanguageChanged = { languageCode = it.code },
                        onBack = { destination = StudioRoutes.HOME },
                    )
                    else -> RoyalFeatureScreen(
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
}

@Composable
private fun RoyalBackdrop(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        RoseMist,
                        RoyalRose.copy(alpha = 0.55f),
                        RoseMist,
                    ),
                ),
            ),
    ) {
        Text(
            "✦",
            color = RoyalGold.copy(alpha = 0.17f),
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.align(Alignment.TopStart).padding(22.dp),
        )
        Text(
            "❈",
            color = RoyalBlue.copy(alpha = 0.10f),
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.align(Alignment.CenterEnd).padding(18.dp),
        )
        Text(
            "✦",
            color = RoyalGold.copy(alpha = 0.15f),
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.align(Alignment.BottomStart).padding(26.dp),
        )
        content()
    }
}

@Composable
private fun RoyalTopBar(
    language: AppLanguage,
    ui: UiCopy,
    onMenu: () -> Unit,
    onLanguageChanged: (AppLanguage) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Surface(color = Cream.copy(alpha = 0.97f), shadowElevation = 6.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .safeDrawingPadding()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(onClick = onMenu, modifier = Modifier.testTag("menu-button")) {
                Icon(Icons.Outlined.Menu, contentDescription = "Menu", tint = RoyalBlue)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "NEXVARY",
                    color = RoyalGold,
                    fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.labelLarge,
                )
                Text(
                    "Andalus Studio",
                    color = MidnightBlue,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            Box {
                TextButton(
                    onClick = { expanded = true },
                    modifier = Modifier.testTag("language-picker"),
                ) {
                    Icon(Icons.Outlined.Language, contentDescription = ui.language, tint = RoyalGold)
                    Text(" ${language.nativeName}", color = Plum)
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
private fun RoyalDrawer(destination: String, ui: UiCopy, onNavigate: (String) -> Unit) {
    ModalDrawerSheet(
        drawerContainerColor = Cream,
        modifier = Modifier.widthIn(max = 330.dp),
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("✦ NEXVARY ✦", color = RoyalGold, fontWeight = FontWeight.Black)
            Text("Andalus Studio", color = MidnightBlue, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            OrnamentStrip()
            Spacer(Modifier.height(6.dp))
            NavigationDrawerItem(
                label = { Text(ui.home) },
                selected = destination == StudioRoutes.HOME,
                onClick = { onNavigate(StudioRoutes.HOME) },
                icon = { Icon(Icons.Outlined.Home, contentDescription = null) },
                modifier = Modifier.testTag("drawer-home"),
            )
            NavigationDrawerItem(
                label = { Text(ui.services) },
                selected = destination == StudioRoutes.SERVICES,
                onClick = { onNavigate(StudioRoutes.SERVICES) },
                icon = { Icon(Icons.Outlined.Build, contentDescription = null) },
                modifier = Modifier.testTag("drawer-services"),
            )
            NavigationDrawerItem(
                label = { Text(ui.about) },
                selected = destination == StudioRoutes.ABOUT,
                onClick = { onNavigate(StudioRoutes.ABOUT) },
                icon = { Icon(Icons.Outlined.Info, contentDescription = null) },
                modifier = Modifier.testTag("drawer-about"),
            )
            NavigationDrawerItem(
                label = { Text(settingsLabel()) },
                selected = destination == SETTINGS_ROUTE,
                onClick = { onNavigate(SETTINGS_ROUTE) },
                icon = { Icon(Icons.Outlined.Settings, contentDescription = null) },
                modifier = Modifier.testTag("drawer-settings"),
            )
            HorizontalDivider(color = RoyalGold.copy(alpha = 0.35f), modifier = Modifier.padding(vertical = 8.dp))
            Text(
                "Andalus Royal UI • v0.8.25",
                color = SoftInk,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
private fun RoyalPrimaryNavigation(destination: String, ui: UiCopy, onNavigate: (String) -> Unit) {
    NavigationBar(containerColor = Cream, tonalElevation = 10.dp) {
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
private fun RoyalHomeScreen(
    padding: PaddingValues,
    ui: UiCopy,
    language: AppLanguage,
    onStart: () -> Unit,
    onOpen: (String) -> Unit,
) {
    RoyalBackdrop {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .testTag("screen-home")
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(modifier = Modifier.fillMaxWidth().widthIn(max = 760.dp)) {
                RoyalHero(ui = ui)
                Spacer(Modifier.height(16.dp))
                RoyalSectionTitle(title = homeSectionLabel(language), subtitle = ui.homeIntro)
                Spacer(Modifier.height(10.dp))
                RoyalQuickActions(language = language, onOpen = onOpen)
                Spacer(Modifier.height(14.dp))
                Button(
                    onClick = onStart,
                    modifier = Modifier.fillMaxWidth().testTag("home-start-services"),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalGold, contentColor = MidnightBlue),
                    shape = RoundedCornerShape(18.dp),
                    contentPadding = PaddingValues(vertical = 15.dp),
                ) {
                    Text(ui.startDesigning, fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.height(12.dp))
                RoyalTrustStrip(ui)
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun RoyalHero(ui: UiCopy) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(248.dp)
            .border(2.dp, RoyalGold, AndalusArchShape),
        shape = AndalusArchShape,
        color = RoyalBlue,
        shadowElevation = 10.dp,
    ) {
        Box(
            Modifier.background(
                Brush.verticalGradient(listOf(RoyalBlue, MidnightBlue)),
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text("✦  ❈  ✦", color = RoyalGold, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Nexvary Andalus Studio",
                    color = Cream,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    ui.tagline,
                    color = RoyalRose,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(12.dp))
                Surface(
                    color = RoyalGold,
                    shape = RoundedCornerShape(50),
                ) {
                    Text(
                        "ANDALUS ROYAL",
                        color = MidnightBlue,
                        fontWeight = FontWeight.Black,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun RoyalSectionTitle(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("✦", color = RoyalGold, style = MaterialTheme.typography.titleLarge)
            Text(title, color = MidnightBlue, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
        }
        Text(subtitle, color = SoftInk, textAlign = TextAlign.Start)
    }
}

@Composable
private fun RoyalQuickActions(language: AppLanguage, onOpen: (String) -> Unit) {
    val services = StudioLocalization.services(language)
    val keys = listOf("projects", "plan", "3d", "patterns")
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        if (maxWidth < 400.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                keys.forEach { key ->
                    RoyalQuickCard(key, services.getValue(key), Modifier.fillMaxWidth()) { onOpen(key) }
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                keys.chunked(2).forEach { pair ->
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        pair.forEach { key ->
                            RoyalQuickCard(key, services.getValue(key), Modifier.weight(1f)) { onOpen(key) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RoyalQuickCard(key: String, copy: ServiceCopy, modifier: Modifier, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = modifier
            .testTag("quick-$key")
            .border(1.dp, RoyalGold.copy(alpha = 0.85f), RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Cream.copy(alpha = 0.96f)),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            Surface(color = RoyalRose.copy(alpha = 0.50f), shape = RoundedCornerShape(14.dp)) {
                Box(Modifier.size(42.dp), contentAlignment = Alignment.Center) {
                    Icon(royalServiceIcon(key), contentDescription = null, tint = RoyalBlue)
                }
            }
            Text(copy.title, color = MidnightBlue, fontWeight = FontWeight.Bold, maxLines = 2)
            Text(copy.badge, color = DarkGold, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun RoyalTrustStrip(ui: UiCopy) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Plum.copy(alpha = 0.94f)),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            RoyalStatusLine(Icons.Outlined.Star, ui.releaseReady)
            RoyalStatusLine(Icons.Outlined.Search, ui.testedNavigation)
            RoyalStatusLine(Icons.Outlined.Lock, ui.secureImports)
        }
    }
}

@Composable
private fun RoyalStatusLine(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Icon(icon, contentDescription = null, tint = RoyalGold, modifier = Modifier.size(19.dp))
        Text(text, color = Cream, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun RoyalServicesScreen(
    padding: PaddingValues,
    language: AppLanguage,
    ui: UiCopy,
    onOpen: (String) -> Unit,
) {
    val services = StudioLocalization.services(language)
    RoyalBackdrop {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("screen-services")
                .padding(padding)
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Column(Modifier.fillMaxWidth().widthIn(max = 760.dp)) {
                    Text("✦ ${ui.services} ✦", color = MidnightBlue, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                    Text(ui.tagline, color = SoftInk, style = MaterialTheme.typography.bodySmall)
                    OrnamentStrip()
                }
            }
            items(StudioRoutes.serviceIds, key = { it }) { key ->
                RoyalServiceCard(key, services.getValue(key)) { onOpen(key) }
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun RoyalServiceCard(key: String, copy: ServiceCopy, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 760.dp)
            .testTag("service-$key")
            .border(1.dp, RoyalGold.copy(alpha = 0.70f), RoundedCornerShape(22.dp)),
        colors = CardDefaults.cardColors(containerColor = Cream.copy(alpha = 0.97f)),
        shape = RoundedCornerShape(22.dp),
    ) {
        Row(
            modifier = Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(13.dp),
        ) {
            Surface(color = RoyalBlue, shape = RoundedCornerShape(16.dp)) {
                Box(Modifier.size(52.dp), contentAlignment = Alignment.Center) {
                    Icon(royalServiceIcon(key), contentDescription = null, tint = RoyalGold, modifier = Modifier.size(27.dp))
                }
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(copy.title, color = MidnightBlue, fontWeight = FontWeight.Black)
                Text(copy.subtitle, color = SoftInk, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                Text(copy.badge, color = DeepRose, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun royalServiceIcon(key: String): ImageVector = when (key) {
    "projects" -> Icons.Outlined.Folder
    "ai" -> Icons.Outlined.Star
    "assets" -> Icons.Outlined.Search
    "exports" -> Icons.Outlined.Share
    "ar" -> Icons.Outlined.Public
    "library" -> Icons.Outlined.Info
    else -> Icons.Outlined.Build
}

@Composable
private fun RoyalFeatureScreen(
    key: String,
    padding: PaddingValues,
    language: AppLanguage,
    ui: UiCopy,
    onBack: () -> Unit,
) {
    val copy = StudioLocalization.services(language).getValue(key)
    RoyalPageFrame(padding = padding, testTag = "screen-$key") {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(copy.title, color = MidnightBlue, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                Text(copy.badge, color = DeepRose, style = MaterialTheme.typography.labelMedium)
            }
            OutlinedButton(onClick = onBack, modifier = Modifier.testTag("back-button")) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = ui.back, tint = RoyalBlue)
                Text(" ${ui.back}", color = RoyalBlue)
            }
        }
        OrnamentStrip()
        Text(copy.subtitle, color = Ink)
        Spacer(Modifier.height(8.dp))
        if (key == "ai") {
            RoyalArchitecturalLocks(language, ui)
        } else {
            Card(
                colors = CardDefaults.cardColors(containerColor = Cream.copy(alpha = 0.97f)),
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, RoyalGold.copy(alpha = 0.65f), RoundedCornerShape(22.dp)),
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("✦", color = RoyalGold)
                    Text(ui.featureStatus, color = SoftInk)
                }
            }
        }
    }
}

@Composable
private fun RoyalArchitecturalLocks(language: AppLanguage, ui: UiCopy) {
    val initial = listOf(true, true, true, false, true, true)
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(ui.lockIntro, color = SoftInk)
        StudioLocalization.lockLabels(language).forEachIndexed { index, label ->
            var checked by remember(label) { mutableStateOf(initial[index]) }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Cream, RoundedCornerShape(18.dp))
                    .border(1.dp, RoyalGold.copy(alpha = 0.45f), RoundedCornerShape(18.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                    .testTag("lock-$index"),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(label, color = Ink, modifier = Modifier.weight(1f))
                Switch(checked = checked, onCheckedChange = { checked = it })
            }
        }
    }
}

@Composable
private fun RoyalAboutScreen(padding: PaddingValues, ui: UiCopy) {
    val uriHandler = LocalUriHandler.current
    RoyalPageFrame(padding = padding, testTag = "screen-about") {
        Text("✦ ${ui.aboutTitle} ✦", color = MidnightBlue, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
        OrnamentStrip()
        Card(
            colors = CardDefaults.cardColors(containerColor = Cream.copy(alpha = 0.97f)),
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier.fillMaxWidth().border(1.dp, RoyalGold.copy(alpha = 0.70f), RoundedCornerShape(22.dp)),
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(ui.aboutBody, color = Ink, textAlign = TextAlign.Start)
                Text(ui.contactTitle, color = RoyalBlue, fontWeight = FontWeight.Black)
            }
        }
        RoyalExternalLink(ui.website, NexvaryLinks.WEBSITE, "social-website", Icons.Outlined.Public, uriHandler::openUri)
        RoyalExternalLink(ui.facebook, NexvaryLinks.FACEBOOK, "social-facebook", Icons.Outlined.Share, uriHandler::openUri)
        RoyalExternalLink(ui.email, NexvaryLinks.EMAIL, "social-email", Icons.Outlined.Email, uriHandler::openUri)
        RoyalExternalLink(ui.youtube, NexvaryLinks.YOUTUBE, "social-youtube", Icons.Outlined.Star, uriHandler::openUri)
        RoyalExternalLink(ui.x, NexvaryLinks.X, "social-x", Icons.Outlined.Share, uriHandler::openUri)
        Spacer(Modifier.height(18.dp))
    }
}

@Composable
private fun RoyalExternalLink(
    label: String,
    uri: String,
    tag: String,
    icon: ImageVector,
    open: (String) -> Unit,
) {
    OutlinedButton(
        onClick = { open(uri) },
        modifier = Modifier.fillMaxWidth().testTag(tag),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = RoyalBlue),
    ) {
        Icon(icon, contentDescription = null, tint = RoyalGold)
        Text("  $label", fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun RoyalSettingsScreen(
    padding: PaddingValues,
    language: AppLanguage,
    ui: UiCopy,
    onLanguageChanged: (AppLanguage) -> Unit,
    onBack: () -> Unit,
) {
    var ornaments by rememberSaveable { mutableStateOf(true) }
    RoyalPageFrame(padding = padding, testTag = "screen-settings") {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(settingsLabel(), color = MidnightBlue, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f))
            OutlinedButton(onClick = onBack, modifier = Modifier.testTag("settings-back")) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = ui.back)
                Text(" ${ui.back}")
            }
        }
        OrnamentStrip()
        SettingsCard(title = languageSettingsLabel(language)) {
            Text(ui.language, color = SoftInk)
            AppLanguage.entries.forEach { item ->
                TextButton(
                    onClick = { onLanguageChanged(item) },
                    modifier = Modifier.fillMaxWidth().testTag("settings-lang-${item.code}"),
                ) {
                    Text(
                        if (item == language) "✦ ${item.nativeName}" else item.nativeName,
                        color = if (item == language) RoyalBlue else Ink,
                        fontWeight = if (item == language) FontWeight.Black else FontWeight.Normal,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start,
                    )
                }
            }
        }
        SettingsCard(title = themeSettingsLabel(language)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Andalus Royal", color = MidnightBlue, fontWeight = FontWeight.Black)
                    Text("Rose • Royal Gold • Royal Blue", color = SoftInk, style = MaterialTheme.typography.bodySmall)
                }
                Switch(checked = ornaments, onCheckedChange = { ornaments = it })
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                PaletteDot(RoyalRose)
                PaletteDot(RoyalGold)
                PaletteDot(RoyalBlue)
                PaletteDot(Plum)
                PaletteDot(Cream)
            }
        }
        SettingsCard(title = securitySettingsLabel(language)) {
            Text(ui.secureImports, color = Ink)
            Text(ui.testedNavigation, color = Ink)
            Text("HTTPS only • Android 15 gates • Build provenance", color = SoftInk, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.height(18.dp))
    }
}

@Composable
private fun SettingsCard(title: String, content: @Composable () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Cream.copy(alpha = 0.97f)),
        shape = RoundedCornerShape(22.dp),
        modifier = Modifier.fillMaxWidth().border(1.dp, RoyalGold.copy(alpha = 0.60f), RoundedCornerShape(22.dp)),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, color = DeepRose, fontWeight = FontWeight.Black)
            content()
        }
    }
}

@Composable
private fun PaletteDot(color: Color) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .background(color, RoundedCornerShape(50))
            .border(1.dp, DarkGold.copy(alpha = 0.40f), RoundedCornerShape(50)),
    )
}

@Composable
private fun RoyalPageFrame(
    padding: PaddingValues,
    testTag: String,
    content: @Composable () -> Unit,
) {
    RoyalBackdrop {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .testTag(testTag)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().widthIn(max = 760.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                content()
            }
        }
    }
}

@Composable
private fun OrnamentStrip() {
    Text(
        "✦  ❈  ✦  ❈  ✦",
        color = RoyalGold,
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.titleMedium,
    )
}

@Composable
private fun settingsLabel(): String = when (LocalLayoutDirection.current) {
    LayoutDirection.Rtl -> "الإعدادات"
    LayoutDirection.Ltr -> "Settings"
}

private fun homeSectionLabel(language: AppLanguage): String = when (language) {
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

private fun languageSettingsLabel(language: AppLanguage): String = when (language) {
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

private fun themeSettingsLabel(language: AppLanguage): String = when (language) {
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

private fun securitySettingsLabel(language: AppLanguage): String = when (language) {
    AppLanguage.AR -> "الأمان وسلامة التنقل"
    AppLanguage.UR -> "سیکیورٹی اور نیویگیشن"
    AppLanguage.FA -> "امنیت و ناوبری"
    AppLanguage.TR -> "Güvenlik ve gezinme"
    AppLanguage.ES -> "Seguridad y navegación"
    AppLanguage.DE -> "Sicherheit und Navigation"
    AppLanguage.IT -> "Sicurezza e navigazione"
    AppLanguage.FR -> "Sécurité et navigation"
    AppLanguage.RU -> "Безопасность и навигация"
    AppLanguage.EN -> "Security & navigation"
}
