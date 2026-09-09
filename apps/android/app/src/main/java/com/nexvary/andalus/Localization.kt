package com.nexvary.andalus

enum class AppLanguage(val code: String, val nativeName: String, val rtl: Boolean) {
    AR("ar", "العربية", true),
    EN("en", "English", false),
    TR("tr", "Türkçe", false),
    ES("es", "Español", false),
    DE("de", "Deutsch", false),
    IT("it", "Italiano", false),
    FR("fr", "Français", false),
    UR("ur", "اردو", true),
    FA("fa", "فارسی", true),
    RU("ru", "Русский", false),
}

data class UiCopy(
    val home: String,
    val services: String,
    val about: String,
    val back: String,
    val language: String,
    val tagline: String,
    val stage: String,
    val startDesigning: String,
    val homeIntro: String,
    val aboutTitle: String,
    val aboutBody: String,
    val contactTitle: String,
    val website: String,
    val facebook: String,
    val email: String,
    val youtube: String,
    val x: String,
    val releaseReady: String,
    val testedNavigation: String,
    val secureImports: String,
    val featureStatus: String,
    val lockIntro: String,
)

data class ServiceCopy(val title: String, val subtitle: String, val badge: String)

object StudioRoutes {
    const val HOME = "home"
    const val SERVICES = "services"
    const val ABOUT = "about"

    val serviceIds = listOf(
        "projects",
        "ai",
        "plan",
        "3d",
        "patterns",
        "assets",
        "materials",
        "exports",
        "ar",
        "library",
    )

    val allInternalRoutes = listOf(HOME, SERVICES, ABOUT) + serviceIds
}

object NexvaryLinks {
    const val WEBSITE = "https://nexvary.com/"
    const val FACEBOOK = "https://www.facebook.com/share/14p9krEn5ij/"
    const val EMAIL = "mailto:info@nexvary.com"
    const val YOUTUBE = "https://www.youtube.com/@NexvaryInc"
    const val X = "https://x.com/Nexvary"
}

