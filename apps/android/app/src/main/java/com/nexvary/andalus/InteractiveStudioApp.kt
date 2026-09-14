package com.nexvary.andalus

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Public
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
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
import kotlin.math.cos
import kotlin.math.sin

private const val LIVE_SETTINGS = "settings"
private const val LIVE_PREFS = "andalus_live_ui"
private const val LIVE_PALETTE = "palette"

private data class LivePalette(
    val id: String,
    val title: String,
    val rose: Color,
    val roseDeep: Color,
    val gold: Color,
    val royal: Color,
    val royalDark: Color,
    val cream: Color,
    val ink: Color,
    val softInk: Color,
)

private val RoseGold = LivePalette(
    id = "rose",
    title = "Rose Royal",
    rose = Color(0xFFF8DDE2),
    roseDeep = Color(0xFFD8AEB7),
    gold = Color(0xFFD2A13A),
    royal = Color(0xFF183A7B),
    royalDark = Color(0xFF0A2458),
    cream = Color(0xFFFFFBF5),
    ink = Color(0xFF2B2430),
    softInk = Color(0xFF6A5B63),
)

private val SapphireGold = RoseGold.copy(
    id = "sapphire",
    title = "Sapphire Gold",
    rose = Color(0xFFE9DDF0),
    roseDeep = Color(0xFFBBA5C8),
    royal = Color(0xFF173F88),
    royalDark = Color(0xFF0B285F),
)

private val EmeraldGold = RoseGold.copy(
    id = "emerald",
    title = "Emerald Andalus",
    rose = Color(0xFFDCEDE5),
    roseDeep = Color(0xFFAFD4C1),
    royal = Color(0xFF176A59),
    royalDark = Color(0xFF0E453B),
)

private val LivePalettes = listOf(RoseGold, SapphireGold, EmeraldGold)
private val LocalLivePalette = staticCompositionLocalOf { RoseGold }

private val LiveArch = GenericShape { size, _ ->
    moveTo(0f, size.height)
    lineTo(0f, size.height * 0.38f)
    quadraticTo(size.width * 0.09f, 0f, size.width * 0.5f, 0f)
    quadraticTo(size.width * 0.91f, 0f, size.width, size.height * 0.38f)
    lineTo(size.width, size.height)
    close()
}

@Composable
fun InteractiveAndalusStudioApp() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(LIVE_PREFS, Context.MODE_PRIVATE) }
    var paletteId by rememberSaveable { mutableStateOf(prefs.getString(LIVE_PALETTE, RoseGold.id) ?: RoseGold.id) }
    val palette = LivePalettes.firstOrNull { it.id == paletteId } ?: RoseGold

    CompositionLocalProvider(LocalLivePalette provides palette) {
        MaterialTheme(
            colorScheme = lightColorScheme(
                primary = palette.gold,
                onPrimary = palette.royalDark,
                secondary = palette.royal,
                onSecondary = palette.cream,
                background = palette.rose,
                onBackground = palette.ink,
                surface = palette.cream,
                onSurface = palette.ink,
                outline = palette.gold,
            ),
        ) {
            InteractiveContent(
                paletteId = paletteId,
                onPalette = {
                    paletteId = it
                    prefs.edit().putString(LIVE_PALETTE, it).apply()
                },
            )
        }
    }
}

