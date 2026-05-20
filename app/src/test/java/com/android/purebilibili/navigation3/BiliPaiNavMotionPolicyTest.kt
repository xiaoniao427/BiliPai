package com.android.purebilibili.navigation3

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BiliPaiNavMotionPolicyTest {

    @Test
    fun predictiveEnabledWithCards_usesPredictiveStableMode() {
        assertEquals(
            BiliPaiNavMotionMode.PREDICTIVE_STABLE,
            resolveBiliPaiNavMotionMode(
                predictiveBackAnimationEnabled = true,
                cardTransitionEnabled = true
            )
        )
    }

    @Test
    fun predictiveDisabledWithCards_usesClassicCardMode() {
        assertEquals(
            BiliPaiNavMotionMode.CLASSIC_CARD,
            resolveBiliPaiNavMotionMode(
                predictiveBackAnimationEnabled = false,
                cardTransitionEnabled = true
            )
        )
    }

    @Test
    fun sharedElementReady_videoReturn_prefersNoOpRouteLayer() {
        val decision = resolveBiliPaiNavMotionDecision(
            fromKey = BiliPaiNavKey.VideoDetail("BV1"),
            toKey = BiliPaiNavKey.Home,
            predictiveBackAnimationEnabled = true,
            cardTransitionEnabled = true,
            sharedTransitionReady = true
        )

        assertEquals(BiliPaiNavRouteTransition.NO_OP_SHARED_ELEMENT, decision.routeTransition)
        assertFalse(decision.interceptSystemBack)
    }

    @Test
    fun videoSharedElementReturn_usesClassicAppBackEvenWhenPredictiveIsEnabled() {
        assertTrue(
            shouldUseClassicBackForVideoSharedElementReturn(
                currentKey = BiliPaiNavKey.VideoDetail("BV1", sourceRoute = "home"),
                previousKey = BiliPaiNavKey.Home,
                cardTransitionEnabled = true
            )
        )
        assertTrue(
            shouldUseClassicBackForVideoSharedElementReturn(
                currentKey = BiliPaiNavKey.VideoDetail("BV1", sourceRoute = "dynamic"),
                previousKey = BiliPaiNavKey.Dynamic,
                cardTransitionEnabled = true
            )
        )
        assertFalse(
            shouldUseClassicBackForVideoSharedElementReturn(
                currentKey = BiliPaiNavKey.VideoDetail("BV1", sourceRoute = "home"),
                previousKey = BiliPaiNavKey.Home,
                cardTransitionEnabled = false
            )
        )
        assertFalse(
            shouldUseClassicBackForVideoSharedElementReturn(
                currentKey = BiliPaiNavKey.Settings,
                previousKey = BiliPaiNavKey.Home,
                cardTransitionEnabled = true
            )
        )
    }

    @Test
    fun navDisplayPredictivePop_sharedReadyVideoReturn_keepsRouteLayerNoOp() {
        val transition = resolveBiliPaiNavDisplayPredictivePopRouteTransition(
            motionMode = BiliPaiNavMotionMode.PREDICTIVE_STABLE,
            sourceMetadata = BiliPaiNavSourceMetadata(
                sourceKey = "history:BV1",
                sourceRoute = "history",
                clickedBoundsRecorded = true,
                cardFullyVisible = true
            ),
            fromKey = BiliPaiNavKey.VideoDetail("BV1", sourceRoute = "history"),
            toKey = BiliPaiNavKey.History
        )

        assertEquals(BiliPaiNavRouteTransition.NO_OP_SHARED_ELEMENT, transition)
    }

    @Test
    fun navDisplayPredictivePop_withoutSharedReady_keepsPredictiveRouteLayer() {
        val transition = resolveBiliPaiNavDisplayPredictivePopRouteTransition(
            motionMode = BiliPaiNavMotionMode.PREDICTIVE_STABLE,
            sourceMetadata = BiliPaiNavSourceMetadata(
                sourceKey = "history:BV1",
                sourceRoute = "history",
                clickedBoundsRecorded = true,
                cardFullyVisible = false
            ),
            fromKey = BiliPaiNavKey.VideoDetail("BV1", sourceRoute = "history"),
            toKey = BiliPaiNavKey.History
        )

        assertEquals(BiliPaiNavRouteTransition.PREDICTIVE_PROGRESS, transition)
    }

    @Test
    fun navDisplayPredictivePop_withStaleVideoSource_keepsPredictiveRouteLayer() {
        val transition = resolveBiliPaiNavDisplayPredictivePopRouteTransition(
            motionMode = BiliPaiNavMotionMode.PREDICTIVE_STABLE,
            sourceMetadata = BiliPaiNavSourceMetadata(
                sourceKey = "history:BV1",
                sourceRoute = "history",
                clickedBoundsRecorded = true,
                cardFullyVisible = true
            ),
            fromKey = BiliPaiNavKey.VideoDetail("BV2", sourceRoute = "history"),
            toKey = BiliPaiNavKey.History
        )

        assertEquals(BiliPaiNavRouteTransition.PREDICTIVE_PROGRESS, transition)
    }

    @Test
    fun sharedElementReady_homeVideoForward_prefersNoOpRouteLayer() {
        val decision = resolveBiliPaiNavMotionDecision(
            fromKey = BiliPaiNavKey.Home,
            toKey = BiliPaiNavKey.VideoDetail("BV1", sourceRoute = "home"),
            predictiveBackAnimationEnabled = false,
            cardTransitionEnabled = true,
            sharedTransitionReady = true
        )

        assertEquals(BiliPaiNavRouteTransition.NO_OP_SHARED_ELEMENT, decision.routeTransition)
        assertTrue(decision.interceptSystemBack)
    }

    @Test
    fun classicCardMode_interceptsSystemBackSoNavDisplayDoesNotOwnPrediction() {
        val decision = resolveBiliPaiNavMotionDecision(
            fromKey = BiliPaiNavKey.VideoDetail("BV1"),
            toKey = BiliPaiNavKey.Home,
            predictiveBackAnimationEnabled = false,
            cardTransitionEnabled = true,
            sharedTransitionReady = false
        )

        assertEquals(BiliPaiNavMotionMode.CLASSIC_CARD, decision.mode)
        assertEquals(BiliPaiNavRouteTransition.CLASSIC_CARD, decision.routeTransition)
        assertTrue(decision.interceptSystemBack)
    }

    @Test
    fun appBackActionInterception_winsEvenWhenPredictiveBackIsEnabled() {
        assertTrue(
            shouldInterceptSystemBackForNavigation3(
                mode = BiliPaiNavMotionMode.PREDICTIVE_STABLE,
                appBackActionRequiresInterception = true
            )
        )
        assertFalse(
            shouldInterceptSystemBackForNavigation3(
                mode = BiliPaiNavMotionMode.PREDICTIVE_STABLE,
                appBackActionRequiresInterception = false
            )
        )
    }
}
