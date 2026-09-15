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
import androidx.compose.foundation.layout.width
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

private const val V3_SETTINGS = "settings"
private const val V3_PREFS = "andalus_v3_ui"
private const val V3_PALETTE = "palette"

private data class V3Palette(
    val id: String,
    val title: String,
    val rose: Color,
    val roseSoft: Color,
    val roseDeep: Color,
    val gold: Color,
    val goldDark: Color,
    val royal: Color,
    val royalDark: Color,
    val ivory: Color,
    val ink: Color,
    val muted: Color,
)

private val RoyalRose = V3Palette(
    id = "rose",
    title = "Rose Royal",
    rose = Color(0xFFF1C6D4),
    roseSoft = Color(0xFFFFEDF2),
    roseDeep = Color(0xFFC77E98),
    gold = Color(0xFFD4AF37),
    goldDark = Color(0xFF9C7417),
    royal = Color(0xFF183A78),
    royalDark = Color(0xFF071A46),
    ivory = Color(0xFFFFFBF3),
    ink = Color(0xFF2C2430),
    muted = Color(0xFF735E68),
)

private val RoyalSapphire = RoyalRose.copy(
    id = "sapphire",
    title = "Sapphire Gold",
    rose = Color(0xFFE9D7E8),
    roseSoft = Color(0xFFF9F0FB),
    roseDeep = Color(0xFFB89ABD),
    royal = Color(0xFF153C87),
    royalDark = Color(0xFF061B4A),
)

private val RoyalEmerald = RoyalRose.copy(
    id = "emerald",
    title = "Emerald Andalus",
    rose = Color(0xFFDDEBE6),
    roseSoft = Color(0xFFF2FAF6),
    roseDeep = Color(0xFFA7CBBB),
    royal = Color(0xFF176957),
    royalDark = Color(0xFF0C4439),
)

private val V3Palettes = listOf(RoyalRose, RoyalSapphire, RoyalEmerald)
private val LocalV3Palette = staticCompositionLocalOf { RoyalRose }

private val RoyalArch = GenericShape { size, _ ->
    moveTo(0f, size.height)
    lineTo(0f, size.height * 0.46f)
    cubicTo(size.width * 0.03f, size.height * 0.17f, size.width * 0.28f, 0f, size.width * 0.5f, 0f)
    cubicTo(size.width * 0.72f, 0f, size.width * 0.97f, size.height * 0.17f, size.width, size.height * 0.46f)
    lineTo(size.width, size.height)
    close()
}

@Composable
fun RoyalAndalusStudioV3() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(V3_PREFS, Context.MODE_PRIVATE) }
    var paletteId by rememberSaveable { mutableStateOf(prefs.getString(V3_PALETTE, RoyalRose.id) ?: RoyalRose.id) }
    val palette = V3Palettes.firstOrNull { it.id == paletteId } ?: RoyalRose

    CompositionLocalProvider(LocalV3Palette provides palette) {
        MaterialTheme(
            colorScheme = lightColorScheme(
                primary = palette.gold,
                onPrimary = palette.royalDark,
                secondary = palette.royal,
                onSecondary = palette.ivory,
                background = palette.roseSoft,
                onBackground = palette.ink,
                surface = palette.ivory,
                onSurface = palette.ink,
                outline = palette.gold,
            ),
        ) {
            V3Shell(
                paletteId = paletteId,
                onPalette = {
                    paletteId = it
                    prefs.edit().putString(V3_PALETTE, it).apply()
                },
            )
        }
    }
}