@Composable
private fun InteractiveContent(paletteId: String, onPalette: (String) -> Unit) {
    var destination by rememberSaveable { mutableStateOf(StudioRoutes.HOME) }
    var languageCode by rememberSaveable { mutableStateOf(AppLanguage.AR.code) }
    val language = AppLanguage.entries.firstOrNull { it.code == languageCode } ?: AppLanguage.AR
    val ui = StudioLocalization.ui(language)
    val isFeature = destination in StudioRoutes.serviceIds
    val direction = if (language.rtl) LayoutDirection.Rtl else LayoutDirection.Ltr
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    BackHandler(enabled = destination != StudioRoutes.HOME) {
        destination = if (isFeature) StudioRoutes.SERVICES else StudioRoutes.HOME
    }

    CompositionLocalProvider(LocalLayoutDirection provides direction) {
        Box(Modifier.fillMaxSize().testTag("palette-$paletteId")) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    LiveDrawer(destination, ui) {
                        destination = it
                        scope.launch { drawerState.close() }
                    }
                },
            ) {
                Scaffold(
                    containerColor = Color.Transparent,
                    topBar = {
                        LiveTopBar(
                            language = language,
                            ui = ui,
                            onMenu = { scope.launch { drawerState.open() } },
                            onLanguage = { languageCode = it.code },
                        )
                    },
                    bottomBar = {
                        if (!isFeature && destination != LIVE_SETTINGS) {
                            LiveBottomBar(destination, ui) { destination = it }
                        }
                    },
                ) { padding ->
                    when (destination) {
                        StudioRoutes.HOME -> LiveHome(padding, language, ui, { destination = StudioRoutes.SERVICES }) { destination = it }
                        StudioRoutes.SERVICES -> LiveServices(padding, language, ui) { destination = it }
                        StudioRoutes.ABOUT -> LiveAbout(padding, ui)
                        LIVE_SETTINGS -> LiveSettings(
                            padding = padding,
                            language = language,
                            ui = ui,
                            paletteId = paletteId,
                            onPalette = onPalette,
                            onLanguage = { languageCode = it.code },
                            onBack = { destination = StudioRoutes.HOME },
                        )
                        else -> LiveFeature(destination, padding, language, ui) { destination = StudioRoutes.SERVICES }
                    }
                }
            }
        }
    }
}

@Composable
private fun LiveBackdrop(content: @Composable () -> Unit) {
    val c = LocalLivePalette.current
    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(c.cream, c.rose, c.roseDeep.copy(alpha = 0.7f), c.rose))),
    ) {
        Text("❈", color = c.royal.copy(alpha = 0.08f), style = MaterialTheme.typography.displayLarge, modifier = Modifier.align(Alignment.TopStart).padding(18.dp))
        Text("✦", color = c.gold.copy(alpha = 0.18f), style = MaterialTheme.typography.displayLarge, modifier = Modifier.align(Alignment.CenterEnd).padding(18.dp))
        Text("❈", color = c.royal.copy(alpha = 0.08f), style = MaterialTheme.typography.displayLarge, modifier = Modifier.align(Alignment.BottomStart).padding(22.dp))
        content()
    }
}

