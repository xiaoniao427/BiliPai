package com.android.purebilibili.navigation3

internal data class BiliPaiNavSourceMetadata(
    val sourceKey: String? = null,
    val sourceRoute: String? = null,
    val clickedBoundsRecorded: Boolean = false,
    val cardFullyVisible: Boolean = false
) {
    val sharedTransitionReady: Boolean
        get() = clickedBoundsRecorded && cardFullyVisible
}
