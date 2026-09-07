package com.nexvary.andalus

data class AssetSummary(
    val assetId: String,
    val category: String,
    val licenseId: String,
    val reviewed: Boolean,
    val commercialUseAllowed: Boolean,
)

data class AiAdapterSummary(
    val id: String,
    val displayName: String,
    val available: Boolean,
    val externalService: Boolean,
)

enum class AiJobStatus {
    QUEUED,
    RUNNING,
    SUCCEEDED,
    FAILED,
    CANCELLED,
}

data class AiJobSummary(
    val jobId: String,
    val adapterId: String,
    val status: AiJobStatus,
    val requestDigest: String,
) {
    init {
        require(jobId.isNotBlank())
        require(adapterId.isNotBlank())
        require(requestDigest.length == 64)
    }
}

data class ProjectSyncState(
    val projectId: String,
    val revision: Int,
    val fingerprint: String,
    val parentFingerprint: String? = null,
) {
    init {
        require(projectId.isNotBlank())
        require(revision >= 1)
        require(fingerprint.length == 64)
        require(parentFingerprint == null || parentFingerprint.length == 64)
    }
}

data class SceneExportRequest(
    val projectFingerprint: String,
    val format: String = "gltf2",
    val realWorldScale: Boolean = true,
) {
    init {
        require(projectFingerprint.length == 64)
        require(format == "gltf2")
    }
}

object StudioStage825 {
    const val VERSION = "0.8.25"
    const val STAGE = 825
}