@Composable
private fun LiveTopBar(language: AppLanguage, ui: UiCopy, onMenu: () -> Unit, onLanguage: (AppLanguage) -> Unit) {
    val c = LocalLivePalette.current
    var expanded by remember { mutableStateOf(false) }
    Surface(color = c.cream.copy(alpha = 0.98f), shadowElevation = 5.dp) {
        Row(
            Modifier.fillMaxWidth().safeDrawingPadding().padding(horizontal = 10.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onMenu, modifier = Modifier.testTag("menu-button")) {
                Icon(Icons.Outlined.Menu, contentDescription = "Menu", tint = c.royal)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("NEXVARY", color = c.gold, fontWeight = FontWeight.Black)
                Text("Andalus Studio", color = c.royalDark, fontWeight = FontWeight.Bold)
            }
            Box {
                TextButton(onClick = { expanded = true }, modifier = Modifier.testTag("language-picker")) {
                    Icon(Icons.Outlined.Language, contentDescription = ui.language, tint = c.gold)
                    Text(" ${language.nativeName}", color = c.royal)
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    AppLanguage.entries.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item.nativeName) },
                            modifier = Modifier.testTag("lang-${item.code}"),
                            onClick = {
                                expanded = false
                                onLanguage(item)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LiveDrawer(destination: String, ui: UiCopy, navigate: (String) -> Unit) {
    val c = LocalLivePalette.current
    ModalDrawerSheet(drawerContainerColor = c.cream, modifier = Modifier.widthIn(max = 330.dp)) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("✦ NEXVARY ✦", color = c.gold, fontWeight = FontWeight.Black)
            Text("Andalus Studio", color = c.royalDark, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
            GoldOrnament()
            DrawerRow(ui.home, destination == StudioRoutes.HOME, "drawer-home", Icons.Outlined.Home) { navigate(StudioRoutes.HOME) }
            DrawerRow(ui.services, destination == StudioRoutes.SERVICES, "drawer-services", Icons.Outlined.Build) { navigate(StudioRoutes.SERVICES) }
            DrawerRow(ui.about, destination == StudioRoutes.ABOUT, "drawer-about", Icons.Outlined.Info) { navigate(StudioRoutes.ABOUT) }
            DrawerRow(if (LocalLayoutDirection.current == LayoutDirection.Rtl) "الإعدادات" else "Settings", destination == LIVE_SETTINGS, "drawer-settings", Icons.Outlined.Settings) { navigate(LIVE_SETTINGS) }
            HorizontalDivider(color = c.gold.copy(alpha = 0.4f), modifier = Modifier.padding(vertical = 8.dp))
            Text("Live Design Runtime", color = c.softInk, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun DrawerRow(label: String, selected: Boolean, tag: String, icon: ImageVector, click: () -> Unit) {
    NavigationDrawerItem(label = { Text(label) }, selected = selected, onClick = click, icon = { Icon(icon, null) }, modifier = Modifier.testTag(tag))
}

@Composable
private fun LiveBottomBar(destination: String, ui: UiCopy, navigate: (String) -> Unit) {
    val c = LocalLivePalette.current
    NavigationBar(containerColor = c.cream) {
        NavigationBarItem(selected = destination == StudioRoutes.HOME, onClick = { navigate(StudioRoutes.HOME) }, icon = { Icon(Icons.Outlined.Home, ui.home) }, label = { Text(ui.home) }, modifier = Modifier.testTag("nav-home"))
        NavigationBarItem(selected = destination == StudioRoutes.SERVICES, onClick = { navigate(StudioRoutes.SERVICES) }, icon = { Icon(Icons.Outlined.Build, ui.services) }, label = { Text(ui.services) }, modifier = Modifier.testTag("nav-services"))
        NavigationBarItem(selected = destination == StudioRoutes.ABOUT, onClick = { navigate(StudioRoutes.ABOUT) }, icon = { Icon(Icons.Outlined.Info, ui.about) }, label = { Text(ui.about) }, modifier = Modifier.testTag("nav-about"))
    }
}

@Composable
private fun LiveHome(padding: PaddingValues, language: AppLanguage, ui: UiCopy, start: () -> Unit, open: (String) -> Unit) {
    val c = LocalLivePalette.current
    LiveBackdrop {
        Column(
            Modifier.fillMaxSize().testTag("screen-home").padding(padding).verticalScroll(rememberScrollState()).padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(Modifier.fillMaxWidth().widthIn(max = 760.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Surface(modifier = Modifier.fillMaxWidth().height(260.dp).border(2.dp, c.gold, LiveArch), shape = LiveArch, color = c.royal, shadowElevation = 10.dp) {
                    Box(Modifier.background(Brush.verticalGradient(listOf(c.royal, c.royalDark)))) {
                        Column(Modifier.fillMaxSize().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Text("✦  ❈  ✦", color = c.gold, style = MaterialTheme.typography.titleLarge)
                            Text("Nexvary Andalus Studio", color = c.cream, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                            Spacer(Modifier.height(10.dp))
                            Text(ui.tagline, color = c.rose, textAlign = TextAlign.Center, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                Text(if (language.rtl) "ابدأ من هنا" else "Start here", color = c.royalDark, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                Text(ui.homeIntro, color = c.softInk)
                val services = StudioLocalization.services(language)
                listOf("projects", "plan", "3d", "patterns").forEach { key ->
                    Card(onClick = { open(key) }, modifier = Modifier.fillMaxWidth().testTag("quick-$key").border(1.dp, c.gold, RoundedCornerShape(20.dp)), colors = CardDefaults.cardColors(containerColor = c.cream), shape = RoundedCornerShape(20.dp)) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Surface(color = c.rose, shape = RoundedCornerShape(14.dp)) { Box(Modifier.size(44.dp), contentAlignment = Alignment.Center) { Icon(serviceIcon(key), null, tint = c.royal) } }
                            Column(Modifier.weight(1f)) {
                                Text(services.getValue(key).title, color = c.royalDark, fontWeight = FontWeight.Black)
                                Text(services.getValue(key).subtitle, color = c.softInk, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
                Button(onClick = start, modifier = Modifier.fillMaxWidth().testTag("home-start-services"), colors = ButtonDefaults.buttonColors(containerColor = c.gold, contentColor = c.royalDark), shape = RoundedCornerShape(18.dp)) {
                    Text(ui.startDesigning, fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.height(18.dp))
            }
        }
    }
}

@Composable
private fun LiveServices(padding: PaddingValues, language: AppLanguage, ui: UiCopy, open: (String) -> Unit) {
    val c = LocalLivePalette.current
    val services = StudioLocalization.services(language)
    LiveBackdrop {
        LazyColumn(
            Modifier.fillMaxSize().testTag("screen-services").padding(padding).padding(horizontal = 14.dp),
            contentPadding = PaddingValues(vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Column(Modifier.fillMaxWidth().widthIn(max = 760.dp)) {
                    Text("✦ ${ui.services} ✦", color = c.royalDark, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                    Text(ui.tagline, color = c.softInk)
                    GoldOrnament()
                }
            }
            items(StudioRoutes.serviceIds, key = { it }) { key ->
                val copy = services.getValue(key)
                Card(onClick = { open(key) }, modifier = Modifier.fillMaxWidth().widthIn(max = 760.dp).testTag("service-$key").border(1.dp, c.gold, RoundedCornerShape(22.dp)), colors = CardDefaults.cardColors(containerColor = c.cream), shape = RoundedCornerShape(22.dp)) {
                    Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Surface(color = c.royal, shape = RoundedCornerShape(16.dp)) { Box(Modifier.size(52.dp), contentAlignment = Alignment.Center) { Icon(serviceIcon(key), null, tint = c.gold) } }
                        Column(Modifier.weight(1f)) {
                            Text(copy.title, color = c.royalDark, fontWeight = FontWeight.Black)
                            Text(copy.subtitle, color = c.softInk, style = MaterialTheme.typography.bodySmall)
                            Text(copy.badge, color = c.royal, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

private fun serviceIcon(key: String): ImageVector = when (key) {
    "projects" -> Icons.Outlined.Folder
    "ai", "patterns" -> Icons.Outlined.Star
    "exports" -> Icons.Outlined.Share
    "ar" -> Icons.Outlined.Public
    "library" -> Icons.Outlined.Info
    else -> Icons.Outlined.Build
}

@Composable
private fun LiveFeature(key: String, padding: PaddingValues, language: AppLanguage, ui: UiCopy, back: () -> Unit) {
    val c = LocalLivePalette.current
    val copy = StudioLocalization.services(language).getValue(key)
    LiveBackdrop {
        Column(Modifier.fillMaxSize().testTag("screen-$key").padding(padding).verticalScroll(rememberScrollState()).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Column(Modifier.fillMaxWidth().widthIn(max = 760.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(copy.title, color = c.royalDark, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                        Text(copy.badge, color = c.royal, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(onClick = back, modifier = Modifier.testTag("back-button")) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, ui.back)
                        Text(" ${ui.back}")
                    }
                }
                GoldOrnament()
                Text(copy.subtitle, color = c.ink)
                LiveWorkspace(key, language, copy)
                Spacer(Modifier.height(18.dp))
            }
        }
    }
}

@Composable
private fun LiveWorkspace(key: String, language: AppLanguage, copy: ServiceCopy) {
    val c = LocalLivePalette.current
    Card(modifier = Modifier.fillMaxWidth().testTag("workspace-$key").border(1.dp, c.gold, RoundedCornerShape(22.dp)), colors = CardDefaults.cardColors(containerColor = c.cream), shape = RoundedCornerShape(22.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(copy.title, color = c.royalDark, fontWeight = FontWeight.Black)
            when (key) {
                "projects" -> ProjectsWorkspace(language)
                "ai" -> AiWorkspace(language)
                "plan" -> PlanWorkspace(language)
                "3d" -> SceneWorkspace(language)
                "patterns" -> PatternWorkspace(language)
                "assets" -> AssetsWorkspace(language)
                "materials" -> MaterialsWorkspace(language)
                "exports" -> ExportWorkspace(language)
                "ar" -> ArWorkspace(language)
                "library" -> LibraryWorkspace(language)
            }
        }
    }
}

@Composable
private fun ProjectsWorkspace(language: AppLanguage) {
    val c = LocalLivePalette.current
    var projects by rememberSaveable { mutableIntStateOf(1) }
    var revisions by rememberSaveable { mutableIntStateOf(1) }
    ActionRow(
        primary = action(language, "مشروع جديد", "New project"),
        primaryTag = "projects-create",
        onPrimary = { projects++ },
        secondary = action(language, "حفظ نسخة", "Save revision"),
        secondaryTag = "projects-save",
        onSecondary = { revisions++ },
    )
    Text("${action(language, "المشروعات", "Projects")}: $projects", color = c.royalDark, modifier = Modifier.testTag("projects-count"))
    Text("${action(language, "النسخ", "Revisions")}: $revisions", color = c.softInk, modifier = Modifier.testTag("projects-revisions"))
}

@Composable
private fun PlanWorkspace(language: AppLanguage) {
    val c = LocalLivePalette.current
    var rooms by rememberSaveable { mutableIntStateOf(2) }
    var arches by rememberSaveable { mutableIntStateOf(1) }
    ActionRow(
        primary = action(language, "أضف غرفة", "Add room"), primaryTag = "plan-add-room", onPrimary = { rooms = (rooms + 1).coerceAtMost(8) },
        secondary = action(language, "أضف قوس", "Add arch"), secondaryTag = "plan-add-arch", onSecondary = { arches = (arches + 1).coerceAtMost(8) },
    )
    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("${action(language, "غرف", "Rooms")}: $rooms", color = c.royalDark, modifier = Modifier.testTag("plan-rooms-count"))
        Text("${action(language, "أقواس", "Arches")}: $arches", color = c.royalDark, modifier = Modifier.testTag("plan-arches-count"))
    }
    Canvas(Modifier.fillMaxWidth().height(210.dp).background(c.rose.copy(alpha = 0.35f), RoundedCornerShape(18.dp)).testTag("plan-canvas")) {
        val margin = 24f
        val columns = 2
        val rows = ((rooms + 1) / 2).coerceAtLeast(1)
        val w = (size.width - margin * 3) / columns
        val h = (size.height - margin * (rows + 1)) / rows
        repeat(rooms) { index ->
            val col = index % columns
            val row = index / columns
            val left = margin + col * (w + margin)
            val top = margin + row * (h + margin)
            drawRect(color = c.royal, topLeft = Offset(left, top), size = androidx.compose.ui.geometry.Size(w, h), style = Stroke(width = 5f))
        }
        repeat(arches.coerceAtMost(rooms)) { index ->
            val x = margin + (index % columns) * (w + margin) + w / 2
            val y = margin + (index / columns) * (h + margin) + h
            val arch = Path().apply {
                moveTo(x - 26f, y)
                quadraticBezierTo(x, y - 42f, x + 26f, y)
            }
            drawPath(arch, c.gold, style = Stroke(width = 6f))
        }
    }
}

@Composable
private fun SceneWorkspace(language: AppLanguage) {
    val c = LocalLivePalette.current
    var angle by rememberSaveable { mutableIntStateOf(30) }
    var floors by rememberSaveable { mutableIntStateOf(2) }
    ActionRow(
        primary = action(language, "تدوير", "Rotate"), primaryTag = "3d-rotate", onPrimary = { angle = (angle + 15) % 360 },
        secondary = action(language, "أضف طابق", "Add floor"), secondaryTag = "3d-add-floor", onSecondary = { floors = (floors + 1).coerceAtMost(6) },
    )
    Text("${action(language, "زاوية", "Angle")}: $angle° • ${action(language, "طوابق", "Floors")}: $floors", color = c.royalDark, modifier = Modifier.testTag("3d-angle"))
    Canvas(Modifier.fillMaxWidth().height(220.dp).background(c.royalDark, RoundedCornerShape(18.dp)).testTag("3d-canvas")) {
        val rad = Math.toRadians(angle.toDouble())
        val dx = (cos(rad) * 45).toFloat()
        val dy = (sin(rad) * 28).toFloat()
        val baseW = size.width * 0.42f
        val baseH = size.height * 0.18f
        val left = size.width * 0.28f
        val bottom = size.height * 0.82f
        repeat(floors) { floor ->
            val y = bottom - floor * (baseH + 3f)
            val p = Path().apply {
                moveTo(left, y)
                lineTo(left + baseW, y)
                lineTo(left + baseW + dx, y - dy)
                lineTo(left + dx, y - dy)
                close()
            }
            drawPath(p, color = if (floor % 2 == 0) c.gold else c.rose, style = Stroke(width = 5f))
            drawLine(c.cream, Offset(left, y), Offset(left, y - baseH), strokeWidth = 4f)
            drawLine(c.cream, Offset(left + baseW, y), Offset(left + baseW, y - baseH), strokeWidth = 4f)
        }
    }
}

@Composable
private fun PatternWorkspace(language: AppLanguage) {
    val c = LocalLivePalette.current
    var index by rememberSaveable { mutableIntStateOf(1) }
    ActionRow(action(language, "النمط التالي", "Next pattern"), "patterns-next", { index = index % 6 + 1 }, action(language, "السابق", "Previous"), "patterns-prev", { index = if (index == 1) 6 else index - 1 })
    Text("${action(language, "النمط", "Pattern")}: $index", color = c.royalDark, modifier = Modifier.testTag("patterns-index"))
    Canvas(Modifier.fillMaxWidth().height(180.dp).background(c.rose.copy(alpha = 0.28f), RoundedCornerShape(18.dp)).testTag("patterns-canvas")) {
        val step = 48f
        var y = 24f
        while (y < size.height) {
            var x = 24f
            while (x < size.width) {
                val radius = 8f + index * 2f
                drawCircle(if (((x + y) / step).toInt() % 2 == 0) c.gold else c.royal, radius = radius, center = Offset(x, y), style = Stroke(width = 3f))
                x += step
            }
            y += step
        }
    }
}

@Composable
private fun AssetsWorkspace(language: AppLanguage) {
    val c = LocalLivePalette.current
    var selected by rememberSaveable { mutableIntStateOf(0) }
    val names = listOf("Muqarnas", "Mashrabiya", "Zellige")
    Text(action(language, "اختر عنصراً من المكتبة", "Select an asset from the library"), color = c.softInk)
    names.forEachIndexed { index, name ->
        OutlinedButton(onClick = { selected = index }, modifier = Modifier.fillMaxWidth().testTag("assets-select-$index")) {
            Text(if (selected == index) "✦ $name" else name)
        }
    }
    Text(names[selected], color = c.royalDark, fontWeight = FontWeight.Black, modifier = Modifier.testTag("assets-current"))
}

@Composable
private fun MaterialsWorkspace(language: AppLanguage) {
    val c = LocalLivePalette.current
    var quantity by rememberSaveable { mutableIntStateOf(12) }
    ActionRow(action(language, "زيادة", "Increase"), "materials-plus", { quantity++ }, action(language, "تقليل", "Decrease"), "materials-minus", { quantity = (quantity - 1).coerceAtLeast(0) })
    Text("${action(language, "كمية البلاط", "Tile quantity")}: $quantity m²", color = c.royalDark, modifier = Modifier.testTag("materials-count"))
    Text("${action(language, "التكلفة التقديرية", "Estimated cost")}: ${quantity * 42}", color = c.softInk)
}

@Composable
private fun ExportWorkspace(language: AppLanguage) {
    val c = LocalLivePalette.current
    var format by rememberSaveable { mutableStateOf("PDF") }
    var generated by rememberSaveable { mutableIntStateOf(0) }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("PDF", "DXF", "GLTF").forEach { option ->
            OutlinedButton(onClick = { format = option }, modifier = Modifier.weight(1f).testTag("export-format-${option.lowercase()}")) { Text(if (format == option) "✦ $option" else option) }
        }
    }
    Button(onClick = { generated++ }, modifier = Modifier.fillMaxWidth().testTag("exports-generate")) { Text(action(language, "إنشاء التصدير", "Generate export")) }
    Text(if (generated == 0) action(language, "لم يتم إنشاء ملف بعد", "No export generated yet") else "$format • ${action(language, "جاهز", "Ready")} #$generated", color = c.royalDark, modifier = Modifier.testTag("exports-status"))
}

@Composable
private fun ArWorkspace(language: AppLanguage) {
    val c = LocalLivePalette.current
    var active by rememberSaveable { mutableStateOf(false) }
    Button(onClick = { active = !active }, modifier = Modifier.fillMaxWidth().testTag("ar-toggle")) { Text(if (active) action(language, "إيقاف المعاينة", "Stop preview") else action(language, "تشغيل المعاينة", "Start preview")) }
    Surface(color = if (active) c.royalDark else c.rose, shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth().height(150.dp)) {
        Box(contentAlignment = Alignment.Center) { Text(if (active) "AR • 1:1" else "AR", color = if (active) c.gold else c.royalDark, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black) }
    }
    Text(if (active) action(language, "المعاينة المحلية فعالة", "Local preview active") else action(language, "المعاينة متوقفة", "Preview stopped"), color = c.royalDark, modifier = Modifier.testTag("ar-status"))
}

@Composable
private fun LibraryWorkspace(language: AppLanguage) {
    val c = LocalLivePalette.current
    var favorite by rememberSaveable { mutableStateOf(false) }
    Text(action(language, "قالب فناء أندلسي", "Andalusian courtyard template"), color = c.royalDark, fontWeight = FontWeight.Black)
    Button(onClick = { favorite = !favorite }, modifier = Modifier.fillMaxWidth().testTag("library-favorite")) { Text(if (favorite) action(language, "إزالة من المفضلة", "Remove favorite") else action(language, "أضف للمفضلة", "Add favorite")) }
    Text(if (favorite) "★" else "☆", color = c.gold, style = MaterialTheme.typography.displaySmall, modifier = Modifier.testTag("library-status"))
}

@Composable
private fun AiWorkspace(language: AppLanguage) {
    val c = LocalLivePalette.current
    var locked by rememberSaveable { mutableStateOf(true) }
    var runs by rememberSaveable { mutableIntStateOf(0) }
    OutlinedButton(onClick = { locked = !locked }, modifier = Modifier.fillMaxWidth().testTag("ai-lock-toggle")) { Text(if (locked) action(language, "القيود مقفلة", "Constraints locked") else action(language, "القيود مفتوحة", "Constraints unlocked")) }
    Button(onClick = { runs++ }, modifier = Modifier.fillMaxWidth().testTag("ai-generate")) { Text(action(language, "أنشئ اقتراحاً", "Generate proposal")) }
    Text(if (runs == 0) action(language, "لا توجد نتيجة بعد", "No result yet") else "${action(language, "اقتراح", "Proposal")} #$runs • ${if (locked) "LOCKED" else "OPEN"}", color = c.royalDark, modifier = Modifier.testTag("ai-status"))
}

@Composable
private fun ActionRow(primary: String, primaryTag: String, onPrimary: () -> Unit, secondary: String, secondaryTag: String, onSecondary: () -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = onPrimary, modifier = Modifier.weight(1f).testTag(primaryTag)) { Text(primary) }
        OutlinedButton(onClick = onSecondary, modifier = Modifier.weight(1f).testTag(secondaryTag)) { Text(secondary) }
    }
}

private fun action(language: AppLanguage, ar: String, en: String): String = if (language.rtl) ar else en

@Composable
private fun LiveAbout(padding: PaddingValues, ui: UiCopy) {
    val c = LocalLivePalette.current
    val uri = LocalUriHandler.current
    LiveBackdrop {
        Column(Modifier.fillMaxSize().testTag("screen-about").padding(padding).verticalScroll(rememberScrollState()).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Column(Modifier.fillMaxWidth().widthIn(max = 760.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("✦ ${ui.aboutTitle} ✦", color = c.royalDark, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                GoldOrnament()
                Text(ui.aboutBody, color = c.ink)
                Text(ui.contactTitle, color = c.royal, fontWeight = FontWeight.Black)
                SocialButton(ui.website, NexvaryLinks.WEBSITE, "social-website", Icons.Outlined.Public, uri::openUri)
                SocialButton(ui.facebook, NexvaryLinks.FACEBOOK, "social-facebook", Icons.Outlined.Share, uri::openUri)
                SocialButton(ui.email, NexvaryLinks.EMAIL, "social-email", Icons.Outlined.Email, uri::openUri)
                SocialButton(ui.youtube, NexvaryLinks.YOUTUBE, "social-youtube", Icons.Outlined.Star, uri::openUri)
                SocialButton(ui.x, NexvaryLinks.X, "social-x", Icons.Outlined.Share, uri::openUri)
                Spacer(Modifier.height(18.dp))
            }
        }
    }
}

@Composable
private fun SocialButton(label: String, target: String, tag: String, icon: ImageVector, open: (String) -> Unit) {
    val c = LocalLivePalette.current
    OutlinedButton(onClick = { open(target) }, modifier = Modifier.fillMaxWidth().testTag(tag), shape = RoundedCornerShape(18.dp)) {
        Icon(icon, null, tint = c.gold)
        Text("  $label", color = c.royal, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun LiveSettings(padding: PaddingValues, language: AppLanguage, ui: UiCopy, paletteId: String, onPalette: (String) -> Unit, onLanguage: (AppLanguage) -> Unit, onBack: () -> Unit) {
    val c = LocalLivePalette.current
    LiveBackdrop {
        Column(Modifier.fillMaxSize().testTag("screen-settings").padding(padding).verticalScroll(rememberScrollState()).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Column(Modifier.fillMaxWidth().widthIn(max = 760.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(if (language.rtl) "الإعدادات" else "Settings", color = c.royalDark, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f))
                    OutlinedButton(onClick = onBack, modifier = Modifier.testTag("settings-back")) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, ui.back)
                        Text(" ${ui.back}")
                    }
                }
                GoldOrnament()
                SettingsBox(if (language.rtl) "اللغة" else "Language") {
                    AppLanguage.entries.forEach { item ->
                        TextButton(onClick = { onLanguage(item) }, modifier = Modifier.fillMaxWidth().testTag("settings-lang-${item.code}")) {
                            Text(if (item == language) "✦ ${item.nativeName}" else item.nativeName, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
                        }
                    }
                }
                SettingsBox(if (language.rtl) "الهوية والألوان" else "Identity & colors") {
                    val current = LivePalettes.firstOrNull { it.id == paletteId } ?: RoseGold
                    Text(current.title, color = c.royalDark, fontWeight = FontWeight.Black, modifier = Modifier.testTag("theme-current"))
                    LivePalettes.forEach { option ->
                        OutlinedButton(onClick = { onPalette(option.id) }, modifier = Modifier.fillMaxWidth().testTag("theme-${option.id}")) {
                            Text(if (paletteId == option.id) "✦ ${option.title}" else option.title)
                        }
                    }
                }
                Spacer(Modifier.height(18.dp))
            }
        }
    }
}

@Composable
private fun SettingsBox(title: String, content: @Composable () -> Unit) {
    val c = LocalLivePalette.current
    Card(colors = CardDefaults.cardColors(containerColor = c.cream), shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth().border(1.dp, c.gold, RoundedCornerShape(22.dp))) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, color = c.royal, fontWeight = FontWeight.Black)
            content()
        }
    }
}

@Composable
private fun GoldOrnament() {
    val c = LocalLivePalette.current
    Text("✦  ❈  ✦  ❈  ✦", color = c.gold, modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp), textAlign = TextAlign.Center)
}
