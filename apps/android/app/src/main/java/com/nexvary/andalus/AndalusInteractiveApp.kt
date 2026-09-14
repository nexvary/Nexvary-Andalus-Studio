package com.nexvary.andalus

import android.content.Context
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Refresh
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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

private const val LIVE_SETTINGS = "settings"
private const val LIVE_PREFS = "andalus_live_ui"
private const val LIVE_PALETTE = "palette"

private data class LivePalette(
    val id: String,
    val label: String,
    val roseMist: Color,
    val rose: Color,
    val accent: Color,
    val gold: Color,
    val royal: Color,
    val royalDark: Color,
    val cream: Color,
    val ink: Color,
    val softInk: Color,
)

private val LiveRose = LivePalette(
    "rose", "Rose Royal", Color(0xFFFFF4F6), Color(0xFFF1C5CE), Color(0xFFB95F73),
    Color(0xFFD19B2E), Color(0xFF213A78), Color(0xFF101D4B), Color(0xFFFFFBF5),
    Color(0xFF2C2230), Color(0xFF6A5862),
)
private val LiveSapphire = LivePalette(
    "sapphire", "Sapphire Gold", Color(0xFFF7F2F7), Color(0xFFE1D0DF), Color(0xFF845D83),
    Color(0xFFD3A33A), Color(0xFF173F88), Color(0xFF0B255C), Color(0xFFFFFCF7),
    Color(0xFF231E2A), Color(0xFF645A69),
)
private val LiveEmerald = LivePalette(
    "emerald", "Emerald Andalus", Color(0xFFF3F8F5), Color(0xFFCFE6D9), Color(0xFF4D876F),
    Color(0xFFCA9830), Color(0xFF176A59), Color(0xFF0C4338), Color(0xFFFFFCF5),
    Color(0xFF20302B), Color(0xFF5B6A64),
)
private val LivePalettes = listOf(LiveRose, LiveSapphire, LiveEmerald)
private val LocalLivePalette = staticCompositionLocalOf { LiveRose }

@Composable
fun AndalusInteractiveApp() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(LIVE_PREFS, Context.MODE_PRIVATE) }
    var paletteId by rememberSaveable {
        mutableStateOf(prefs.getString(LIVE_PALETTE, LiveRose.id) ?: LiveRose.id)
    }
    val palette = LivePalettes.firstOrNull { it.id == paletteId } ?: LiveRose

    CompositionLocalProvider(LocalLivePalette provides palette) {
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
                outline = palette.gold,
            ),
        ) {
            LiveStudioContent(
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
private fun LiveStudioContent(paletteId: String, onPalette: (String) -> Unit) {
    var destination by rememberSaveable { mutableStateOf(StudioRoutes.HOME) }
    var languageCode by rememberSaveable { mutableStateOf(AppLanguage.AR.code) }
    val language = AppLanguage.entries.firstOrNull { it.code == languageCode } ?: AppLanguage.AR
    val ui = StudioLocalization.ui(language)
    val layout = if (language.rtl) LayoutDirection.Rtl else LayoutDirection.Ltr
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val feature = destination in StudioRoutes.serviceIds

    BackHandler(enabled = destination != StudioRoutes.HOME) {
        destination = if (feature) StudioRoutes.SERVICES else StudioRoutes.HOME
    }

    CompositionLocalProvider(LocalLayoutDirection provides layout) {
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
                        if (!feature && destination != LIVE_SETTINGS) {
                            LiveBottomNav(destination, ui) { destination = it }
                        }
                    },
                ) { padding ->
                    when (destination) {
                        StudioRoutes.HOME -> LiveHome(padding, ui, language, { destination = StudioRoutes.SERVICES }) { destination = it }
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
        Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(c.roseMist, c.rose.copy(alpha = .78f), c.roseMist)),
        ),
    ) {
        Text("❈", color = c.gold.copy(alpha = .16f), style = MaterialTheme.typography.displayLarge, modifier = Modifier.align(Alignment.TopStart).padding(18.dp))
        Text("✦", color = c.royal.copy(alpha = .10f), style = MaterialTheme.typography.displayLarge, modifier = Modifier.align(Alignment.CenterEnd).padding(20.dp))
        Text("❈", color = c.gold.copy(alpha = .13f), style = MaterialTheme.typography.displayLarge, modifier = Modifier.align(Alignment.BottomStart).padding(22.dp))
        content()
    }
}

