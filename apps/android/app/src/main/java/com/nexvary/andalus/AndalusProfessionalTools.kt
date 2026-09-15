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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
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

/** 2,400 deterministic local ornaments: 12 historical families × 200 variants. */
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

@Composable
internal fun TughraInspiredMark(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val gold = ProGold
        val ivory = ProIvory
        val blue = Color(0xFF315FC2)
        val w = size.width
        val h = size.height
        val baseline = h * 0.76f

        // Three upright calligraphic stems inspired by the supplied tughra reference.
        listOf(0.42f, 0.52f, 0.62f).forEachIndexed { i, xRatio ->
            val x = w * xRatio
            drawLine(gold, Offset(x, baseline), Offset(x + i * 2f, h * 0.08f), strokeWidth = 7f)
            drawLine(ivory, Offset(x + 2f, baseline - 3f), Offset(x + 2f + i * 2f, h * 0.11f), strokeWidth = 2.5f)
        }

        val sweep = Path().apply {
            moveTo(w * 0.08f, h * 0.58f)
            cubicTo(w * 0.18f, h * 0.20f, w * 0.48f, h * 0.34f, w * 0.44f, h * 0.60f)
            cubicTo(w * 0.39f, h * 0.90f, w * 0.13f, h * 0.86f, w * 0.09f, h * 0.64f)
            cubicTo(w * 0.28f, h * 0.84f, w * 0.48f, h * 0.82f, w * 0.68f, h * 0.72f)
            cubicTo(w * 0.78f, h * 0.67f, w * 0.87f, h * 0.70f, w * 0.95f, h * 0.66f)
        }
        drawPath(sweep, gold, style = Stroke(width = 7f))
        drawPath(sweep, blue.copy(alpha = 0.75f), style = Stroke(width = 2.2f))

        val inner = Path().apply {
            moveTo(w * 0.22f, h * 0.61f)
            cubicTo(w * 0.33f, h * 0.46f, w * 0.48f, h * 0.51f, w * 0.43f, h * 0.67f)
            cubicTo(w * 0.37f, h * 0.79f, w * 0.25f, h * 0.76f, w * 0.20f, h * 0.68f)
        }
        drawPath(inner, ivory, style = Stroke(width = 5f))
        drawPath(inner, gold, style = Stroke(width = 2f))

        // Jewel rosette on the right, mirroring the reference's pendant medallion.
        val center = Offset(w * 0.82f, h * 0.35f)
        repeat(12) { i ->
            val a = (2.0 * PI * i / 12.0).toFloat()
            val p = Offset(center.x + cos(a) * h * 0.18f, center.y + sin(a) * h * 0.18f)
            drawCircle(gold, h * 0.045f, p)
            drawCircle(blue, h * 0.025f, p)
        }
        drawCircle(gold, h * 0.095f, center)
        drawCircle(ProRoyal, h * 0.066f, center)
        drawCircle(ivory, h * 0.018f, center)
    }
}

