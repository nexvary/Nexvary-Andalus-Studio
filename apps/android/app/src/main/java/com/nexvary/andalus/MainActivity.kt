package com.nexvary.andalus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private val Navy = Color(0xFF06131F)
private val Panel = Color(0xFF0D2630)
private val Emerald = Color(0xFF1D735F)
private val Gold = Color(0xFFD6B85E)
private val Ivory = Color(0xFFF3ECD8)
private val Muted = Color(0xFF91AAA5)

data class StudioTool(val title: String, val subtitle: String, val badge: String? = null)

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
    var screen by remember { mutableStateOf("home") }
    BackHandler(enabled = screen != "home") { screen = "home" }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Navy,
    ) { innerPadding ->
        if (screen == "home") {
            Dashboard(padding = innerPadding, onOpen = { screen = it })
        } else {
            FeatureScreen(key = screen, padding = innerPadding, onBack = { screen = "home" })
        }
    }
}

@Composable
private fun Dashboard(
    padding: PaddingValues,
    onOpen: (String) -> Unit,
) {
    val tools = listOf(
        "projects" to StudioTool("المشاريع والإصدارات", "حفظ النسخ وتتبع البصمات ومنع تعارض التعديلات", "SYNC"),
        "ai" to StudioTool("المعماري الذكي", "خطط AI مع Architectural Lock وحالة Jobs", "LOCK"),
        "plan" to StudioTool("المخطط 2D", "الجدران والغرف والفتحات والقياسات"),
        "3d" to StudioTool("الاستوديو 3D", "مشاهد حقيقية القياس وتصدير glTF 2.0", "glTF"),
        "patterns" to StudioTool("Pattern Studio", "الزليج والنجوم والروسيات والحدود"),
        "assets" to StudioTool("مكتبة الأصول", "أصول مرخصة مع المصدر والترخيص والبصمة", "LICENSE"),
        "materials" to StudioTool("الخامات والكميات", "BOM والهالك والأسعار التي يدخلها المستخدم"),
        "exports" to StudioTool("مركز التصدير", "SVG وDXF وCSV وglTF مع Manifest"),
        "ar" to StudioTool("الواقع المعزز", "معاينة العناصر على الأجهزة المدعومة", "HW GATE"),
        "library" to StudioTool("مكتبة الأندلس", "غرناطة وقرطبة والمغرب والمدارس الإسلامية"),
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Navy)
            .safeDrawingPadding()
            .padding(padding)
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Spacer(Modifier.height(12.dp))
            Text("Nexvary Andalus Studio", color = Gold, fontWeight = FontWeight.Bold)
            Text(
                "Stage 825 • Production Runtime Foundation • v0.8.25",
                color = Emerald,
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                "العمارة الأندلسية والتصميم الإسلامي بالذكاء الاصطناعي",
                color = Muted,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Right,
            )
            Spacer(Modifier.height(10.dp))
        }
        items(tools) { (key, tool) ->
            Card(
                onClick = { onOpen(key) },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Gold.copy(alpha = 0.24f), RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = Panel),
                shape = RoundedCornerShape(18.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(tool.title, color = Ivory, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(5.dp))
                        Text(tool.subtitle, color = Muted, style = MaterialTheme.typography.bodySmall)
                    }
                    if (tool.badge != null) {
                        Text(tool.badge, color = Gold, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun FeatureScreen(
    key: String,
    padding: PaddingValues,
    onBack: () -> Unit,
) {
    val title = when (key) {
        "projects" -> "المشاريع والإصدارات"
        "ai" -> "المعماري الذكي"
        "plan" -> "المخطط 2D"
        "3d" -> "الاستوديو 3D"
        "patterns" -> "Pattern Studio"
        "assets" -> "مكتبة الأصول"
        "materials" -> "الخامات والكميات"
        "exports" -> "مركز التصدير"
        "ar" -> "الواقع المعزز"
        else -> "مكتبة الأندلس"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Navy)
            .safeDrawingPadding()
            .padding(padding)
            .padding(18.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(title, color = Gold, fontWeight = FontWeight.Bold)
                Text("Stage 825", color = Muted, style = MaterialTheme.typography.labelSmall)
            }
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = Panel, contentColor = Ivory),
            ) { Text("رجوع") }
        }
        Spacer(Modifier.height(18.dp))

        if (key == "ai") {
            ArchitecturalLocks()
        } else {
            CapabilityPanel(key)
        }
    }
}

@Composable
private fun CapabilityPanel(key: String) {
    val lines = when (key) {
        "projects" -> listOf(
            "SQLite transactional repository",
            "Immutable revision fingerprints",
            "Optimistic concurrency conflict protection",
        )
        "3d" -> listOf(
            "Real-world wall dimensions",
            "glTF 2.0 scene export",
            "Scene export tied to project fingerprint",
        )
        "assets" -> listOf(
            "Source URL + license + attribution",
            "SHA-256 integrity digest",
            "Commercial-use and review gate",
        )
        "materials" -> listOf(
            "Surface takeoff and waste",
            "BOM aggregation",
            "Costs only from user-supplied unit prices",
        )
        "exports" -> listOf(
            "SVG patterns and floor plans",
            "DXF R12 and UTF-8 CSV",
            "glTF 2.0 scene export",
        )
        "ar" -> listOf(
            "SceneView / ARSceneView integration",
            "Camera capability is hardware-gated",
            "No release claim before real-device QA",
        )
        "plan" -> listOf("Room area/perimeter", "Wall/opening validation", "Deterministic geometry model")
        "patterns" -> listOf("Stars", "Rosettes", "Zellij grids", "Borders")
        else -> listOf("Nasrid Granada", "Cordoban", "Moroccan-Andalusian", "Contemporary Andalusian")
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Panel,
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            lines.forEach { line -> Text("• $line", color = Ivory) }
            Text(
                "الواجهات الثقيلة أو المعتمدة على الأجهزة لا تُعتبر Release-ready قبل اجتياز بوابة البناء والاختبار الفعلي.",
                color = Muted,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun ArchitecturalLocks() {
    val locks = listOf(
        "كتلة المبنى" to true,
        "عدد الطوابق" to true,
        "الأبواب والنوافذ" to true,
        "المدخل" to false,
        "خط السطح" to true,
        "حدود الغرف" to true,
    )
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            "ثبّت العناصر التي لا تسمح للذكاء الاصطناعي بتغييرها. نتيجة AI التي تكسر قفلًا تُرفض برمجيًا.",
            color = Muted,
            style = MaterialTheme.typography.bodySmall,
        )
        locks.forEach { (label, initial) ->
            var checked by remember(label) { mutableStateOf(initial) }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Panel, RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(label, color = Ivory)
                Switch(checked = checked, onCheckedChange = { checked = it })
            }
        }
    }
}
