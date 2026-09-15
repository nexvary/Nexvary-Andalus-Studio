package com.nexvary.andalus

import android.Manifest
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

private val ProRoyal = Color(0xFF081B48)
private val ProRoyal2 = Color(0xFF123D88)
private val ProGold = Color(0xFFD4AF37)
private val ProGoldDeep = Color(0xFFA97916)
private val ProIvory = Color(0xFFFFFBF3)
private val ProRose = Color(0xFFF7DDE6)
private val ProInk = Color(0xFF2B2430)

internal data class OrnamentAsset(
    val id: Int,
    val family: String,
    val arName: String,
    val enName: String,
    val seed: Int,
)

private val ornamentFamilies = listOf(
    Triple("zellige", "زليج مغربي", "Moroccan Zellige"),
    Triple("girih", "جيريه هندسي", "Girih Geometry"),
    Triple("rosette", "روسيات أندلسية", "Andalusian Rosettes"),
    Triple("star", "نجوم إسلامية", "Islamic Stars"),
    Triple("arabesque", "أرابيسك نباتي", "Floral Arabesque"),
    Triple("muqarnas", "مقرنصات", "Muqarnas"),
    Triple("arch", "أقواس أندلسية", "Andalusian Arches"),
    Triple("border", "أشرطة وحدود", "Borders & Bands"),
    Triple("mashrabiya", "مشربيات", "Mashrabiya"),
    Triple("mosaic", "فسيفساء", "Mosaic"),
    Triple("ceiling", "زخارف أسقف", "Ceiling Ornament"),
    Triple("calligraphy", "حليات خطية", "Calligraphic Ornament"),
)

/** 2,400 deterministic local ornaments: 12 families × 200 variants. */
internal val LocalOrnamentCatalog: List<OrnamentAsset> by lazy {
    buildList(2400) {
        var id = 1
        ornamentFamilies.forEachIndexed { familyIndex, family ->
            repeat(200) { variant ->
                add(
                    OrnamentAsset(
                        id = id++,
                        family = family.first,
                        arName = "${family.second} ${variant + 1}",
                        enName = "${family.third} ${variant + 1}",
                        seed = familyIndex * 211 + variant,
                    ),
                )
            }
        }
    }
}

internal fun ornamentsForFamily(family: String): List<OrnamentAsset> =
    if (family == "all") LocalOrnamentCatalog else LocalOrnamentCatalog.filter { it.family == family }

/**
 * Kept under the old function name so existing callers remain stable, but the mark is now
 * an explicitly Andalusian horseshoe arch with geometric/floral ornament instead of a tughra-like glyph.
 */