@Composable
private fun V3Shell(paletteId: String, onPalette: (String) -> Unit) {
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
                    V3Drawer(destination, ui) {
                        destination = it
                        scope.launch { drawerState.close() }
                    }
                },
            ) {
                Scaffold(
                    containerColor = Color.Transparent,
                    topBar = {
                        V3TopBar(
                            language = language,
                            ui = ui,
                            onMenu = { scope.launch { drawerState.open() } },
                            onLanguage = { languageCode = it.code },
                        )
                    },
                    bottomBar = {
                        if (!isFeature && destination != V3_SETTINGS) {
                            V3BottomBar(destination, ui) { destination = it }
                        }
                    },
                ) { padding ->
                    when (destination) {
                        StudioRoutes.HOME -> V3Home(padding, language, ui, { destination = StudioRoutes.SERVICES }) { destination = it }
                        StudioRoutes.SERVICES -> V3Services(padding, language, ui) { destination = it }
                        StudioRoutes.ABOUT -> V3About(padding, ui)
                        V3_SETTINGS -> V3Settings(
                            padding = padding,
                            language = language,
                            ui = ui,
                            paletteId = paletteId,
                            onPalette = onPalette,
                            onLanguage = { languageCode = it.code },
                            onBack = { destination = StudioRoutes.HOME },
                        )
                        else -> V3Feature(destination, padding, language, ui) { destination = StudioRoutes.SERVICES }
                    }
                }
            }
        }
    }
}

@Composable
private fun AndalusBackdrop(content: @Composable () -> Unit) {
    val c = LocalV3Palette.current
    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(c.roseSoft, c.rose.copy(alpha = 0.78f), c.roseSoft))),
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val step = 140f
            var y = 70f
            while (y < size.height) {
                var x = 50f
                while (x < size.width) {
                    drawCircle(c.gold.copy(alpha = 0.10f), radius = 16f, center = Offset(x, y), style = Stroke(width = 2f))
                    drawCircle(c.royal.copy(alpha = 0.045f), radius = 7f, center = Offset(x, y), style = Stroke(width = 2f))
                    drawLine(c.gold.copy(alpha = 0.075f), Offset(x - 20f, y), Offset(x + 20f, y), strokeWidth = 2f)
                    drawLine(c.gold.copy(alpha = 0.075f), Offset(x, y - 20f), Offset(x, y + 20f), strokeWidth = 2f)
                    x += step
                }
                y += step
            }
        }
        content()
    }
}

