from __future__ import annotations

from dataclasses import dataclass

from .styles import get_style


LOCK_LABELS = {
    "massing": "overall building massing",
    "floorCount": "number of floors",
    "openings": "existing window and door opening positions",
    "entrance": "main entrance geometry and position",
    "roofline": "roofline and parapet silhouette",
    "roomBoundaries": "room boundaries and structural layout",
}


@dataclass(frozen=True)
class DesignIntent:
    style_school: str
    space_type: str
    ornament_intensity: str = "balanced"
    user_request: str = ""
    locks: dict[str, bool] | None = None

    def validate(self) -> None:
        get_style(self.style_school)
        if self.space_type not in {
            "facade",
            "interior",
            "courtyard",
            "garden",
            "floorplan",
            "full-home",
        }:
            raise ValueError("unsupported space_type")
        if self.ornament_intensity not in {
            "calm",
            "balanced",
            "luxury",
            "royal",
            "historic",
        }:
            raise ValueError("unsupported ornament_intensity")


def compose_design_prompt(intent: DesignIntent) -> dict[str, object]:
    intent.validate()
    style = get_style(intent.style_school)
    locks = intent.locks or {}
    preserved = [
        LOCK_LABELS[key]
        for key, enabled in locks.items()
        if enabled and key in LOCK_LABELS
    ]
    unlocked = [
        LOCK_LABELS[key]
        for key, enabled in locks.items()
        if not enabled and key in LOCK_LABELS
    ]

    prompt_parts = [
        f"Redesign the {intent.space_type} in {style.name_en} style.",
        *style.prompt_tokens,
        f"Ornament intensity: {intent.ornament_intensity}.",
        "Preserve realistic scale, material junctions, openings, circulation, "
        "and buildable proportions.",
    ]
    if preserved:
        prompt_parts.append("Do not alter: " + ", ".join(preserved) + ".")
    if unlocked:
        prompt_parts.append("May reinterpret: " + ", ".join(unlocked) + ".")
    if intent.user_request.strip():
        prompt_parts.append("User direction: " + intent.user_request.strip())

    negative = [
        "warped walls",
        "impossible stairs",
        "floating doors",
        "duplicated windows",
        "distorted perspective",
        "text artifacts",
        "structural impossibility",
        "non-functional circulation",
    ]

    return {
        "style": style.id,
        "prompt": " ".join(prompt_parts),
        "negativePrompt": ", ".join(negative),
        "preserved": preserved,
        "unlocked": unlocked,
        "palette": list(style.palette),
        "motifs": list(style.motifs),
        "architecturalElements": list(style.architectural_elements),
    }