@Composable
internal fun TughraInspiredMark(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val base = h * 0.88f
        val left = w * 0.24f
        val right = w * 0.76f
        val spring = h * 0.57f

        // Horseshoe/Andalusian arch silhouette.
        val outer = Path().apply {
            moveTo(left, base)
            lineTo(left, spring)
            cubicTo(left, h * 0.24f, w * 0.34f, h * 0.08f, cx, h * 0.08f)
            cubicTo(w * 0.66f, h * 0.08f, right, h * 0.24f, right, spring)
            cubicTo(right, h * 0.72f, w * 0.67f, h * 0.78f, w * 0.61f, h * 0.72f)
            cubicTo(w * 0.70f, h * 0.57f, w * 0.67f, h * 0.28f, cx, h * 0.25f)
            cubicTo(w * 0.33f, h * 0.28f, w * 0.30f, h * 0.57f, w * 0.39f, h * 0.72f)
            cubicTo(w * 0.33f, h * 0.78f, left, h * 0.72f, left, spring)
            lineTo(left, base)
            close()
        }
        drawPath(outer, ProGold)
        drawPath(outer, ProRoyal2, style = Stroke(width = 3f))

        // Inner ivory/gold arch line.
        val inner = Path().apply {
            moveTo(w * 0.31f, base)
            lineTo(w * 0.31f, spring)
            cubicTo(w * 0.31f, h * 0.34f, w * 0.39f, h * 0.18f, cx, h * 0.18f)
            cubicTo(w * 0.61f, h * 0.18f, w * 0.69f, h * 0.34f, w * 0.69f, spring)
            cubicTo(w * 0.69f, h * 0.66f, w * 0.63f, h * 0.70f, w * 0.58f, h * 0.66f)
            cubicTo(w * 0.64f, h * 0.52f, w * 0.60f, h * 0.33f, cx, h * 0.32f)
            cubicTo(w * 0.40f, h * 0.33f, w * 0.36f, h * 0.52f, w * 0.42f, h * 0.66f)
            cubicTo(w * 0.37f, h * 0.70f, w * 0.31f, h * 0.66f, w * 0.31f, spring)
        }
        drawPath(inner, ProIvory, style = Stroke(width = 4f))
        drawPath(inner, ProGoldDeep, style = Stroke(width = 2f))

        // Rosette suspended in the arch.
        drawRosette(center = Offset(cx, h * 0.42f), radius = h * 0.12f, petals = 12, seed = 3)

        // Floral vines on both sides.
        fun vine(mirror: Float) {
            val startX = if (mirror < 0) w * 0.20f else w * 0.80f
            val endX = if (mirror < 0) w * 0.34f else w * 0.66f
            val vinePath = Path().apply {
                moveTo(startX, h * 0.82f)
                cubicTo(startX + mirror * w * 0.03f, h * 0.68f, endX - mirror * w * 0.08f, h * 0.64f, endX, h * 0.53f)
            }
            drawPath(vinePath, ProGold, style = Stroke(width = 4f))
            listOf(0.62f, 0.72f).forEachIndexed { index, y ->
                val x = if (mirror < 0) w * (0.25f + index * 0.04f) else w * (0.75f - index * 0.04f)
                drawCircle(ProGold, h * 0.035f, Offset(x, h * y))
                drawCircle(ProRoyal2, h * 0.018f, Offset(x, h * y))
            }
        }
        vine(-1f)
        vine(1f)

        // Decorated columns.
        listOf(left, right).forEach { x ->
            drawRoundRect(
                ProGold,
                topLeft = Offset(x - w * 0.035f, h * 0.61f),
                size = Size(w * 0.07f, h * 0.27f),
                cornerRadius = CornerRadius(5f, 5f),
            )
            repeat(4) { i ->
                drawCircle(ProRoyal2, h * 0.014f, Offset(x, h * (0.66f + i * 0.055f)))
            }
        }
    }
}

@Composable
internal fun V3PatternStudioPro(language: AppLanguage) {
    var family by remember { mutableStateOf("all") }
    var page by remember { mutableStateOf(0) }
    val filtered = remember(family) { ornamentsForFamily(family) }
    val visible = filtered.drop(page * 12).take(12)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ProSectionTitle(if (language.rtl) "مكتبة الزخارف الاحترافية" else "Professional Ornament Library")
        Text(
            if (language.rtl) "٢٤٠٠ زخرفة محلية قابلة للاستخدام دون إنترنت • 12 عائلة زخرفية"
            else "2,400 offline ornament variants • 12 design families",
            color = ProInk,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.testTag("ornament-count"),
        )
        Text(
            text = familyLabel(language, family),
            color = ProRoyal,
            fontWeight = FontWeight.Black,
            modifier = Modifier.testTag("ornament-selected-family"),
        )
        FamilyFilter(language, family) {
            family = it
            page = 0
        }
        visible.chunked(3).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { asset ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, ProGold, RoundedCornerShape(16.dp))
                            .testTag("ornament-${asset.id}"),
                        colors = CardDefaults.cardColors(containerColor = ProIvory),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            OrnamentPreview(asset, Modifier.fillMaxWidth().height(78.dp))
                            Text(
                                if (language.rtl) asset.arName else asset.enName,
                                color = ProRoyal,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                                modifier = Modifier.testTag("ornament-name-${asset.id}"),
                            )
                        }
                    }
                }
                repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
                onClick = { page = (page - 1).coerceAtLeast(0) },
                modifier = Modifier.weight(1f).testTag("ornament-prev"),
            ) { Text(if (language.rtl) "السابق" else "Previous") }
            Button(
                onClick = {
                    val maxPage = ((filtered.size - 1).coerceAtLeast(0) / 12)
                    page = if (page >= maxPage) 0 else page + 1
                },
                modifier = Modifier.weight(1f).testTag("ornament-next"),
                colors = ButtonDefaults.buttonColors(containerColor = ProGold, contentColor = ProRoyal),
            ) { Text(if (language.rtl) "المزيد" else "More", fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
internal fun V3AssetLibraryPro(language: AppLanguage) {
    var query by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf<OrnamentAsset?>(null) }
    val results = remember(query) {
        if (query.isBlank()) LocalOrnamentCatalog.take(24)
        else LocalOrnamentCatalog.filter {
            it.arName.contains(query, ignoreCase = true) ||
                it.enName.contains(query, ignoreCase = true) ||
                it.family.contains(query, ignoreCase = true)
        }.take(24)
    }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ProSectionTitle(if (language.rtl) "مكتبة الأندلس — 2400 أصل زخرفي" else "Al-Andalus Library — 2,400 Ornament Assets")
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth().testTag("library-search"),
            leadingIcon = { Icon(Icons.Outlined.Search, null) },
            label = { Text(if (language.rtl) "بحث: زليج، نجمة، قوس، أرابيسك..." else "Search zellige, stars, arches, arabesque...") },
            singleLine = true,
        )
        results.chunked(4).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                row.forEach { asset ->
                    Surface(
                        onClick = { selected = asset },
                        modifier = Modifier
                            .weight(1f)
                            .height(92.dp)
                            .border(1.dp, if (selected?.id == asset.id) ProRoyal2 else ProGold, RoundedCornerShape(14.dp))
                            .testTag("library-asset-${asset.id}"),
                        color = ProIvory,
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        OrnamentPreview(asset, Modifier.padding(5.dp).fillMaxSize())
                    }
                }
                repeat(4 - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
        selected?.let { asset ->
            Surface(color = ProRoyal, shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    OrnamentPreview(asset, Modifier.size(86.dp))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(if (language.rtl) asset.arName else asset.enName, color = ProIvory, fontWeight = FontWeight.Black)
                        Text("ID ${asset.id} • ${asset.family}", color = ProGold)
                        Text(if (language.rtl) "جاهز للإدراج في مشروع أو صورة غرفة" else "Ready for room/project placement", color = ProIvory)
                    }
                }
            }
        }
    }
}