object StudioLocalization {
    fun ui(language: AppLanguage): UiCopy = when (language) {
        AppLanguage.AR -> UiCopy(
            "الرئيسية", "الخدمات", "عنا", "رجوع", "اللغة",
            "العمارة الأندلسية والتصميم الإسلامي بالذكاء الاصطناعي",
            "المرحلة 825 • نواة تشغيل إنتاجية • v0.8.25",
            "ابدأ التصميم", "استوديو موحّد للمخططات والزخارف والواجهات والواقع المعزز والذكاء المعماري.",
            "عن Nexvary Andalus Studio", "منصة لتصميم العمارة الأندلسية والإسلامية تجمع المخططات والمواد والزخارف والذكاء الاصطناعي في مشروع واحد قابل للتتبع.",
            "تواصل معنا", "الموقع", "فيسبوك", "البريد الإلكتروني", "يوتيوب", "X",
            "بوابة الإصدار فعالة", "التنقل مفحوص", "الاستيراد الآمن مفعل", "هذه الصفحة متصلة بسجل التنقل وتخضع لاختبار سلامة الروابط.",
            "ثبّت العناصر التي لا تسمح للذكاء الاصطناعي بتغييرها. أي نتيجة تكسر قفلًا تُرفض برمجيًا.",
        )
        AppLanguage.EN -> UiCopy(
            "Home", "Services", "About", "Back", "Language",
            "Andalusian architecture and Islamic design powered by AI",
            "Stage 825 • Production Runtime Foundation • v0.8.25",
            "Start designing", "A unified studio for plans, patterns, facades, AR and architectural intelligence.",
            "About Nexvary Andalus Studio", "A platform for Andalusian and Islamic architecture combining plans, materials, patterns and AI in one traceable project.",
            "Contact us", "Website", "Facebook", "Email", "YouTube", "X",
            "Release gate active", "Navigation tested", "Secure imports enabled", "This page is connected to the central route registry and covered by navigation integrity tests.",
            "Lock any element that AI must not change. A candidate that violates a lock is rejected programmatically.",
        )
        AppLanguage.TR -> UiCopy(
            "Ana Sayfa", "Hizmetler", "Hakkımızda", "Geri", "Dil",
            "Yapay zekâ destekli Endülüs mimarisi ve İslami tasarım",
            "Aşama 825 • Üretim çalışma temeli • v0.8.25",
            "Tasarıma başla", "Planlar, desenler, cepheler, AR ve mimari zekâ için birleşik stüdyo.",
            "Nexvary Andalus Studio Hakkında", "Endülüs ve İslami mimariyi planlar, malzemeler, desenler ve yapay zekâ ile tek izlenebilir projede birleştiren platform.",
            "İletişim", "Web sitesi", "Facebook", "E-posta", "YouTube", "X",
            "Sürüm kapısı aktif", "Gezinme test edildi", "Güvenli içe aktarma açık", "Bu sayfa merkezi rota kaydına bağlıdır ve gezinme bütünlüğü testleri kapsamındadır.",
            "Yapay zekânın değiştirmemesi gereken öğeleri kilitleyin. Kilidi ihlal eden sonuç programatik olarak reddedilir.",
        )
        AppLanguage.ES -> UiCopy(
            "Inicio", "Servicios", "Acerca de", "Atrás", "Idioma",
            "Arquitectura andalusí y diseño islámico con IA",
            "Etapa 825 • Base de ejecución de producción • v0.8.25",
            "Empezar a diseñar", "Un estudio unificado para planos, patrones, fachadas, RA e inteligencia arquitectónica.",
            "Acerca de Nexvary Andalus Studio", "Plataforma de arquitectura andalusí e islámica que combina planos, materiales, patrones e IA en un proyecto trazable.",
            "Contacto", "Sitio web", "Facebook", "Correo", "YouTube", "X",
            "Puerta de lanzamiento activa", "Navegación probada", "Importación segura activa", "Esta página está conectada al registro central de rutas y cubierta por pruebas de integridad de navegación.",
            "Bloquea cualquier elemento que la IA no deba cambiar. Los resultados que violen un bloqueo se rechazan automáticamente.",
        )
        AppLanguage.DE -> UiCopy(
            "Start", "Dienste", "Über uns", "Zurück", "Sprache",
            "Andalusische Architektur und islamisches Design mit KI",
            "Stufe 825 • Produktionslaufzeit-Basis • v0.8.25",
            "Design starten", "Ein Studio für Pläne, Muster, Fassaden, AR und architektonische Intelligenz.",
            "Über Nexvary Andalus Studio", "Eine Plattform für andalusische und islamische Architektur mit Plänen, Materialien, Mustern und KI in einem nachvollziehbaren Projekt.",
            "Kontakt", "Website", "Facebook", "E-Mail", "YouTube", "X",
            "Release-Gate aktiv", "Navigation getestet", "Sicherer Import aktiv", "Diese Seite ist mit dem zentralen Routenregister verbunden und durch Navigationstests abgedeckt.",
            "Sperren Sie Elemente, die die KI nicht ändern darf. Ergebnisse mit Sperrverletzung werden automatisch abgelehnt.",
        )
        AppLanguage.IT -> UiCopy(
            "Home", "Servizi", "Informazioni", "Indietro", "Lingua",
            "Architettura andalusa e design islamico con IA",
            "Fase 825 • Base runtime di produzione • v0.8.25",
            "Inizia a progettare", "Uno studio unificato per planimetrie, motivi, facciate, AR e intelligenza architettonica.",
            "Informazioni su Nexvary Andalus Studio", "Piattaforma per architettura andalusa e islamica che combina piani, materiali, motivi e IA in un progetto tracciabile.",
            "Contatti", "Sito web", "Facebook", "Email", "YouTube", "X",
            "Release gate attivo", "Navigazione verificata", "Importazione sicura attiva", "Questa pagina è collegata al registro centrale delle rotte ed è coperta dai test di integrità della navigazione.",
            "Blocca gli elementi che l'IA non deve modificare. Un risultato che viola un blocco viene rifiutato automaticamente.",
        )
        AppLanguage.FR -> UiCopy(
            "Accueil", "Services", "À propos", "Retour", "Langue",
            "Architecture andalouse et design islamique avec IA",
            "Étape 825 • Base d’exécution production • v0.8.25",
            "Commencer", "Un studio unifié pour plans, motifs, façades, RA et intelligence architecturale.",
            "À propos de Nexvary Andalus Studio", "Plateforme d’architecture andalouse et islamique combinant plans, matériaux, motifs et IA dans un projet traçable.",
            "Contact", "Site web", "Facebook", "E-mail", "YouTube", "X",
            "Porte de release active", "Navigation testée", "Import sécurisé actif", "Cette page est connectée au registre central des routes et couverte par les tests d’intégrité de navigation.",
            "Verrouillez les éléments que l’IA ne doit pas modifier. Tout résultat qui enfreint un verrou est rejeté automatiquement.",
        )
        AppLanguage.UR -> UiCopy(
            "ہوم", "خدمات", "ہمارے بارے میں", "واپس", "زبان",
            "مصنوعی ذہانت کے ساتھ اندلسی فنِ تعمیر اور اسلامی ڈیزائن",
            "مرحلہ 825 • پروڈکشن رن ٹائم بنیاد • v0.8.25",
            "ڈیزائن شروع کریں", "نقشوں، پیٹرنز، فساڈ، AR اور معماری ذہانت کے لیے متحد اسٹوڈیو۔",
            "Nexvary Andalus Studio کے بارے میں", "اندلسی اور اسلامی فنِ تعمیر کے لیے ایک پلیٹ فارم جو نقشے، مواد، پیٹرنز اور AI کو ایک قابلِ سراغ منصوبے میں جمع کرتا ہے۔",
            "رابطہ", "ویب سائٹ", "فیس بک", "ای میل", "یوٹیوب", "X",
            "ریلیز گیٹ فعال", "نیویگیشن ٹیسٹ شدہ", "محفوظ درآمد فعال", "یہ صفحہ مرکزی روٹ رجسٹری سے منسلک ہے اور نیویگیشن انٹیگریٹی ٹیسٹ کے تحت ہے۔",
            "ان عناصر کو لاک کریں جنہیں AI تبدیل نہیں کر سکتا۔ لاک توڑنے والا نتیجہ خودکار طور پر مسترد کیا جاتا ہے۔",
        )
        AppLanguage.FA -> UiCopy(
            "خانه", "خدمات", "درباره ما", "بازگشت", "زبان",
            "معماری اندلسی و طراحی اسلامی با هوش مصنوعی",
            "مرحله 825 • پایه اجرای تولید • v0.8.25",
            "شروع طراحی", "استودیوی یکپارچه برای پلان، الگو، نما، واقعیت افزوده و هوش معماری.",
            "درباره Nexvary Andalus Studio", "پلتفرمی برای معماری اندلسی و اسلامی که پلان، مصالح، الگوها و هوش مصنوعی را در یک پروژه قابل ردیابی ترکیب می‌کند.",
            "ارتباط", "وب‌سایت", "فیسبوک", "ایمیل", "یوتیوب", "X",
            "دروازه انتشار فعال", "ناوبری آزمایش شده", "ورود امن فعال", "این صفحه به رجیستری مرکزی مسیرها متصل است و توسط آزمون یکپارچگی ناوبری پوشش داده می‌شود.",
            "عناصری را که هوش مصنوعی نباید تغییر دهد قفل کنید. نتیجه‌ای که قفل را نقض کند به‌صورت برنامه‌ای رد می‌شود.",
        )
        AppLanguage.RU -> UiCopy(
            "Главная", "Сервисы", "О нас", "Назад", "Язык",
            "Андалусская архитектура и исламский дизайн с ИИ",
            "Этап 825 • Производственная основа • v0.8.25",
            "Начать проект", "Единая студия для планов, орнаментов, фасадов, AR и архитектурного интеллекта.",
            "О Nexvary Andalus Studio", "Платформа для андалусской и исламской архитектуры, объединяющая планы, материалы, орнаменты и ИИ в отслеживаемом проекте.",
            "Контакты", "Сайт", "Facebook", "Эл. почта", "YouTube", "X",
            "Release Gate активен", "Навигация проверена", "Безопасный импорт включён", "Эта страница подключена к центральному реестру маршрутов и покрыта тестами целостности навигации.",
            "Заблокируйте элементы, которые ИИ не должен менять. Результат, нарушающий блокировку, отклоняется программно.",
        )
    }