@Composable
private fun LiveTopBar(language: AppLanguage, ui: UiCopy, onMenu: () -> Unit, onLanguage: (AppLanguage) -> Unit) {
    val c = LocalLivePalette.current
    var expanded by remember { mutableStateOf(false) }
    Surface(color = c.cream.copy(alpha = .98f), shadowElevation = 5.dp) {
        Row(
            Modifier.fillMaxWidth().safeDrawingPadding().padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(onClick = onMenu, modifier = Modifier.testTag("menu-button")) {
                Icon(Icons.Outlined.Menu, "Menu", tint = c.royal)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("NEXVARY", color = c.gold, fontWeight = FontWeight.Black)
                Text("Andalus Studio", color = c.royalDark, fontWeight = FontWeight.SemiBold)
            }
            Box {
                TextButton(onClick = { expanded = true }, modifier = Modifier.testTag("language-picker")) {
                    Icon(Icons.Outlined.Language, ui.language, tint = c.gold)
                    Text(" ${language.nativeName}", color = c.accent)
                }
                DropdownMenu(expanded, { expanded = false }) {
                    AppLanguage.entries.forEach { item ->
                        DropdownMenuItem(
                            modifier = Modifier.testTag("lang-${item.code}"),
                            text = { Text(item.nativeName) },
                            onClick = { expanded = false; onLanguage(item) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LiveDrawer(destination: String, ui: UiCopy, onNavigate: (String) -> Unit) {
    val c = LocalLivePalette.current
    ModalDrawerSheet(drawerContainerColor = c.cream, modifier = Modifier.widthIn(max = 330.dp)) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            Text("✦ NEXVARY ✦", color = c.gold, fontWeight = FontWeight.Black)
            Text("Andalus Studio", color = c.royalDark, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
            LiveOrnament()
            LiveDrawerItem(ui.home, destination == StudioRoutes.HOME, "drawer-home", Icons.Outlined.Home) { onNavigate(StudioRoutes.HOME) }
            LiveDrawerItem(ui.services, destination == StudioRoutes.SERVICES, "drawer-services", Icons.Outlined.Build) { onNavigate(StudioRoutes.SERVICES) }
            LiveDrawerItem(ui.about, destination == StudioRoutes.ABOUT, "drawer-about", Icons.Outlined.Info) { onNavigate(StudioRoutes.ABOUT) }
            LiveDrawerItem(if (LocalLayoutDirection.current == LayoutDirection.Rtl) "الإعدادات" else "Settings", destination == LIVE_SETTINGS, "drawer-settings", Icons.Outlined.Settings) { onNavigate(LIVE_SETTINGS) }
        }
    }
}

@Composable
private fun LiveDrawerItem(label: String, selected: Boolean, tag: String, icon: ImageVector, onClick: () -> Unit) {
    NavigationDrawerItem(
        label = { Text(label) }, selected = selected, onClick = onClick,
        icon = { Icon(icon, null) }, modifier = Modifier.testTag(tag),
    )
}

@Composable
private fun LiveBottomNav(destination: String, ui: UiCopy, onNavigate: (String) -> Unit) {
    val c = LocalLivePalette.current
    NavigationBar(containerColor = c.cream) {
        listOf(
            Triple(StudioRoutes.HOME, ui.home, Icons.Outlined.Home),
            Triple(StudioRoutes.SERVICES, ui.services, Icons.Outlined.Build),
            Triple(StudioRoutes.ABOUT, ui.about, Icons.Outlined.Info),
        ).forEach { (route, label, icon) ->
            NavigationBarItem(
                modifier = Modifier.testTag("nav-$route"), selected = destination == route,
                onClick = { onNavigate(route) }, icon = { Icon(icon, label) }, label = { Text(label) },
            )
        }
    }
}

@Composable
private fun LiveHome(padding: PaddingValues, ui: UiCopy, language: AppLanguage, onStart: () -> Unit, onOpen: (String) -> Unit) {
    val c = LocalLivePalette.current
    val services = StudioLocalization.services(language)
    LiveBackdrop {
        Column(
            Modifier.fillMaxSize().testTag("screen-home").padding(padding).verticalScroll(rememberScrollState()).padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(Modifier.fillMaxWidth().widthIn(max = 760.dp)) {
                Surface(
                    color = c.royalDark,
                    shape = RoundedCornerShape(32.dp),
                    modifier = Modifier.fillMaxWidth().border(2.dp, c.gold, RoundedCornerShape(32.dp)),
                    shadowElevation = 8.dp,
                ) {
                    Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("✦  ❈  ✦", color = c.gold, style = MaterialTheme.typography.titleLarge)
                        Text("Nexvary Andalus Studio", color = c.cream, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                        Text(ui.tagline, color = c.rose, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 10.dp))
                    }
                }
                Spacer(Modifier.height(14.dp))
                Text(if (language.rtl) "ابدأ التصميم الآن" else "Start designing now", color = c.royalDark, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                Text(ui.homeIntro, color = c.softInk)
                Spacer(Modifier.height(10.dp))
                listOf("projects", "plan", "3d", "patterns").forEach { key ->
                    val copy = services.getValue(key)
                    Card(
                        onClick = { onOpen(key) },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp).testTag("quick-$key").border(1.dp, c.gold, RoundedCornerShape(20.dp)),
                        colors = CardDefaults.cardColors(containerColor = c.cream),
                    ) {
                        Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(liveIcon(key), null, tint = c.royal, modifier = Modifier.size(28.dp))
                            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                                Text(copy.title, color = c.royalDark, fontWeight = FontWeight.Black)
                                Text(copy.subtitle, color = c.softInk, style = MaterialTheme.typography.bodySmall)
                            }
                            Text(copy.badge, color = c.gold, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Button(
                    onClick = onStart,
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp).testTag("home-start-services"),
                    colors = ButtonDefaults.buttonColors(containerColor = c.gold, contentColor = c.royalDark),
                ) { Text(ui.startDesigning, fontWeight = FontWeight.Black) }
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun LiveServices(padding: PaddingValues, language: AppLanguage, ui: UiCopy, onOpen: (String) -> Unit) {
    val c = LocalLivePalette.current
    val services = StudioLocalization.services(language)
    LiveBackdrop {
        LazyColumn(
            Modifier.fillMaxSize().testTag("screen-services").padding(padding).padding(horizontal = 14.dp),
            contentPadding = PaddingValues(vertical = 14.dp), verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Column(Modifier.fillMaxWidth().widthIn(max = 760.dp)) {
                    Text("✦ ${ui.services} ✦", color = c.royalDark, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                    Text(if (language.rtl) "كل بطاقة تفتح أداة قابلة للاستخدام، وليست صفحة عرض." else "Every card opens a usable tool, not a static page.", color = c.softInk)
                    LiveOrnament()
                }
            }
            items(StudioRoutes.serviceIds, key = { it }) { key ->
                val copy = services.getValue(key)
                Card(
                    onClick = { onOpen(key) },
                    modifier = Modifier.fillMaxWidth().widthIn(max = 760.dp).testTag("service-$key").border(1.dp, c.gold, RoundedCornerShape(22.dp)),
                    colors = CardDefaults.cardColors(containerColor = c.cream),
                ) {
                    Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = c.royal, shape = RoundedCornerShape(15.dp)) {
                            Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) { Icon(liveIcon(key), null, tint = c.gold) }
                        }
                        Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                            Text(copy.title, color = c.royalDark, fontWeight = FontWeight.Black)
                            Text(copy.subtitle, color = c.softInk, style = MaterialTheme.typography.bodySmall)
                        }
                        Text(copy.badge, color = c.accent, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun LiveFeature(key: String, padding: PaddingValues, language: AppLanguage, ui: UiCopy, onBack: () -> Unit) {
    val c = LocalLivePalette.current
    val copy = StudioLocalization.services(language).getValue(key)
    LiveBackdrop {
        Column(
            Modifier.fillMaxSize().testTag("screen-$key").padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp), horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(Modifier.fillMaxWidth().widthIn(max = 760.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(copy.title, color = c.royalDark, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                        Text(copy.subtitle, color = c.softInk)
                    }
                    OutlinedButton(onClick = onBack, modifier = Modifier.testTag("back-button")) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, ui.back)
                        Text(" ${ui.back}")
                    }
                }
                LiveOrnament()
                when (key) {
                    "projects" -> ProjectWorkspace(language)
                    "ai" -> AiWorkspace(language)
                    "plan" -> PlanWorkspace(language)
                    "3d" -> SceneWorkspace(language)
                    "patterns" -> PatternWorkspace(language)
                    "assets" -> AssetWorkspace(language)
                    "materials" -> MaterialsWorkspace(language)
                    "exports" -> ExportWorkspace(language)
                    "ar" -> ArWorkspace(language)
                    "library" -> LibraryWorkspace(language)
                }
                Spacer(Modifier.height(18.dp))
            }
        }
    }
}

@Composable
private fun ProjectWorkspace(language: AppLanguage) {
    var name by rememberSaveable { mutableStateOf("") }
    val projects = remember { mutableStateListOf("قصر غرناطة", "فناء قرطبة") }
    WorkspaceCard("workspace-projects", if (language.rtl) "مشاريعي" else "My projects") {
        OutlinedTextField(value = name, onValueChange = { name = it }, modifier = Modifier.fillMaxWidth().testTag("project-name"), label = { Text(if (language.rtl) "اسم المشروع" else "Project name") })
        Button(onClick = { if (name.isNotBlank()) { projects.add(name.trim()); name = "" } }, modifier = Modifier.testTag("project-create")) {
            Icon(Icons.Outlined.Add, null); Text(if (language.rtl) " إنشاء مشروع" else " Create project")
        }
        projects.forEachIndexed { index, item -> Text("${index + 1}. $item", modifier = Modifier.testTag("project-item-$index")) }
    }
}

@Composable
private fun AiWorkspace(language: AppLanguage) {
    var facade by rememberSaveable { mutableStateOf(true) }
    var courtyard by rememberSaveable { mutableStateOf(true) }
    var status by rememberSaveable { mutableStateOf(if (language.rtl) "جاهز للتوليد" else "Ready to generate") }
    WorkspaceCard("workspace-ai", if (language.rtl) "مساعد التصميم الذكي" else "AI design assistant") {
        ToggleRow(if (language.rtl) "قفل الواجهة" else "Lock facade", facade, { facade = it }, "ai-lock-facade")
        ToggleRow(if (language.rtl) "قفل الفناء" else "Lock courtyard", courtyard, { courtyard = it }, "ai-lock-courtyard")
        Button(onClick = { status = if (language.rtl) "تم إنشاء اقتراح أندلسي مع احترام الأقفال" else "Andalusian proposal generated with locks respected" }, modifier = Modifier.testTag("ai-generate")) { Text(if (language.rtl) "ولّد اقتراحًا" else "Generate proposal") }
        Text(status, modifier = Modifier.testTag("ai-status"))
    }
}

@Composable
private fun PlanWorkspace(language: AppLanguage) {
    var walls by rememberSaveable { mutableIntStateOf(4) }
    var grid by rememberSaveable { mutableStateOf(true) }
    WorkspaceCard("workspace-plan", if (language.rtl) "محرر المخطط ثنائي الأبعاد" else "2D plan editor") {
        Text(if (language.rtl) "عدد الجدران: $walls" else "Walls: $walls", modifier = Modifier.testTag("plan-wall-count"))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { walls++ }, modifier = Modifier.testTag("plan-add-wall")) { Text(if (language.rtl) "أضف جدارًا" else "Add wall") }
            OutlinedButton(onClick = { if (walls > 1) walls-- }, modifier = Modifier.testTag("plan-remove-wall")) { Text(if (language.rtl) "حذف جدار" else "Remove wall") }
        }
        ToggleRow(if (language.rtl) "إظهار الشبكة" else "Show grid", grid, { grid = it }, "plan-grid")
        Surface(color = LocalLivePalette.current.rose.copy(alpha = .45f), shape = RoundedCornerShape(16.dp)) {
            Text("▭  ┃  ━  ┃  ▭\n┃     □     ┃\n▭  ━  ━  ━  ▭", modifier = Modifier.fillMaxWidth().padding(20.dp), textAlign = TextAlign.Center, color = LocalLivePalette.current.royalDark)
        }
    }
}

@Composable
private fun SceneWorkspace(language: AppLanguage) {
    var rotation by rememberSaveable { mutableIntStateOf(0) }
    var zoom by rememberSaveable { mutableIntStateOf(100) }
    WorkspaceCard("workspace-3d", if (language.rtl) "المشهد ثلاثي الأبعاد" else "3D scene") {
        Surface(color = LocalLivePalette.current.royalDark, shape = RoundedCornerShape(18.dp)) {
            Box(Modifier.fillMaxWidth().height(180.dp), contentAlignment = Alignment.Center) {
                Text("⌂\n${rotation}°  •  ${zoom}%", color = LocalLivePalette.current.gold, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center, modifier = Modifier.testTag("scene-state"))
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { rotation = (rotation + 15) % 360 }, modifier = Modifier.testTag("scene-rotate")) { Icon(Icons.Outlined.Refresh, null); Text(if (language.rtl) " تدوير" else " Rotate") }
            OutlinedButton(onClick = { zoom = (zoom + 10).coerceAtMost(200) }, modifier = Modifier.testTag("scene-zoom")) { Text(if (language.rtl) "تكبير" else "Zoom") }
        }
    }
}

@Composable
private fun PatternWorkspace(language: AppLanguage) {
    val patterns = listOf("❈ نجمة ثمانية", "✦ هندسي ذهبي", "❉ زهري أندلسي")
    var selected by rememberSaveable { mutableIntStateOf(0) }
    WorkspaceCard("workspace-patterns", if (language.rtl) "مكتبة الزخارف" else "Pattern library") {
        patterns.forEachIndexed { index, pattern ->
            OutlinedButton(onClick = { selected = index }, modifier = Modifier.fillMaxWidth().testTag("pattern-$index")) { Text(pattern) }
        }
        Text(if (language.rtl) "المحدد: ${patterns[selected]}" else "Selected: ${patterns[selected]}", modifier = Modifier.testTag("pattern-selected"), color = LocalLivePalette.current.royalDark, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun AssetWorkspace(language: AppLanguage) {
    var query by rememberSaveable { mutableStateOf("") }
    val all = listOf("قوس حدوة حصان", "باب خشبي مزخرف", "نافورة رخام", "مشربية", "عمود أندلسي")
    val filtered = all.filter { query.isBlank() || it.contains(query, ignoreCase = true) }
    WorkspaceCard("workspace-assets", if (language.rtl) "الأصول والعناصر" else "Assets") {
        OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth().testTag("asset-search"), leadingIcon = { Icon(Icons.Outlined.Search, null) }, label = { Text(if (language.rtl) "بحث" else "Search") })
        filtered.forEach { Text("• $it") }
        Text(if (language.rtl) "${filtered.size} نتيجة" else "${filtered.size} results", modifier = Modifier.testTag("asset-count"), color = LocalLivePalette.current.accent)
    }
}

@Composable
private fun MaterialsWorkspace(language: AppLanguage) {
    var marble by rememberSaveable { mutableIntStateOf(10) }
    var wood by rememberSaveable { mutableIntStateOf(5) }
    val total = marble * 1200 + wood * 850
    WorkspaceCard("workspace-materials", if (language.rtl) "الخامات والكميات" else "Materials & quantities") {
        QuantityRow(if (language.rtl) "رخام" else "Marble", marble, { marble = (marble + it).coerceAtLeast(0) }, "material-marble")
        QuantityRow(if (language.rtl) "خشب" else "Wood", wood, { wood = (wood + it).coerceAtLeast(0) }, "material-wood")
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Calculate, null, tint = LocalLivePalette.current.gold)
            Text(if (language.rtl) " التكلفة التقديرية: $total" else " Estimated cost: $total", modifier = Modifier.testTag("material-total"), fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun ExportWorkspace(language: AppLanguage) {
    var format by rememberSaveable { mutableStateOf("PDF") }
    var status by rememberSaveable { mutableStateOf("") }
    WorkspaceCard("workspace-exports", if (language.rtl) "التصدير" else "Export") {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("PDF", "PNG", "GLTF").forEach { item ->
                OutlinedButton(onClick = { format = item }, modifier = Modifier.testTag("export-${item.lowercase()}")) { Text(item) }
            }
        }
        Text(if (language.rtl) "الصيغة: $format" else "Format: $format", modifier = Modifier.testTag("export-format"))
        Button(onClick = { status = if (language.rtl) "تم تجهيز حزمة $format محليًا" else "$format package prepared locally" }, modifier = Modifier.testTag("export-run")) { Icon(Icons.Outlined.Share, null); Text(if (language.rtl) " تجهيز التصدير" else " Prepare export") }
        if (status.isNotBlank()) Text(status, modifier = Modifier.testTag("export-status"), color = LocalLivePalette.current.accent)
    }
}

@Composable
private fun ArWorkspace(language: AppLanguage) {
    var enabled by rememberSaveable { mutableStateOf(false) }
    var scale by rememberSaveable { mutableIntStateOf(100) }
    WorkspaceCard("workspace-ar", if (language.rtl) "الواقع المعزز" else "Augmented reality") {
        ToggleRow(if (language.rtl) "تفعيل وضع AR" else "Enable AR mode", enabled, { enabled = it }, "ar-toggle")
        Button(onClick = { scale = if (scale == 100) 50 else 100 }, enabled = enabled, modifier = Modifier.testTag("ar-scale")) { Text(if (language.rtl) "المقياس $scale%" else "Scale $scale%") }
        Text(if (enabled) (if (language.rtl) "جاهز لوضع النموذج في المساحة" else "Ready to place model") else (if (language.rtl) "فعّل AR للبدء" else "Enable AR to begin"), modifier = Modifier.testTag("ar-status"))
    }
}

@Composable
private fun LibraryWorkspace(language: AppLanguage) {
    val favorites = remember { mutableStateListOf("قصر الحمراء", "باحة إشبيليا") }
    WorkspaceCard("workspace-library", if (language.rtl) "المكتبة" else "Library") {
        favorites.forEachIndexed { index, item ->
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("★ $item", modifier = Modifier.weight(1f))
                TextButton(onClick = { favorites.removeAt(index) }, modifier = Modifier.testTag("library-remove-$index")) { Text(if (language.rtl) "حذف" else "Remove") }
            }
        }
        Text(if (language.rtl) "العناصر المحفوظة: ${favorites.size}" else "Saved items: ${favorites.size}", modifier = Modifier.testTag("library-count"), color = LocalLivePalette.current.accent)
    }
}

@Composable
private fun WorkspaceCard(tag: String, title: String, content: @Composable () -> Unit) {
    val c = LocalLivePalette.current
    Card(
        colors = CardDefaults.cardColors(containerColor = c.cream),
        shape = RoundedCornerShape(22.dp),
        modifier = Modifier.fillMaxWidth().testTag(tag).border(1.dp, c.gold, RoundedCornerShape(22.dp)),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, color = c.royalDark, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
            content()
        }
    }
}

@Composable
private fun ToggleRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit, tag: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, modifier = Modifier.weight(1f))
        Switch(checked, onChange, modifier = Modifier.testTag(tag))
    }
}

@Composable
private fun QuantityRow(label: String, quantity: Int, change: (Int) -> Unit, tag: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, modifier = Modifier.weight(1f))
        OutlinedButton(onClick = { change(-1) }, modifier = Modifier.testTag("$tag-minus")) { Text("−") }
        Text(quantity.toString(), modifier = Modifier.testTag("$tag-value"), fontWeight = FontWeight.Black)
        Button(onClick = { change(1) }, modifier = Modifier.testTag("$tag-plus")) { Text("+") }
    }
}

@Composable
private fun LiveAbout(padding: PaddingValues, ui: UiCopy) {
    val c = LocalLivePalette.current
    val uri = LocalUriHandler.current
    LiveBackdrop {
        Column(Modifier.fillMaxSize().testTag("screen-about").padding(padding).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("✦ ${ui.aboutTitle} ✦", color = c.royalDark, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
            Text(ui.aboutBody, color = c.ink)
            LiveOrnament()
            listOf(
                Triple(ui.website, NexvaryLinks.WEBSITE, "social-website"),
                Triple(ui.facebook, NexvaryLinks.FACEBOOK, "social-facebook"),
                Triple(ui.email, NexvaryLinks.EMAIL, "social-email"),
                Triple(ui.youtube, NexvaryLinks.YOUTUBE, "social-youtube"),
                Triple(ui.x, NexvaryLinks.X, "social-x"),
            ).forEach { (label, link, tag) ->
                OutlinedButton(onClick = { uri.openUri(link) }, modifier = Modifier.fillMaxWidth().testTag(tag)) {
                    Icon(if (tag == "social-email") Icons.Outlined.Email else Icons.Outlined.Public, null, tint = c.gold)
                    Text("  $label", color = c.royalDark)
                }
            }
        }
    }
}

@Composable
private fun LiveSettings(
    padding: PaddingValues,
    language: AppLanguage,
    ui: UiCopy,
    paletteId: String,
    onPalette: (String) -> Unit,
    onLanguage: (AppLanguage) -> Unit,
    onBack: () -> Unit,
) {
    val c = LocalLivePalette.current
    LiveBackdrop {
        Column(Modifier.fillMaxSize().testTag("screen-settings").padding(padding).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(if (language.rtl) "الإعدادات" else "Settings", color = c.royalDark, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f))
                OutlinedButton(onClick = onBack, modifier = Modifier.testTag("settings-back")) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, ui.back); Text(" ${ui.back}") }
            }
            LiveOrnament()
            WorkspaceCard("settings-theme", if (language.rtl) "الهوية والألوان" else "Identity & colors") {
                LivePalettes.forEach { option ->
                    OutlinedButton(onClick = { onPalette(option.id) }, modifier = Modifier.fillMaxWidth().testTag("theme-${option.id}")) {
                        Text(if (paletteId == option.id) "✦ ${option.label}" else option.label, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
                    }
                }
                Text(LivePalettes.first { it.id == paletteId }.label, modifier = Modifier.testTag("theme-current"), color = c.accent, fontWeight = FontWeight.Black)
            }
            WorkspaceCard("settings-language", ui.language) {
                AppLanguage.entries.forEach { item ->
                    TextButton(onClick = { onLanguage(item) }, modifier = Modifier.fillMaxWidth().testTag("settings-lang-${item.code}")) {
                        Text(if (item == language) "✦ ${item.nativeName}" else item.nativeName, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
                    }
                }
            }
        }
    }
}

@Composable
private fun LiveOrnament() {
    Text("✦  ❈  ✦  ❈  ✦", color = LocalLivePalette.current.gold, modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp), textAlign = TextAlign.Center)
}

private fun liveIcon(key: String): ImageVector = when (key) {
    "projects" -> Icons.Outlined.Folder
    "ai" -> Icons.Outlined.Star
    "assets" -> Icons.Outlined.Search
    "materials" -> Icons.Outlined.Calculate
    "exports" -> Icons.Outlined.Share
    "ar" -> Icons.Outlined.Public
    "library" -> Icons.Outlined.Info
    else -> Icons.Outlined.Build
}