private data class PlacedOrnament(val asset: OrnamentAsset, val x: Float, val y: Float)

@Composable
internal fun V3ArRoomDesigner(language: AppLanguage) {
    var roomBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var selected by remember { mutableStateOf(LocalOrnamentCatalog.first()) }
    var roomFamily by remember { mutableStateOf("all") }
    val placed = remember { mutableStateListOf<PlacedOrnament>() }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) roomBitmap = bitmap
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) cameraLauncher.launch(null)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ProSectionTitle(if (language.rtl) "مصمم الغرفة بالسحب والإفلات" else "Room Designer — Drag & Drop")
        Text(
            if (language.rtl) "صوّر الغرفة، اختر عائلة زخرفية وعنصرًا، ثم أضفه واسحبه فوق الصورة."
            else "Capture the room, choose a family and ornament, then place and drag it over the photo.",
            color = ProInk,
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                modifier = Modifier.weight(1f).testTag("room-camera"),
                colors = ButtonDefaults.buttonColors(containerColor = ProGold, contentColor = ProRoyal),
            ) {
                Icon(Icons.Outlined.CameraAlt, null)
                Spacer(Modifier.width(6.dp))
                Text(if (language.rtl) "تصوير الغرفة" else "Capture room", fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
                onClick = { placed.clear() },
                modifier = Modifier.weight(1f).testTag("room-clear"),
            ) {
                Icon(Icons.Outlined.DeleteSweep, null)
                Spacer(Modifier.width(6.dp))
                Text(if (language.rtl) "مسح العناصر" else "Clear")
            }
        }

        Box(
            Modifier
                .fillMaxWidth()
                .height(360.dp)
                .background(ProRoyal, RoundedCornerShape(22.dp))
                .border(2.dp, ProGold, RoundedCornerShape(22.dp))
                .testTag("room-workspace"),
        ) {
            val bitmap = roomBitmap
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Canvas(Modifier.fillMaxSize()) {
                    drawRect(Color(0xFFEEE6DD))
                    drawLine(ProRoyal2, Offset(size.width * 0.12f, size.height * 0.18f), Offset(size.width * 0.12f, size.height * 0.88f), 4f)
                    drawLine(ProRoyal2, Offset(size.width * 0.88f, size.height * 0.18f), Offset(size.width * 0.88f, size.height * 0.88f), 4f)
                    drawLine(ProGoldDeep, Offset(size.width * 0.12f, size.height * 0.88f), Offset(size.width * 0.88f, size.height * 0.88f), 5f)
                    drawAndalusArch(Offset(size.width * 0.50f, size.height * 0.62f), size.minDimension * 0.22f, 0)
                }
                Text(
                    if (language.rtl) "معاينة الغرفة — التقط صورة حقيقية للبدء" else "Room preview — capture a real photo to begin",
                    color = ProRoyal,
                    modifier = Modifier.align(Alignment.TopCenter).padding(14.dp),
                    fontWeight = FontWeight.Bold,
                )
            }

            placed.forEach { item ->
                val itemKey = item.asset.id
                Box(
                    Modifier
                        .offset { IntOffset(item.x.roundToInt(), item.y.roundToInt()) }
                        .size(92.dp)
                        .background(ProIvory.copy(alpha = 0.90f), RoundedCornerShape(16.dp))
                        .border(2.dp, ProGold, RoundedCornerShape(16.dp))
                        .pointerInput(itemKey, item.x, item.y) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val index = placed.indexOfFirst { it === item }
                                if (index >= 0) {
                                    placed[index] = item.copy(x = item.x + dragAmount.x, y = item.y + dragAmount.y)
                                }
                            }
                        }
                        .testTag("placed-${item.asset.id}"),
                ) {
                    OrnamentPreview(item.asset, Modifier.fillMaxSize().padding(5.dp))
                }
            }
        }

        Text(if (language.rtl) "اختر نوع الزخرفة" else "Choose ornament family", color = ProRoyal, fontWeight = FontWeight.Black)
        FamilyFilter(language, roomFamily) { family ->
            roomFamily = family
            selected = ornamentsForFamily(family).firstOrNull() ?: LocalOrnamentCatalog.first()
        }
        Text(if (language.rtl) "اسحب الزخرفة داخل الصورة بعد إضافتها" else "Drag any placed ornament directly on the image", color = ProRoyal, fontWeight = FontWeight.Bold)
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ornamentsForFamily(roomFamily).take(18).forEach { asset ->
                Surface(
                    onClick = { selected = asset },
                    modifier = Modifier
                        .size(76.dp)
                        .border(2.dp, if (selected.id == asset.id) ProRoyal2 else ProGold, RoundedCornerShape(14.dp))
                        .testTag("room-asset-${asset.id}"),
                    color = ProIvory,
                    shape = RoundedCornerShape(14.dp),
                ) {
                    OrnamentPreview(asset, Modifier.padding(4.dp).fillMaxSize())
                }
            }
        }
        Button(
            onClick = {
                placed.add(
                    PlacedOrnament(
                        asset = selected,
                        x = 70f + (placed.size % 4) * 34f,
                        y = 90f + (placed.size % 3) * 42f,
                    ),
                )
            },
            modifier = Modifier.fillMaxWidth().testTag("room-add-ornament"),
            colors = ButtonDefaults.buttonColors(containerColor = ProRoyal, contentColor = ProIvory),
            shape = RoundedCornerShape(18.dp),
        ) {
            Text(if (language.rtl) "أضف: ${selected.arName}" else "Add: ${selected.enName}", fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun FamilyFilter(language: AppLanguage, selected: String, onSelect: (String) -> Unit) {
    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        val options = listOf(Triple("all", "الكل", "All")) + ornamentFamilies
        options.forEach { family ->
            val modifier = Modifier.testTag("ornament-filter-${family.first}")
            if (selected == family.first) {
                Button(
                    onClick = { onSelect(family.first) },
                    modifier = modifier,
                    colors = ButtonDefaults.buttonColors(containerColor = ProRoyal, contentColor = ProIvory),
                ) { Text(if (language.rtl) family.second else family.third) }
            } else {
                OutlinedButton(onClick = { onSelect(family.first) }, modifier = modifier) {
                    Text(if (language.rtl) family.second else family.third, color = ProRoyal)
                }
            }
        }
    }
}

private fun familyLabel(language: AppLanguage, family: String): String {
    if (family == "all") return if (language.rtl) "كل العائلات" else "All families"
    val item = ornamentFamilies.firstOrNull { it.first == family }
    return if (item == null) family else if (language.rtl) item.second else item.third
}

@Composable
private fun ProSectionTitle(text: String) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(text, color = ProRoyal, fontWeight = FontWeight.Black)
        Box(Modifier.width(128.dp).height(3.dp).background(ProGold))
    }
}