@Composable
internal fun V3PatternStudioPro(language: AppLanguage) {
    var family by remember { mutableStateOf("all") }
    var page by remember { mutableStateOf(0) }
    val filtered = remember(family) {
        if (family == "all") LocalOrnamentCatalog else LocalOrnamentCatalog.filter { it.family == family }
    }
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
        FamilyFilter(language, family) {
            family = it
            page = 0
        }
        visible.chunked(3).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { asset ->
                    Card(
                        modifier = Modifier.weight(1f).border(1.dp, ProGold, RoundedCornerShape(16.dp)).testTag("ornament-${asset.id}"),
                        colors = CardDefaults.cardColors(containerColor = ProIvory),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            OrnamentPreview(asset, Modifier.fillMaxWidth().height(74.dp))
                            Text(
                                if (language.rtl) asset.arName else asset.enName,
                                color = ProRoyal,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
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
            it.arName.contains(query, ignoreCase = true) || it.enName.contains(query, ignoreCase = true) || it.family.contains(query, ignoreCase = true)
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
                        modifier = Modifier.weight(1f).height(92.dp).border(1.dp, if (selected?.id == asset.id) ProRoyal2 else ProGold, RoundedCornerShape(14.dp)),
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
            if (language.rtl) "صوّر الغرفة، اختر زخرفة من الشريط، ثم أضفها واسحبها فوق الصورة إلى المكان المطلوب."
            else "Capture the room, choose an ornament, add it, then drag it freely over the photo.",
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
            Modifier.fillMaxWidth().height(360.dp).background(ProRoyal, RoundedCornerShape(22.dp)).border(2.dp, ProGold, RoundedCornerShape(22.dp)).testTag("room-workspace"),
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
                    // Room placeholder keeps the workspace useful even before taking a photo.
                    drawRect(Color(0xFFEEE6DD))
                    drawLine(ProRoyal2, Offset(size.width * 0.12f, size.height * 0.18f), Offset(size.width * 0.12f, size.height * 0.88f), 4f)
                    drawLine(ProRoyal2, Offset(size.width * 0.88f, size.height * 0.18f), Offset(size.width * 0.88f, size.height * 0.88f), 4f)
                    drawLine(ProGoldDeep, Offset(size.width * 0.12f, size.height * 0.88f), Offset(size.width * 0.88f, size.height * 0.88f), 5f)
                    val arch = Path().apply {
                        moveTo(size.width * 0.32f, size.height * 0.70f)
                        quadraticBezierTo(size.width * 0.50f, size.height * 0.34f, size.width * 0.68f, size.height * 0.70f)
                    }
                    drawPath(arch, ProGold, style = Stroke(width = 7f))
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
                                    placed[index] = item.copy(
                                        x = item.x + dragAmount.x,
                                        y = item.y + dragAmount.y,
                                    )
                                }
                            }
                        }
                        .testTag("placed-${item.asset.id}"),
                ) {
                    OrnamentPreview(item.asset, Modifier.fillMaxSize().padding(5.dp))
                }
            }
        }

        Text(if (language.rtl) "اسحب الزخرفة داخل الصورة بعد إضافتها" else "Drag any placed ornament directly on the image", color = ProRoyal, fontWeight = FontWeight.Bold)
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            LocalOrnamentCatalog.take(18).forEach { asset ->
                Surface(
                    onClick = { selected = asset },
                    modifier = Modifier.size(76.dp).border(2.dp, if (selected.id == asset.id) ProRoyal2 else ProGold, RoundedCornerShape(14.dp)),
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
            Text(
                if (language.rtl) "أضف: ${selected.arName}" else "Add: ${selected.enName}",
                fontWeight = FontWeight.Black,
            )
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
            if (selected == family.first) {
                Button(
                    onClick = { onSelect(family.first) },
                    colors = ButtonDefaults.buttonColors(containerColor = ProRoyal, contentColor = ProIvory),
                ) { Text(if (language.rtl) family.second else family.third) }
            } else {
                OutlinedButton(onClick = { onSelect(family.first) }) {
                    Text(if (language.rtl) family.second else family.third, color = ProRoyal)
                }
            }
        }
    }
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
    Canvas(modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension * 0.31f
        val points = 8 + (asset.seed % 5) * 2
        val star = Path()
        repeat(points * 2) { index ->
            val outer = index % 2 == 0
            val r = if (outer) radius else radius * (0.42f + (asset.seed % 4) * 0.06f)
            val angle = (-PI / 2.0 + PI * index / points).toFloat()
            val x = center.x + cos(angle) * r
            val y = center.y + sin(angle) * r
            if (index == 0) star.moveTo(x, y) else star.lineTo(x, y)
        }
        star.close()

        drawCircle(ProRose, radius * 1.36f, center)
        drawCircle(ProGold.copy(alpha = 0.28f), radius * 1.25f, center, style = Stroke(width = 3f))
        drawPath(star, if (asset.seed % 2 == 0) ProRoyal2 else ProGoldDeep)
        drawPath(star, ProGold, style = Stroke(width = 3f))
        drawCircle(ProIvory, radius * 0.26f, center)
        drawCircle(ProGoldDeep, radius * 0.13f, center)
        repeat(8) { i ->
            val a = (2.0 * PI * i / 8.0).toFloat()
            val p = Offset(center.x + cos(a) * radius * 0.78f, center.y + sin(a) * radius * 0.78f)
            drawCircle(ProGold, radius * 0.07f, p)
        }
    }
}