    fun services(language: AppLanguage): Map<String, ServiceCopy> = when (language) {
        AppLanguage.AR -> mapOf(
            "projects" to ServiceCopy("المشاريع والإصدارات", "حفظ النسخ وتتبع البصمات ومنع تعارض التعديلات", "SYNC"),
            "ai" to ServiceCopy("المعماري الذكي", "خطط الذكاء الاصطناعي مع القفل المعماري وحالة المهام", "AI"),
            "plan" to ServiceCopy("المخطط ثنائي الأبعاد", "الجدران والغرف والفتحات والقياسات", "2D"),
            "3d" to ServiceCopy("الاستوديو ثلاثي الأبعاد", "مشاهد بأبعاد حقيقية وتصدير النماذج", "3D"),
            "patterns" to ServiceCopy("استوديو الزخارف", "الزليج والنجوم والروسيات والحدود", "PATTERN"),
            "assets" to ServiceCopy("مكتبة الأصول", "أصول مرخصة مع المصدر والترخيص والبصمة", "LICENSE"),
            "materials" to ServiceCopy("الخامات والكميات", "جداول الكميات والهالك والأسعار التي يدخلها المستخدم", "BOM"),
            "exports" to ServiceCopy("مركز التصدير", "تصدير الملفات الهندسية مع بيان المصدر والبصمة", "EXPORT"),
            "ar" to ServiceCopy("الواقع المعزز", "معاينة العناصر على الأجهزة المدعومة", "AR"),
            "library" to ServiceCopy("مكتبة الأندلس", "غرناطة وقرطبة والمغرب والمدارس الإسلامية", "LIB"),
        )
        AppLanguage.TR -> titledServices(listOf("Projeler ve sürümler", "Akıllı mimar", "2B plan", "3B stüdyo", "Desen stüdyosu", "Varlık kütüphanesi", "Malzemeler ve miktarlar", "Dışa aktarma merkezi", "Artırılmış gerçeklik", "Endülüs kütüphanesi"), "Proje özelliği etkin ve bağlantılıdır")
        AppLanguage.ES -> titledServices(listOf("Proyectos y versiones", "Arquitecto inteligente", "Plano 2D", "Estudio 3D", "Estudio de patrones", "Biblioteca de recursos", "Materiales y cantidades", "Centro de exportación", "Realidad aumentada", "Biblioteca de Al-Ándalus"), "Función activa y conectada al proyecto")
        AppLanguage.DE -> titledServices(listOf("Projekte und Versionen", "KI-Architekt", "2D-Plan", "3D-Studio", "Musterstudio", "Asset-Bibliothek", "Materialien und Mengen", "Exportzentrum", "Augmented Reality", "Al-Andalus-Bibliothek"), "Aktive und verbundene Projektfunktion")
        AppLanguage.IT -> titledServices(listOf("Progetti e versioni", "Architetto intelligente", "Planimetria 2D", "Studio 3D", "Studio motivi", "Libreria risorse", "Materiali e quantità", "Centro esportazione", "Realtà aumentata", "Biblioteca di Al-Andalus"), "Funzione di progetto attiva e collegata")
        AppLanguage.FR -> titledServices(listOf("Projets et versions", "Architecte intelligent", "Plan 2D", "Studio 3D", "Studio de motifs", "Bibliothèque d’actifs", "Matériaux et quantités", "Centre d’export", "Réalité augmentée", "Bibliothèque d’Al-Andalus"), "Fonction active et connectée au projet")
        AppLanguage.UR -> titledServices(listOf("پروجیکٹس اور ورژنز", "ذہین معمار", "2D پلان", "3D اسٹوڈیو", "پیٹرن اسٹوڈیو", "اثاثہ لائبریری", "مواد اور مقدار", "ایکسپورٹ مرکز", "اگمینٹڈ ریئلٹی", "اندلس لائبریری"), "فعال اور منسلک پروجیکٹ فیچر")
        AppLanguage.FA -> titledServices(listOf("پروژه‌ها و نسخه‌ها", "معمار هوشمند", "پلان دوبعدی", "استودیوی سه‌بعدی", "استودیوی الگو", "کتابخانه دارایی", "مصالح و مقادیر", "مرکز خروجی", "واقعیت افزوده", "کتابخانه اندلس"), "قابلیت فعال و متصل پروژه")
        AppLanguage.RU -> titledServices(listOf("Проекты и версии", "Умный архитектор", "2D-план", "3D-студия", "Студия орнаментов", "Библиотека ресурсов", "Материалы и объёмы", "Центр экспорта", "Дополненная реальность", "Библиотека Аль-Андалуса"), "Активная и подключённая функция проекта")
        AppLanguage.EN -> titledServices(listOf("Projects & revisions", "AI Architect", "2D Floor Plan", "3D Studio", "Pattern Studio", "Asset Library", "Materials & quantities", "Export Center", "Augmented Reality", "Al-Andalus Library"), "Active project capability connected to the shared project model")
    }