@Composable
private fun OrnamentPreview(asset: OrnamentAsset, modifier: Modifier = Modifier) {
    Canvas(modifier.testTag("ornament-preview-${asset.id}")) {
        drawRoundRect(
            color = ProRose.copy(alpha = 0.55f),
            topLeft = Offset.Zero,
            size = size,
            cornerRadius = CornerRadius(size.minDimension * 0.13f),
        )
        when (asset.family) {
            "zellige" -> drawZellige(asset.seed)
            "girih" -> drawGirih(asset.seed)
            "rosette" -> drawRosette(Offset(size.width / 2f, size.height / 2f), size.minDimension * 0.30f, 10 + asset.seed % 4, asset.seed)
            "star" -> drawStarOrnament(asset.seed)
            "arabesque" -> drawArabesque(asset.seed)
            "muqarnas" -> drawMuqarnas(asset.seed)
            "arch" -> drawAndalusArch(Offset(size.width / 2f, size.height * 0.56f), size.minDimension * 0.34f, asset.seed)
            "border" -> drawBorderBand(asset.seed)
            "mashrabiya" -> drawMashrabiya(asset.seed)
            "mosaic" -> drawMosaic(asset.seed)
            "ceiling" -> drawCeiling(asset.seed)
            "calligraphy" -> drawCalligraphicFlourish(asset.seed)
            else -> drawStarOrnament(asset.seed)
        }
    }
}

