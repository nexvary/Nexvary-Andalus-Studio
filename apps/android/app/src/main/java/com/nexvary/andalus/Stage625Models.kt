package com.nexvary.andalus

data class ArchitecturalLocks(
    val massing: Boolean = true,
    val floorCount: Boolean = true,
    val openings: Boolean = true,
    val entrance: Boolean = false,
    val roofline: Boolean = true,
    val roomBoundaries: Boolean = true,
)

data class MaterialTakeoff(
    val materialId: String,
    val areaM2: Double,
    val wasteOverride: Double? = null,
) {
    init {
        require(areaM2 >= 0.0) { "areaM2 must be non-negative" }
        require(wasteOverride == null || wasteOverride in 0.0..0.5) { "invalid waste override" }
    }
}

data class ProjectRevisionSummary(
    val projectId: String,
    val revision: Int,
    val fingerprint: String,
) {
    init {
        require(revision >= 1)
        require(fingerprint.length == 64)
    }
}

object StudioStage {
    const val VERSION = "0.6.25"
    const val STAGE = 625
}