    fun lockLabels(language: AppLanguage): List<String> = when (language) {
        AppLanguage.AR -> listOf("كتلة المبنى", "عدد الطوابق", "الأبواب والنوافذ", "المدخل", "خط السطح", "حدود الغرف")
        AppLanguage.EN -> listOf("Building mass", "Floor count", "Doors & windows", "Entrance", "Roof line", "Room boundaries")
        AppLanguage.TR -> listOf("Bina kütlesi", "Kat sayısı", "Kapı ve pencereler", "Giriş", "Çatı çizgisi", "Oda sınırları")
        AppLanguage.ES -> listOf("Masa del edificio", "Número de plantas", "Puertas y ventanas", "Entrada", "Línea de cubierta", "Límites de habitaciones")
        AppLanguage.DE -> listOf("Gebäudemasse", "Geschosszahl", "Türen und Fenster", "Eingang", "Dachlinie", "Raumgrenzen")
        AppLanguage.IT -> listOf("Massa edificio", "Numero piani", "Porte e finestre", "Ingresso", "Linea tetto", "Confini stanze")
        AppLanguage.FR -> listOf("Masse du bâtiment", "Nombre d’étages", "Portes et fenêtres", "Entrée", "Ligne de toiture", "Limites des pièces")
        AppLanguage.UR -> listOf("عمارت کا حجم", "منزلوں کی تعداد", "دروازے اور کھڑکیاں", "داخلہ", "چھت کی لکیر", "کمروں کی حدود")
        AppLanguage.FA -> listOf("حجم ساختمان", "تعداد طبقات", "درها و پنجره‌ها", "ورودی", "خط بام", "مرز اتاق‌ها")
        AppLanguage.RU -> listOf("Масса здания", "Число этажей", "Двери и окна", "Вход", "Линия крыши", "Границы комнат")
    }

    private fun titledServices(titles: List<String>, subtitle: String): Map<String, ServiceCopy> {
        val badges = listOf("SYNC", "AI", "2D", "3D", "PATTERN", "LICENSE", "BOM", "EXPORT", "AR", "LIB")
        return StudioRoutes.serviceIds.mapIndexed { index, id ->
            id to ServiceCopy(titles[index], subtitle, badges[index])
        }.toMap()
    }
}