private fun DrawScope.drawStarOrnament(seed: Int) {
    val center = Offset(size.width / 2f, size.height / 2f)
    val radius = size.minDimension * 0.31f
    val points = 8 + (seed % 5) * 2
    val path = starPath(center, radius, points, 0.44f + (seed % 3) * 0.08f)
    drawPath(path, if (seed % 2 == 0) ProRoyal2 else ProGoldDeep)
    drawPath(path, ProGold, style = Stroke(width = 3f))
    drawCircle(ProIvory, radius * 0.22f, center)
    drawCircle(ProGoldDeep, radius * 0.10f, center)
}

private fun DrawScope.drawZellige(seed: Int) {
    val cell = size.minDimension / 4.2f
    repeat(4) { row ->
        repeat(5) { col ->
            val cx = col * cell + cell * 0.45f + if (row % 2 == 0) 0f else cell * 0.5f
            val cy = row * cell + cell * 0.45f
            val p = Path().apply {
                moveTo(cx, cy - cell * 0.38f)
                lineTo(cx + cell * 0.38f, cy)
                lineTo(cx, cy + cell * 0.38f)
                lineTo(cx - cell * 0.38f, cy)
                close()
            }
            drawPath(p, if ((row + col + seed) % 2 == 0) ProRoyal2 else ProIvory)
            drawPath(p, ProGold, style = Stroke(width = 2f))
        }
    }
}

private fun DrawScope.drawGirih(seed: Int) {
    val center = Offset(size.width / 2f, size.height / 2f)
    val r1 = size.minDimension * 0.34f
    val r2 = r1 * 0.58f
    val outer = regularPolygon(center, r1, 10, -PI / 2)
    val inner = regularPolygon(center, r2, 5, -PI / 2 + (seed % 5) * 0.08)
    drawPath(outer, ProIvory)
    drawPath(outer, ProGoldDeep, style = Stroke(width = 3f))
    drawPath(inner, ProRoyal2, style = Stroke(width = 4f))
    repeat(5) { i ->
        val a = (-PI / 2 + 2 * PI * i / 5).toFloat()
        val p1 = Offset(center.x + cos(a) * r2, center.y + sin(a) * r2)
        val p2 = Offset(center.x + cos(a + PI.toFloat()) * r1, center.y + sin(a + PI.toFloat()) * r1)
        drawLine(ProGold, p1, p2, 2f)
    }
}

private fun DrawScope.drawRosette(center: Offset, radius: Float, petals: Int, seed: Int) {
    repeat(petals) { i ->
        val a = (2.0 * PI * i / petals).toFloat()
        val petalCenter = Offset(center.x + cos(a) * radius * 0.62f, center.y + sin(a) * radius * 0.62f)
        drawCircle(if ((i + seed) % 2 == 0) ProGold else ProRoyal2, radius * 0.30f, petalCenter)
        drawCircle(ProIvory, radius * 0.18f, petalCenter)
    }
    drawCircle(ProGoldDeep, radius * 0.36f, center)
    drawCircle(ProIvory, radius * 0.21f, center)
    drawCircle(ProRoyal2, radius * 0.10f, center)
}

