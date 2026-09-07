from __future__ import annotations

from dataclasses import dataclass


@dataclass(frozen=True)
class StyleSchool:
    id: str
    name_en: str
    name_ar: str
    palette: tuple[str, ...]
    motifs: tuple[str, ...]
    architectural_elements: tuple[str, ...]
    prompt_tokens: tuple[str, ...]


_STYLE_SCHOOLS = (
    StyleSchool(
        id="nasrid-granada",
        name_en="Nasrid Granada",
        name_ar="غرناطي نصري",
        palette=("ivory", "emerald", "lapis", "terracotta", "antique-gold"),
        motifs=("zellij", "arabesque", "epigraphy", "muqarnas"),
        architectural_elements=(
            "horseshoe-arches",
            "sebka-panels",
            "slender-columns",
            "courtyard-fountain",
        ),
        prompt_tokens=(
            "Nasrid Andalusian architecture",
            "refined stucco relief",
            "geometric zellij",
            "delicate horseshoe arches",
            "courtyard water geometry",
        ),
    ),
    StyleSchool(
        id="cordoban",
        name_en="Cordoban",
        name_ar="قرطبي",
        palette=("warm-stone", "brick-red", "ivory", "deep-green"),
        motifs=("alternating-voussoirs", "geometric-carving", "floral-relief"),
        architectural_elements=("double-arches", "horseshoe-arches", "stone-columns"),
        prompt_tokens=(
            "Cordoban Andalusian architecture",
            "red and ivory voussoirs",
            "monumental horseshoe arches",
            "calm stone surfaces",
        ),
    ),
    StyleSchool(
        id="moroccan-andalusian",
        name_en="Moroccan Andalusian",
        name_ar="مغربي أندلسي",
        palette=("cobalt", "turquoise", "white", "cedar", "brass"),
        motifs=("zellij", "carved-plaster", "cedar-geometric"),
        architectural_elements=("riadh-courtyard", "carved-doors", "arcades"),
        prompt_tokens=(
            "Moroccan Andalusian riad",
            "hand-cut zellij",
            "carved cedar",
            "white carved plaster",
        ),
    ),
    StyleSchool(
        id="mamluk",
        name_en="Mamluk",
        name_ar="مملوكي",
        palette=("sandstone", "black", "white", "brass", "deep-blue"),
        motifs=("ablaq", "stone-carving", "thuluth-bands"),
        architectural_elements=("pointed-arches", "mashrabiya", "stone-portals"),
        prompt_tokens=("Mamluk architecture", "ablaq masonry", "carved stone portal"),
    ),
    StyleSchool(
        id="ottoman",
        name_en="Ottoman",
        name_ar="عثماني",
        palette=("white", "iznik-blue", "turquoise", "gold", "walnut"),
        motifs=("iznik-floral", "calligraphy", "wood-inlay"),
        architectural_elements=("domed-space", "ogee-arches", "timber-screens"),
        prompt_tokens=("Ottoman interior", "Iznik-inspired pattern", "restrained gilding"),
    ),
    StyleSchool(
        id="damascene",
        name_en="Damascene",
        name_ar="دمشقي",
        palette=("walnut", "mother-of-pearl", "cream", "deep-red", "brass"),
        motifs=("ajami", "mother-of-pearl-inlay", "arabesque"),
        architectural_elements=("iwans", "courtyard", "timber-ceilings"),
        prompt_tokens=("Damascene courtyard house", "ajami woodwork", "inlaid timber"),
    ),
    StyleSchool(
        id="contemporary-andalusian",
        name_en="Contemporary Andalusian",
        name_ar="أندلسي معاصر",
        palette=("warm-white", "sand", "emerald", "brushed-brass", "dark-walnut"),
        motifs=("simplified-zellij", "linear-arabesque", "minimal-geometric"),
        architectural_elements=("modern-horseshoe-arches", "screen-walls", "linear-water"),
        prompt_tokens=(
            "contemporary Andalusian architecture",
            "minimal Islamic geometry",
            "clean modern massing",
            "refined horseshoe arch references",
        ),
    ),
)


def list_styles() -> list[StyleSchool]:
    return list(_STYLE_SCHOOLS)


def get_style(style_id: str) -> StyleSchool:
    for style in _STYLE_SCHOOLS:
        if style.id == style_id:
            return style
    raise KeyError(f"unknown style school: {style_id}")