@Composable
private fun V3TopBar(language: AppLanguage, ui: UiCopy, onMenu: () -> Unit, onLanguage: (AppLanguage) -> Unit) {
    val c = LocalV3Palette.current
    var expanded by remember { mutableStateOf(false) }
    Surface(color = c.royalDark, shadowElevation = 9.dp) {
        Column(Modifier.safeDrawingPadding()) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                IconButton(onClick = onMenu, modifier = Modifier.testTag("menu-button")) {
                    Icon(Icons.Outlined.Menu, contentDescription = "Menu", tint = c.gold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("NEXVARY", color = c.gold, fontWeight = FontWeight.Black)
                    Text("ANDALUS STUDIO", color = c.ivory, fontWeight = FontWeight.ExtraBold)
                }
                Box {
                    TextButton(onClick = { expanded = true }, modifier = Modifier.testTag("language-picker")) {
                        Icon(Icons.Outlined.Language, contentDescription = ui.language, tint = c.gold)
                        Text(" ${language.nativeName}", color = c.ivory)
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
            Box(Modifier.fillMaxWidth().height(3.dp).background(Brush.horizontalGradient(listOf(Color.Transparent, c.gold, c.goldDark, c.gold, Color.Transparent))))
        }
    }
}

@Composable
private fun V3Drawer(destination: String, ui: UiCopy, navigate: (String) -> Unit) {
    val c = LocalV3Palette.current
    ModalDrawerSheet(drawerContainerColor = c.roseSoft, modifier = Modifier.widthIn(max = 332.dp)) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Surface(shape = RoyalArch, color = c.royalDark, modifier = Modifier.fillMaxWidth().height(190.dp).border(2.dp, c.gold, RoyalArch)) {
                Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    TughraInspiredMark(Modifier.width(160.dp).height(90.dp))
                    Text("NEXVARY", color = c.gold, fontWeight = FontWeight.Black)
                    Text("Andalus Studio", color = c.ivory, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                }
            }
            Spacer(Modifier.height(4.dp))
            V3DrawerRow(ui.home, destination == StudioRoutes.HOME, "drawer-home", Icons.Outlined.Home) { navigate(StudioRoutes.HOME) }
            V3DrawerRow(ui.services, destination == StudioRoutes.SERVICES, "drawer-services", Icons.Outlined.Build) { navigate(StudioRoutes.SERVICES) }
            V3DrawerRow(ui.about, destination == StudioRoutes.ABOUT, "drawer-about", Icons.Outlined.Info) { navigate(StudioRoutes.ABOUT) }
            V3DrawerRow(if (LocalLayoutDirection.current == LayoutDirection.Rtl) "الإعدادات" else "Settings", destination == V3_SETTINGS, "drawer-settings", Icons.Outlined.Settings) { navigate(V3_SETTINGS) }
            HorizontalDivider(color = c.gold.copy(alpha = 0.65f), modifier = Modifier.padding(vertical = 8.dp))
            Text("Royal Andalus Design System • Tughra Edition", color = c.muted, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun V3DrawerRow(label: String, selected: Boolean, tag: String, icon: ImageVector, click: () -> Unit) {
    val c = LocalV3Palette.current
    NavigationDrawerItem(
        label = { Text(label, fontWeight = FontWeight.SemiBold) },
        selected = selected,
        onClick = click,
        icon = { Icon(icon, null, tint = if (selected) c.goldDark else c.royal) },
        modifier = Modifier.testTag(tag),
    )
}

@Composable
private fun V3BottomBar(destination: String, ui: UiCopy, navigate: (String) -> Unit) {
    val c = LocalV3Palette.current
    NavigationBar(containerColor = c.royalDark) {
        NavigationBarItem(
            selected = destination == StudioRoutes.HOME,
            onClick = { navigate(StudioRoutes.HOME) },
            icon = { Icon(Icons.Outlined.Home, ui.home, tint = if (destination == StudioRoutes.HOME) c.gold else c.ivory) },
            label = { Text(ui.home, color = if (destination == StudioRoutes.HOME) c.gold else c.ivory) },
            modifier = Modifier.testTag("nav-home"),
        )
        NavigationBarItem(
            selected = destination == StudioRoutes.SERVICES,
            onClick = { navigate(StudioRoutes.SERVICES) },
            icon = { Icon(Icons.Outlined.Build, ui.services, tint = if (destination == StudioRoutes.SERVICES) c.gold else c.ivory) },
            label = { Text(ui.services, color = if (destination == StudioRoutes.SERVICES) c.gold else c.ivory) },
            modifier = Modifier.testTag("nav-services"),
        )
        NavigationBarItem(
            selected = destination == StudioRoutes.ABOUT,
            onClick = { navigate(StudioRoutes.ABOUT) },
            icon = { Icon(Icons.Outlined.Info, ui.about, tint = if (destination == StudioRoutes.ABOUT) c.gold else c.ivory) },
            label = { Text(ui.about, color = if (destination == StudioRoutes.ABOUT) c.gold else c.ivory) },
            modifier = Modifier.testTag("nav-about"),
        )
    }
}

@Composable
private fun V3Home(padding: PaddingValues, language: AppLanguage, ui: UiCopy, start: () -> Unit, open: (String) -> Unit) {
    val c = LocalV3Palette.current
    val services = StudioLocalization.services(language)
    AndalusBackdrop {
        Column(
            Modifier.fillMaxSize().testTag("screen-home").padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(Modifier.fillMaxWidth().widthIn(max = 780.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Surface(
                    modifier = Modifier.fillMaxWidth().height(360.dp).border(2.dp, c.gold, RoyalArch),
                    shape = RoyalArch,
                    color = c.royalDark,
                    shadowElevation = 14.dp,
                ) {
                    Box(Modifier.background(Brush.verticalGradient(listOf(c.royal, c.royalDark, Color(0xFF030D2D))))) {
                        Canvas(Modifier.fillMaxSize()) {
                            val center = Offset(size.width / 2f, size.height * 0.36f)
                            repeat(16) { i ->
                                val a = (Math.PI * 2 * i / 16).toFloat()
                                val p = Offset(center.x + cos(a) * 118f, center.y + sin(a) * 118f)
                                drawCircle(c.gold.copy(alpha = 0.17f), 9f, p, style = Stroke(width = 2.5f))
                            }
                            val inset = 22f
                            drawRect(
                                color = c.gold.copy(alpha = 0.34f),
                                topLeft = Offset(inset, size.height * 0.44f),
                                size = androidx.compose.ui.geometry.Size(size.width - inset * 2, size.height * 0.47f),
                                style = Stroke(width = 2f),
                            )
                        }
                        Column(Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 22.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            TughraInspiredMark(Modifier.width(210.dp).height(125.dp))
                            Spacer(Modifier.height(6.dp))
                            Text("Nexvary Andalus Studio", color = c.ivory, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                            Spacer(Modifier.height(8.dp))
                            Text(ui.tagline, color = c.roseSoft, textAlign = TextAlign.Center, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(14.dp))
                            Surface(color = c.gold.copy(alpha = 0.13f), shape = RoundedCornerShape(50.dp), modifier = Modifier.border(1.dp, c.gold, RoundedCornerShape(50.dp))) {
                                Text(if (language.rtl) "هندسة أندلسية • زخارف • واقع معزز" else "Andalusian Architecture • Ornament • AR", color = c.gold, modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                SectionTitle(if (language.rtl) "ابدأ مشروعك" else "Start your project")
                Text(ui.homeIntro, color = c.muted)

                listOf("projects", "plan", "patterns", "ar").chunked(2).forEach { rowKeys ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        rowKeys.forEach { key ->
                            val copy = services.getValue(key)
                            Card(
                                onClick = { open(key) },
                                modifier = Modifier.weight(1f).testTag("quick-$key").border(1.dp, c.gold, RoundedCornerShape(22.dp)),
                                colors = CardDefaults.cardColors(containerColor = c.ivory.copy(alpha = 0.97f)),
                                shape = RoundedCornerShape(22.dp),
                            ) {
                                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                                    Surface(color = c.royalDark, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                                        Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Icon(v3ServiceIcon(key), null, tint = c.gold)
                                            Spacer(Modifier.width(8.dp))
                                            Text(copy.badge, color = c.gold, fontWeight = FontWeight.Black)
                                        }
                                    }
                                    Text(copy.title, color = c.royalDark, fontWeight = FontWeight.Black)
                                    Text(copy.subtitle, color = c.muted, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }

                Surface(color = c.royalDark, shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth().border(1.dp, c.gold, RoundedCornerShape(22.dp))) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        TughraInspiredMark(Modifier.width(90.dp).height(60.dp))
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(if (language.rtl) "مكتبة زخارف محلية" else "Local Ornament Library", color = c.gold, fontWeight = FontWeight.Black)
                            Text(if (language.rtl) "2400 زخرفة • 12 عائلة • تعمل دون إنترنت" else "2,400 ornaments • 12 families • offline", color = c.ivory)
                        }
                    }
                }

                Button(
                    onClick = start,
                    modifier = Modifier.fillMaxWidth().height(54.dp).testTag("home-start-services"),
                    colors = ButtonDefaults.buttonColors(containerColor = c.gold, contentColor = c.royalDark),
                    shape = RoundedCornerShape(20.dp),
                ) { Text(ui.startDesigning, fontWeight = FontWeight.Black) }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    val c = LocalV3Palette.current
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(text, color = c.royalDark, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
        Box(Modifier.width(126.dp).height(3.dp).background(Brush.horizontalGradient(listOf(c.goldDark, c.gold, Color.Transparent))))
    }
}

@Composable
private fun V3Services(padding: PaddingValues, language: AppLanguage, ui: UiCopy, open: (String) -> Unit) {
    val c = LocalV3Palette.current
    val services = StudioLocalization.services(language)
    AndalusBackdrop {
        LazyColumn(
            Modifier.fillMaxSize().testTag("screen-services").padding(padding).padding(horizontal = 14.dp),
            contentPadding = PaddingValues(vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth().widthIn(max = 780.dp).border(2.dp, c.gold, RoundedCornerShape(26.dp)),
                    color = c.royalDark,
                    shape = RoundedCornerShape(26.dp),
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        TughraInspiredMark(Modifier.width(100.dp).height(70.dp))
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            Text(ui.services, color = c.gold, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                            Text(ui.tagline, color = c.roseSoft)
                        }
                    }
                }
            }
            items(StudioRoutes.serviceIds, key = { it }) { key ->
                val copy = services.getValue(key)
                Card(
                    onClick = { open(key) },
                    modifier = Modifier.fillMaxWidth().widthIn(max = 780.dp).testTag("service-$key").border(1.dp, c.gold, RoundedCornerShape(22.dp)),
                    colors = CardDefaults.cardColors(containerColor = c.ivory.copy(alpha = 0.98f)),
                    shape = RoundedCornerShape(22.dp),
                ) {
                    Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Surface(color = c.royalDark, shape = RoundedCornerShape(17.dp)) {
                            Box(Modifier.size(54.dp), contentAlignment = Alignment.Center) { Icon(v3ServiceIcon(key), null, tint = c.gold) }
                        }
                        Column(Modifier.weight(1f)) {
                            Text(copy.title, color = c.royalDark, fontWeight = FontWeight.Black)
                            Text(copy.subtitle, color = c.muted, style = MaterialTheme.typography.bodySmall)
                            Text(copy.badge, color = c.goldDark, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(18.dp)) }
        }
    }
}

private fun v3ServiceIcon(key: String): ImageVector = when (key) {
    "projects" -> Icons.Outlined.Folder
    "ai", "patterns" -> Icons.Outlined.Star
    "exports" -> Icons.Outlined.Share
    "ar" -> Icons.Outlined.Public
    "library" -> Icons.Outlined.Info
    else -> Icons.Outlined.Build
}

@Composable
private fun V3Feature(key: String, padding: PaddingValues, language: AppLanguage, ui: UiCopy, back: () -> Unit) {
    val c = LocalV3Palette.current
    val copy = StudioLocalization.services(language).getValue(key)
    AndalusBackdrop {
        Column(
            Modifier.fillMaxSize().testTag("screen-$key").padding(padding).verticalScroll(rememberScrollState()).padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(Modifier.fillMaxWidth().widthIn(max = 780.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(
                    modifier = Modifier.fillMaxWidth().border(1.dp, c.gold, RoundedCornerShape(24.dp)),
                    color = c.royalDark,
                    shape = RoundedCornerShape(24.dp),
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = c.gold.copy(alpha = 0.15f), shape = RoundedCornerShape(16.dp)) {
                            Box(Modifier.size(50.dp), contentAlignment = Alignment.Center) { Icon(v3ServiceIcon(key), null, tint = c.gold) }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(copy.title, color = c.ivory, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                            Text(copy.badge, color = c.gold, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(onClick = back, modifier = Modifier.testTag("back-button")) {
                            Icon(Icons.AutoMirrored.Outlined.ArrowBack, ui.back, tint = c.gold)
                            Text(" ${ui.back}", color = c.ivory)
                        }
                    }
                }
                Text(copy.subtitle, color = c.muted)
                V3Workspace(key, language, copy)
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun V3Workspace(key: String, language: AppLanguage, copy: ServiceCopy) {
    val c = LocalV3Palette.current
    Card(
        modifier = Modifier.fillMaxWidth().testTag("workspace-$key").border(1.dp, c.gold, RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = c.ivory.copy(alpha = 0.98f)),
        shape = RoundedCornerShape(24.dp),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(copy.title, color = c.royalDark, fontWeight = FontWeight.Black)
            when (key) {
                "projects" -> V3Projects(language)
                "plan" -> V3Plan(language)
                "3d" -> V3Scene(language)
                "patterns" -> V3PatternStudioPro(language)
                "assets", "library" -> V3AssetLibraryPro(language)
                "ar" -> V3ArRoomDesigner(language)
                "exports" -> V3Export(language)
                else -> V3GenericTool(language, key)
            }
        }
    }
}

private fun action(language: AppLanguage, ar: String, en: String): String = if (language.rtl) ar else en

@Composable
private fun V3ActionRow(primary: String, primaryTag: String, onPrimary: () -> Unit, secondary: String, secondaryTag: String, onSecondary: () -> Unit) {
    val c = LocalV3Palette.current
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Button(onClick = onPrimary, modifier = Modifier.weight(1f).testTag(primaryTag), colors = ButtonDefaults.buttonColors(containerColor = c.gold, contentColor = c.royalDark), shape = RoundedCornerShape(16.dp)) { Text(primary, fontWeight = FontWeight.Bold) }
        OutlinedButton(onClick = onSecondary, modifier = Modifier.weight(1f).testTag(secondaryTag), shape = RoundedCornerShape(16.dp)) { Text(secondary, color = c.royalDark, fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun V3Projects(language: AppLanguage) {
    val c = LocalV3Palette.current
    var projects by rememberSaveable { mutableIntStateOf(1) }
    var revisions by rememberSaveable { mutableIntStateOf(1) }
    V3ActionRow(action(language, "مشروع جديد", "New project"), "projects-create", { projects++ }, action(language, "حفظ نسخة", "Save revision"), "projects-save", { revisions++ })
    MetricStrip(action(language, "المشروعات", "Projects"), projects.toString(), "projects-count")
    MetricStrip(action(language, "النسخ", "Revisions"), revisions.toString(), "projects-revisions")
    Text(action(language, "كل مشروع يحتفظ بخيارات اللون والمخطط والزخارف محليًا.", "Each project keeps its palette, plan and ornament choices locally."), color = c.muted)
}

@Composable
private fun MetricStrip(label: String, value: String, tag: String) {
    val c = LocalV3Palette.current
    Surface(color = c.roseSoft, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth().border(1.dp, c.gold.copy(alpha = 0.55f), RoundedCornerShape(14.dp))) {
        Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = c.royalDark, fontWeight = FontWeight.Bold)
            Text(value, color = c.goldDark, fontWeight = FontWeight.Black, modifier = Modifier.testTag(tag))
        }
    }
}

@Composable
private fun V3Plan(language: AppLanguage) {
    val c = LocalV3Palette.current
    var rooms by rememberSaveable { mutableIntStateOf(2) }
    var arches by rememberSaveable { mutableIntStateOf(1) }
    V3ActionRow(action(language, "أضف غرفة", "Add room"), "plan-add-room", { rooms = (rooms + 1).coerceAtMost(8) }, action(language, "أضف قوس", "Add arch"), "plan-add-arch", { arches = (arches + 1).coerceAtMost(8) })
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("${action(language, "غرف", "Rooms")}: $rooms", color = c.royalDark, modifier = Modifier.testTag("plan-rooms-count"), fontWeight = FontWeight.Bold)
        Text("${action(language, "أقواس", "Arches")}: $arches", color = c.goldDark, modifier = Modifier.testTag("plan-arches-count"), fontWeight = FontWeight.Bold)
    }
    Canvas(Modifier.fillMaxWidth().height(230.dp).background(c.roseSoft, RoundedCornerShape(20.dp)).border(1.dp, c.gold, RoundedCornerShape(20.dp)).testTag("plan-canvas")) {
        val margin = 28f
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
                moveTo(x - 28f, y)
                quadraticBezierTo(x, y - 48f, x + 28f, y)
            }
            drawPath(arch, c.goldDark, style = Stroke(width = 7f))
        }
    }
}

@Composable
private fun V3Scene(language: AppLanguage) {
    val c = LocalV3Palette.current
    var angle by rememberSaveable { mutableIntStateOf(30) }
    var floors by rememberSaveable { mutableIntStateOf(2) }
    V3ActionRow(action(language, "تدوير المشهد", "Rotate scene"), "3d-rotate", { angle = (angle + 15) % 360 }, action(language, "أضف طابق", "Add floor"), "3d-add-floor", { floors = (floors + 1).coerceAtMost(6) })
    Text("${action(language, "الزاوية", "Angle")}: $angle°", color = c.royalDark, fontWeight = FontWeight.Black, modifier = Modifier.testTag("3d-angle"))
    Canvas(Modifier.fillMaxWidth().height(240.dp).background(c.royalDark, RoundedCornerShape(22.dp)).border(1.dp, c.gold, RoundedCornerShape(22.dp)).testTag("3d-canvas")) {
        val cx = size.width / 2f
        val baseY = size.height * 0.8f
        val scale = 1f + floors * 0.06f
        val rotation = Math.toRadians(angle.toDouble())
        val dx = (cos(rotation) * 72f * scale).toFloat()
        val dy = (sin(rotation) * 32f).toFloat()
        repeat(floors) { floor ->
            val y = baseY - floor * 48f
            val p = Path().apply {
                moveTo(cx - 95f, y)
                lineTo(cx + 95f, y)
                lineTo(cx + 95f + dx, y - 34f + dy)
                lineTo(cx - 95f + dx, y - 34f + dy)
                close()
            }
            drawPath(p, c.rose.copy(alpha = 0.2f))
            drawPath(p, c.gold, style = Stroke(width = 4f))
        }
        val arch = Path().apply {
            moveTo(cx - 38f, baseY)
            quadraticBezierTo(cx, baseY - 86f, cx + 38f, baseY)
        }
        drawPath(arch, c.roseSoft, style = Stroke(width = 7f))
    }
}

@Composable
private fun V3Export(language: AppLanguage) {
    val c = LocalV3Palette.current
    var count by rememberSaveable { mutableIntStateOf(0) }
    Button(onClick = { count++ }, modifier = Modifier.fillMaxWidth().testTag("exports-generate"), colors = ButtonDefaults.buttonColors(containerColor = c.gold, contentColor = c.royalDark), shape = RoundedCornerShape(18.dp)) {
        Text(action(language, "تجهيز حزمة التصدير", "Prepare export package"), fontWeight = FontWeight.Black)
    }
    Surface(color = c.royalDark, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Text("${action(language, "حزمة جاهزة", "Package ready")} #$count", color = c.gold, modifier = Modifier.padding(14.dp).testTag("exports-status"), fontWeight = FontWeight.Black)
    }
}

@Composable
private fun V3GenericTool(language: AppLanguage, key: String) {
    val c = LocalV3Palette.current
    var value by rememberSaveable(key) { mutableIntStateOf(0) }
    val (primary, secondary) = when (key) {
        "ai" -> action(language, "توليد تصور", "Generate concept") to action(language, "تثبيت العناصر", "Lock elements")
        "materials" -> action(language, "إضافة خامة", "Add material") to action(language, "مقارنة", "Compare")
        else -> action(language, "تنفيذ", "Run") to action(language, "إعادة", "Reset")
    }
    V3ActionRow(primary, "$key-primary", { value++ }, secondary, "$key-secondary", { value = (value + 1) % 10 })
    MetricStrip(action(language, "التغييرات", "Changes"), value.toString(), "$key-state")
    Text(action(language, "هذه الأداة متصلة بحالة المشروع المحلية وتتفاعل فورًا مع أوامرك.", "This tool is connected to local project state and reacts immediately to your commands."), color = c.muted)
}

@Composable
private fun V3About(padding: PaddingValues, ui: UiCopy) {
    val c = LocalV3Palette.current
    val uri = LocalUriHandler.current
    AndalusBackdrop {
        Column(Modifier.fillMaxSize().testTag("screen-about").padding(padding).verticalScroll(rememberScrollState()).padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Column(Modifier.fillMaxWidth().widthIn(max = 780.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(shape = RoyalArch, color = c.royalDark, modifier = Modifier.fillMaxWidth().height(270.dp).border(2.dp, c.gold, RoyalArch)) {
                    Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        TughraInspiredMark(Modifier.width(190.dp).height(115.dp))
                        Text(ui.aboutTitle, color = c.ivory, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                    }
                }
                Text(ui.aboutBody, color = c.ink)
                SectionTitle(ui.contactTitle)
                V3LinkButton(ui.website, "social-website") { uri.openUri(NexvaryLinks.WEBSITE) }
                V3LinkButton(ui.facebook, "social-facebook") { uri.openUri(NexvaryLinks.FACEBOOK) }
                V3LinkButton(ui.email, "social-email") { uri.openUri(NexvaryLinks.EMAIL) }
                V3LinkButton(ui.youtube, "social-youtube") { uri.openUri(NexvaryLinks.YOUTUBE) }
                V3LinkButton(ui.x, "social-x") { uri.openUri(NexvaryLinks.X) }
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun V3LinkButton(label: String, tag: String, click: () -> Unit) {
    val c = LocalV3Palette.current
    OutlinedButton(onClick = click, modifier = Modifier.fillMaxWidth().height(52.dp).testTag(tag), shape = RoundedCornerShape(18.dp)) {
        Icon(if (tag == "social-email") Icons.Outlined.Email else Icons.Outlined.Public, null, tint = c.goldDark)
        Spacer(Modifier.width(8.dp))
        Text(label, color = c.royalDark, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun V3Settings(
    padding: PaddingValues,
    language: AppLanguage,
    ui: UiCopy,
    paletteId: String,
    onPalette: (String) -> Unit,
    onLanguage: (AppLanguage) -> Unit,
    onBack: () -> Unit,
) {
    val c = LocalV3Palette.current
    AndalusBackdrop {
        Column(Modifier.fillMaxSize().testTag("screen-settings").padding(padding).verticalScroll(rememberScrollState()).padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Column(Modifier.fillMaxWidth().widthIn(max = 780.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SectionTitle(if (language.rtl) "إعدادات الهوية" else "Identity settings")
                    Spacer(Modifier.weight(1f))
                    OutlinedButton(onClick = onBack, modifier = Modifier.testTag("settings-back")) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, ui.back)
                        Text(" ${ui.back}")
                    }
                }
                Surface(color = c.ivory, shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth().border(1.dp, c.gold, RoundedCornerShape(22.dp))) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(if (language.rtl) "اختر هوية التطبيق" else "Choose app identity", color = c.royalDark, fontWeight = FontWeight.Black)
                        Text(V3Palettes.firstOrNull { it.id == paletteId }?.title ?: RoyalRose.title, color = c.goldDark, fontWeight = FontWeight.Black, modifier = Modifier.testTag("theme-current"))
                        V3Palettes.forEach { p ->
                            OutlinedButton(onClick = { onPalette(p.id) }, modifier = Modifier.fillMaxWidth().testTag("theme-${p.id}"), shape = RoundedCornerShape(16.dp)) {
                                Box(Modifier.size(18.dp).background(p.rose, RoundedCornerShape(50.dp)).border(1.dp, p.gold, RoundedCornerShape(50.dp)))
                                Spacer(Modifier.width(8.dp))
                                Text(p.title, color = c.royalDark)
                            }
                        }
                    }
                }
                Surface(color = c.ivory, shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth().border(1.dp, c.gold, RoundedCornerShape(22.dp))) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(ui.language, color = c.royalDark, fontWeight = FontWeight.Black)
                        AppLanguage.entries.chunked(2).forEach { pair ->
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                pair.forEach { item ->
                                    TextButton(onClick = { onLanguage(item) }, modifier = Modifier.weight(1f)) {
                                        Text(item.nativeName, color = if (item == language) c.goldDark else c.royalDark)
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}