private fun DrawScope.drawArabesque(seed: Int) {
    val baseY = size.height * 0.73f
    val vine = Path().apply {
        moveTo(size.width * 0.08f, baseY)
        cubicTo(size.width * 0.22f, size.height * 0.28f, size.width * 0.43f, size.height * 0.83f, size.width * 0.52f, size.height * 0.42f)
        cubicTo(size.width * 0.60f, size.height * 0.10f, size.width * 0.79f, size.height * 0.48f, size.width * 0.92f, size.height * 0.23f)
    }
    drawPath(vine, ProGoldDeep, style = Stroke(width = 5f))
    repeat(5) { i ->
        val x = size.width * (0.20f + i * 0.15f)
        val y = size.height * (if ((i + seed) % 2 == 0) 0.48f else 0.58f)
        drawCircle(ProGold, size.minDimension * 0.085f, Offset(x, y))
        drawCircle(ProRoyal2, size.minDimension * 0.048f, Offset(x, y))
        val leaf = Path().apply {
            moveTo(x, y)
            quadraticBezierTo(x + size.width * 0.08f, y - size.height * 0.09f, x + size.width * 0.11f, y + size.height * 0.02f)
            quadraticBezierTo(x + size.width * 0.05f, y + size.height * 0.06f, x, y)
        }
        drawPath(leaf, ProRoyal2)
        drawPath(leaf, ProGold, style = Stroke(width = 2f))
    }
}

private fun DrawScope.drawMuqarnas(seed: Int) {
    val rows = 4
    val top = size.height * 0.12f
    val rowH = size.height * 0.17f
    repeat(rows) { row ->
        val cells = row + 3
        val cellW = size.width * 0.82f / cells
        val startX = size.width * 0.09f
        repeat(cells) { col ->
            val x = startX + col * cellW
            val y = top + row * rowH
            val p = Path().apply {
                moveTo(x, y)
                lineTo(x + cellW, y)
                lineTo(x + cellW * 0.74f, y + rowH * 0.72f)
                lineTo(x + cellW * 0.50f, y + rowH)
                lineTo(x + cellW * 0.26f, y + rowH * 0.72f)
                close()
            }
            drawPath(p, if ((row + col + seed) % 2 == 0) ProGold else ProRoyal2)
            drawPath(p, ProIvory, style = Stroke(width = 2f))
        }
    }
}

private fun DrawScope.drawAndalusArch(center: Offset, radius: Float, seed: Int) {
    val left = center.x - radius * 0.62f
    val right = center.x + radius * 0.62f
    val baseY = center.y + radius * 0.88f
    val springY = center.y + radius * 0.12f
    val arch = Path().apply {
        moveTo(left, baseY)
        lineTo(left, springY)
        cubicTo(left, center.y - radius * 0.70f, center.x - radius * 0.36f, center.y - radius, center.x, center.y - radius)
        cubicTo(center.x + radius * 0.36f, center.y - radius, right, center.y - radius * 0.70f, right, springY)
        cubicTo(right, center.y + radius * 0.46f, center.x + radius * 0.36f, center.y + radius * 0.50f, center.x + radius * 0.28f, center.y + radius * 0.36f)
        cubicTo(center.x + radius * 0.44f, center.y + radius * 0.02f, center.x + radius * 0.30f, center.y - radius * 0.45f, center.x, center.y - radius * 0.49f)
        cubicTo(center.x - radius * 0.30f, center.y - radius * 0.45f, center.x - radius * 0.44f, center.y + radius * 0.02f, center.x - radius * 0.28f, center.y + radius * 0.36f)
        cubicTo(center.x - radius * 0.36f, center.y + radius * 0.50f, left, center.y + radius * 0.46f, left, springY)
        close()
    }
    drawPath(arch, if (seed % 2 == 0) ProGold else ProGoldDeep)
    drawPath(arch, ProRoyal2, style = Stroke(width = 3f))
    drawRosette(Offset(center.x, center.y - radius * 0.17f), radius * 0.22f, 8 + seed % 5, seed)
}

private fun DrawScope.drawBorderBand(seed: Int) {
    val top = size.height * 0.30f
    val bottom = size.height * 0.70f
    drawRoundRect(ProRoyal2, Offset(size.width * 0.04f, top), Size(size.width * 0.92f, bottom - top), CornerRadius(10f))
    drawRoundRect(ProGold, Offset(size.width * 0.04f, top), Size(size.width * 0.92f, bottom - top), CornerRadius(10f), style = Stroke(width = 3f))
    repeat(7) { i ->
        val x = size.width * (0.11f + i * 0.13f)
        val p = starPath(Offset(x, size.height * 0.5f), size.minDimension * 0.095f, 6 + seed % 3, 0.48f)
        drawPath(p, ProIvory)
        drawPath(p, ProGold, style = Stroke(width = 1.8f))
    }
}

