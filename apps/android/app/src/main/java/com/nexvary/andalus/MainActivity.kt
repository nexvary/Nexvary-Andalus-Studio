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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
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

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
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
}

@Composable
private fun Dashboard(
    padding: PaddingValues,
    onOpen: (String) -> Unit,
) {
    val tools = listOf(
        "projects" to StudioTool("المشاريع والإصدارات", "حفظ النسخ وتتبع البصمات ومنع تعارض التعديلات", "SYNC"),
        "ai" to StudioTool("المعماري الذكي", "خطط الذكاء الاصطناعي مع القفل المعماري وحالة المهام", "LOCK"),
        "plan" to StudioTool("المخطط ثنائي الأبعاد", "الجدران والغرف والفتحات والقياسات", "2D"),
        "3d" to StudioTool("الاستوديو ثلاثي الأبعاد", "مشاهد بأبعاد حقيقية وتصدير النماذج", "3D"),
        "patterns" to StudioTool("استوديو الزخارف", "الزليج والنجوم والروسيات والحدود", "PATTERN"),
        "assets" to StudioTool("مكتبة الأصول", "أصول مرخصة مع المصدر والترخيص والبصمة", "LICENSE"),
        "materials" to StudioTool("الخامات والكميات", "جداول الكميات والهالك والأسعار التي يدخلها المستخدم", "BOM"),
        "exports" to StudioTool("مركز التصدير", "تصدير الملفات الهندسية مع بيان المصدر والبصمة", "EXPORT"),
        "ar" to StudioTool("الواقع المعزز", "معاينة العناصر على الأجهزة المدعومة", "AR"),
        "library" to StudioTool("مكتبة الأندلس", "غرناطة وقرطبة والمغرب والمدارس الإسلامية", "LIB"),
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
            Text(
                "Nexvary Andalus Studio",
                modifier = Modifier.fillMaxWidth(),
                color = Gold,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
            )
            Text(
                "Stage 825 • Production Runtime Foundation • v0.8.25",
                modifier = Modifier.fillMaxWidth(),
                color = Emerald,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Start,
            )
            Text(
                "العمارة الأندلسية والتصميم الإسلامي بالذكاء الاصطناعي",
                modifier = Modifier.fillMaxWidth(),
                color = Muted,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Start,
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
                        Text(
                            tool.title,
                            modifier = Modifier.fillMaxWidth(),
                            color = Ivory,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Start,
                        )
                        Spacer(Modifier.height(5.dp))
                        Text(
                            tool.subtitle,
                            modifier = Modifier.fillMaxWidth(),
                            color = Muted,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Start,
                        )
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
        "plan" -> "المخطط ثنائي الأبعاد"
        "3d" -> "الاستوديو ثلاثي الأبعاد"
        "patterns" -> "استوديو الزخارف"
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
            "حفظ معاملات موثوق للمشاريع",
            "بصمة ثابتة لكل إصدار",
            "منع تعارض التعديلات المتزامنة",
        )
        "3d" -> listOf(
            "أبعاد الجدران بالمقاييس الحقيقية",
            "تصدير مشاهد ثلاثية الأبعاد",
            "ربط التصدير ببصمة إصدار المشروع",
        )
        "assets" -> listOf(
            "المصدر والترخيص ونسبة العمل لصاحبه",
            "بصمة سلامة للملفات",
            "بوابة مراجعة قبل الاستخدام التجاري",
        )
        "materials" -> listOf(
            "حساب المساحات والهالك",
            "تجميع جداول الكميات",
            "التكلفة تعتمد فقط على سعر الوحدة الذي يدخله المستخدم",
        )
        "exports" -> listOf(
            "تصدير الزخارف والمخططات",
            "ملفات هندسية وجداول كميات",
            "تصدير المشهد ثلاثي الأبعاد",
        )
        "ar" -> listOf(
            "دمج العرض ثلاثي الأبعاد والواقع المعزز",
            "تشغيل الكاميرا حسب قدرات الجهاز",
            "لا اعتماد للميزة قبل اختبار جهاز حقيقي",
        )
        "plan" -> listOf("مساحات ومحيط الغرف", "التحقق من الجدران والفتحات", "نموذج هندسي حتمي")
        "patterns" -> listOf("النجوم", "الروسيات", "شبكات الزليج", "الحدود")
        else -> listOf("المدرسة الغرناطية النصرية", "القرطبية", "المغربية الأندلسية", "الأندلسية المعاصرة")
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Panel,
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            lines.forEach { line -> Text("• $line", color = Ivory) }
            Text(
                "الواجهات الثقيلة أو المعتمدة على الأجهزة لا تُعتبر جاهزة للإصدار قبل اجتياز بوابة البناء والاختبار الفعلي.",
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
            "ثبّت العناصر التي لا تسمح للذكاء الاصطناعي بتغييرها. أي نتيجة تكسر قفلًا تُرفض برمجيًا.",
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
