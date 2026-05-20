package com.android.purebilibili.navigation3

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BiliPaiNavEntryProviderPolicyTest {

    @Test
    fun sharedReadyMetadataDisablesRouteLayerForReturnTarget() {
        val metadata = biliPaiNavEntryMetadata(
            key = BiliPaiNavKey.Home,
            sourceMetadata = BiliPaiNavSourceMetadata(
                sourceKey = "home:BV1",
                sourceRoute = "home",
                clickedBoundsRecorded = true,
                cardFullyVisible = true
            )
        )

        assertTrue(metadata.isNotEmpty())
        assertEquals(3, metadata.size)
    }

    @Test
    fun providerUsesTypedVideoEntryContentKey() {
        val provider = biliPaiNavEntryProvider(
            sourceMetadata = BiliPaiNavSourceMetadata(),
            content = {}
        )
        val key = BiliPaiNavKey.VideoDetail(bvid = "BV1", sourceRoute = "search")
        val entry = provider(key)

        assertEquals(key.toString(), entry.contentKey)
        assertTrue(entry.metadata.isNotEmpty())
    }
}