private fun DrawScope.drawMashrabiya(seed: Int) {
    val step = size.minDimension * (0.20f + (seed % 3) * 0.02f)
    var y = step * 0.45f
    var row = 0
    while (y < size.height) {
        var x = step * 0.45f + if (row % 2 == 0) 0f else step * 0.5f
        while (x < size.width) {
            val diamond = Path().apply {
                moveTo(x, y - step * 0.32f)
                lineTo(x + step * 0.32f, y)
                lineTo(x, y + step * 0.32f)
                lineTo(x - step * 0.32f, y)
                close()
            }
            drawPath(diamond, ProRoyal2, style = Stroke(width = 3f))
            drawCircle(ProGold, step * 0.075f, Offset(x, y))
            x += step
        }
        y += step
        row++
    }
}

private fun DrawScope.drawMosaic(seed: Int) {
    val step = size.minDimension * 0.23f
    repeat(5) { row ->
        repeat(6) { col ->
            val x = col * step + if (row % 2 == 0) 0f else step * 0.5f
            val y = row * step * 0.82f
            val center = Offset(x, y)
            drawCircle(if ((row + col + seed) % 3 == 0) ProGold else ProRoyal2, step * 0.31f, center)
            drawCircle(ProIvory, step * 0.16f, center)
        }
    }
}

private fun DrawScope.drawCeiling(seed: Int) {
    val center = Offset(size.width / 2f, size.height / 2f)
    val outer = size.minDimension * 0.37f
    repeat(3) { ring ->
        val r = outer * (1f - ring * 0.24f)
        val petals = 12 - ring * 2 + seed % 2
        val p = starPath(center, r, petals, 0.68f)
        drawPath(p, if (ring % 2 == 0) ProGold else ProRoyal2, style = Stroke(width = if (ring == 0) 4f else 3f))
    }
    drawRosette(center, outer * 0.37f, 10 + seed % 4, seed)
}

private fun DrawScope.drawCalligraphicFlourish(seed: Int) {
    val p1 = Path().apply {
        moveTo(size.width * 0.09f, size.height * 0.67f)
        cubicTo(size.width * 0.20f, size.height * 0.22f, size.width * 0.47f, size.height * 0.29f, size.width * 0.44f, size.height * 0.61f)
        cubicTo(size.width * 0.41f, size.height * 0.85f, size.width * 0.18f, size.height * 0.84f, size.width * 0.16f, size.height * 0.62f)
        cubicTo(size.width * 0.37f, size.height * 0.79f, size.width * 0.61f, size.height * 0.78f, size.width * 0.92f, size.height * (0.62f + (seed % 3) * 0.02f))
    }
    drawPath(p1, ProGoldDeep, style = Stroke(width = 6f))
    val p2 = Path().apply {
        moveTo(size.width * 0.28f, size.height * 0.60f)
        cubicTo(size.width * 0.36f, size.height * 0.45f, size.width * 0.51f, size.height * 0.50f, size.width * 0.46f, size.height * 0.68f)
    }
    drawPath(p2, ProRoyal2, style = Stroke(width = 4f))
    repeat(3) { i ->
        val x = size.width * (0.54f + i * 0.11f)
        drawLine(ProGold, Offset(x, size.height * 0.66f), Offset(x, size.height * (0.23f - i * 0.025f)), 4f)
    }
}

private fun starPath(center: Offset, radius: Float, points: Int, innerRatio: Float): Path {
    val path = Path()
    repeat(points * 2) { index ->
        val r = if (index % 2 == 0) radius else radius * innerRatio
        val angle = (-PI / 2.0 + PI * index / points).toFloat()
        val x = center.x + cos(angle) * r
        val y = center.y + sin(angle) * r
        if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}

private fun regularPolygon(center: Offset, radius: Float, sides: Int, rotation: Double): Path {
    val path = Path()
    repeat(sides) { i ->
        val angle = (rotation + 2.0 * PI * i / sides).toFloat()
        val x = center.x + cos(angle) * radius
        val y = center.y + sin(angle) * radius
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}
