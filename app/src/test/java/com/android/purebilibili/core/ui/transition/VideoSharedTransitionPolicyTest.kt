package com.android.purebilibili.core.ui.transition

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VideoSharedTransitionPolicyTest {

    @Test
    fun coverSharedTransition_enabled_whenTransitionAndScopesAreReady() {
        assertTrue(
            shouldEnableVideoCoverSharedTransition(
                transitionEnabled = true,
                hasSharedTransitionScope = true,
                hasAnimatedVisibilityScope = true
            )
        )
        assertFalse(
            shouldEnableVideoCoverSharedTransition(
                transitionEnabled = true,
                hasSharedTransitionScope = false,
                hasAnimatedVisibilityScope = true
            )
        )
    }

    @Test
    fun metadataSharedTransition_keepsDefaultForNonHomeCallers() {
        assertEquals(VideoSharedTransitionProfile.COVER_AND_METADATA, resolveVideoSharedTransitionProfile())
        assertTrue(
            shouldEnableVideoMetadataSharedTransition(
                coverSharedEnabled = true,
                isQuickReturnLimited = false
            )
        )
    }

    @Test
    fun metadataSharedTransition_staysEnabledWhenQuickReturnLimitedForNonHomeCallers() {
        assertTrue(
            shouldEnableVideoMetadataSharedTransition(
                coverSharedEnabled = true,
                isQuickReturnLimited = true
            )
        )
    }

    @Test
    fun metadataSharedTransition_disabledWhenCardContainerOwnsSharedBounds() {
        assertFalse(
            shouldEnableVideoMetadataSharedTransition(
                coverSharedEnabled = true,
                isQuickReturnLimited = false,
                useCardContainerSharedBounds = true
            )
        )
    }

    @Test
    fun homeVideoTransition_usesCoverAsPrimaryAnchor() {
        val policy = resolveVideoSharedTransitionOwnership(
            sourceRoute = "home",
            coverSharedEnabled = true,
            isQuickReturnLimited = false
        )

        assertTrue(policy.useCoverSharedBounds)
        assertFalse(policy.useMetadataSharedBounds)
    }

    @Test
    fun nonHomeVideoTransition_keepsMetadataSharedBoundsWhenAvailable() {
        val policy = resolveVideoSharedTransitionOwnership(
            sourceRoute = "search",
            coverSharedEnabled = true,
            isQuickReturnLimited = false
        )

        assertTrue(policy.useCoverSharedBounds)
        assertTrue(policy.useMetadataSharedBounds)
    }

    @Test
    fun detailContentReveal_usesLightVisibleMotionForHomeSharedTransition() {
        val motion = resolveVideoDetailContentRevealMotion(
            sourceRoute = "home",
            transitionEnabled = true
        )

        assertTrue(motion.enabled)
        assertEquals(40, motion.delayMillis)
        assertEquals(220, motion.durationMillis)
        assertEquals(14, motion.slideOffsetDp)
        assertEquals(0.985f, motion.initialScale, 0.0001f)
    }

    @Test
    fun homeSharedTransitionMotion_usesShortCoverPrimaryTimeline() {
        val motion = resolveHomeVideoSharedTransitionMotionSpec(
            sourceRoute = "home",
            transitionEnabled = true
        )

        assertTrue(motion.enabled)
        assertEquals(360, motion.durationMillis)
        assertEquals(40, motion.contentDelayMillis)
        assertEquals(220, motion.contentDurationMillis)
        assertEquals(14, motion.contentSlideOffsetDp)
        assertEquals(0.985f, motion.contentInitialScale, 0.0001f)
    }

    @Test
    fun homeSharedTransitionMotion_disabledForNonHomeSources() {
        val motion = resolveHomeVideoSharedTransitionMotionSpec(
            sourceRoute = "search",
            transitionEnabled = true
        )

        assertFalse(motion.enabled)
        assertEquals(0, motion.durationMillis)
    }

    @Test
    fun homeSharedTransitionCornerSpec_softlyConvergesFromCardToPlayer() {
        val corner = resolveHomeVideoSharedTransitionCornerSpec(
            sourceRoute = "home",
            transitionEnabled = true
        )

        assertTrue(corner.enabled)
        assertEquals(16, corner.startCornerDp)
        assertEquals(12, corner.endCornerDp)
    }

    @Test
    fun detailContentReveal_disabledWithoutSharedTransition() {
        val motion = resolveVideoDetailContentRevealMotion(
            sourceRoute = "home",
            transitionEnabled = false
        )

        assertFalse(motion.enabled)
        assertEquals(0, motion.delayMillis)
        assertEquals(0, motion.slideOffsetDp)
        assertEquals(1f, motion.initialScale, 0.0001f)
    }

    @Test
    fun sharedCoverAspectRatio_defaultsToHomeCardSixteenByTen() {
        assertEquals(1.6f, VIDEO_SHARED_COVER_ASPECT_RATIO, 0.0001f)
    }
}
