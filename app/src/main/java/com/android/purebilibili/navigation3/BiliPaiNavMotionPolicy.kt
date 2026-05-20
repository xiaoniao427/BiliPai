package com.android.purebilibili.navigation3

internal enum class BiliPaiNavMotionMode {
    CARD_DISABLED,
    CLASSIC_CARD,
    PREDICTIVE_STABLE
}

internal enum class BiliPaiNavRouteTransition {
    NO_OP_SHARED_ELEMENT,
    HOME_VIDEO_SHEET_FORWARD,
    HOME_VIDEO_SHEET_RETURN,
    PREDICTIVE_PROGRESS,
    CLASSIC_CARD,
    FALLBACK
}

internal data class BiliPaiNavMotionDecision(
    val mode: BiliPaiNavMotionMode,
    val routeTransition: BiliPaiNavRouteTransition,
    val interceptSystemBack: Boolean
)

internal fun resolveBiliPaiNavMotionMode(
    predictiveBackAnimationEnabled: Boolean,
    cardTransitionEnabled: Boolean
): BiliPaiNavMotionMode {
    if (!cardTransitionEnabled) return BiliPaiNavMotionMode.CARD_DISABLED
    return if (predictiveBackAnimationEnabled) {
        BiliPaiNavMotionMode.PREDICTIVE_STABLE
    } else {
        BiliPaiNavMotionMode.CLASSIC_CARD
    }
}

internal fun resolveBiliPaiNavMotionDecision(
    fromKey: BiliPaiNavKey?,
    toKey: BiliPaiNavKey?,
    predictiveBackAnimationEnabled: Boolean,
    cardTransitionEnabled: Boolean,
    sharedTransitionReady: Boolean,
    appBackActionRequiresInterception: Boolean = false
): BiliPaiNavMotionDecision {
    val mode = resolveBiliPaiNavMotionMode(
        predictiveBackAnimationEnabled = predictiveBackAnimationEnabled,
        cardTransitionEnabled = cardTransitionEnabled
    )
    val isVideoToCardReturn = fromKey is BiliPaiNavKey.VideoDetail &&
        toKey != null &&
        isCardReturnTargetNavKey(toKey)
    val isCardToVideoForward = fromKey != null &&
        isCardReturnTargetNavKey(fromKey) &&
        toKey is BiliPaiNavKey.VideoDetail
    val routeTransition = when {
        cardTransitionEnabled &&
            sharedTransitionReady &&
            (isVideoToCardReturn || isCardToVideoForward) ->
            BiliPaiNavRouteTransition.NO_OP_SHARED_ELEMENT
        mode == BiliPaiNavMotionMode.PREDICTIVE_STABLE ->
            BiliPaiNavRouteTransition.PREDICTIVE_PROGRESS
        mode == BiliPaiNavMotionMode.CLASSIC_CARD ->
            BiliPaiNavRouteTransition.CLASSIC_CARD
        else -> BiliPaiNavRouteTransition.FALLBACK
    }

    return BiliPaiNavMotionDecision(
        mode = mode,
        routeTransition = routeTransition,
        interceptSystemBack = shouldInterceptSystemBackForNavigation3(
            mode = mode,
            appBackActionRequiresInterception = appBackActionRequiresInterception
        )
    )
}

internal fun resolveBiliPaiNavDisplayPredictivePopRouteTransition(
    motionMode: BiliPaiNavMotionMode,
    sourceMetadata: BiliPaiNavSourceMetadata,
    fromKey: BiliPaiNavKey?,
    toKey: BiliPaiNavKey?
): BiliPaiNavRouteTransition {
    val fromVideoKey = fromKey as? BiliPaiNavKey.VideoDetail
    val normalizedSourceRoute = sourceMetadata.sourceRoute?.substringBefore("?")
    val normalizedVideoRoute = fromVideoKey?.sourceRoute?.substringBefore("?")
    val sourceMatchesCurrentVideo = fromVideoKey != null &&
        normalizedSourceRoute != null &&
        normalizedVideoRoute == normalizedSourceRoute &&
        sourceMetadata.sourceKey == "$normalizedSourceRoute:${fromVideoKey.bvid}"
    val sharedReadyVideoToSourceCard = sourceMetadata.sharedTransitionReady &&
        sourceMatchesCurrentVideo &&
        toKey != null &&
        isCardReturnTargetNavKey(toKey)
    if (sharedReadyVideoToSourceCard) {
        return BiliPaiNavRouteTransition.NO_OP_SHARED_ELEMENT
    }
    return if (shouldUseNavigation3PredictivePop(motionMode)) {
        BiliPaiNavRouteTransition.PREDICTIVE_PROGRESS
    } else {
        BiliPaiNavRouteTransition.CLASSIC_CARD
    }
}

internal fun shouldInterceptSystemBackForNavigation3(
    mode: BiliPaiNavMotionMode,
    appBackActionRequiresInterception: Boolean
): Boolean {
    if (appBackActionRequiresInterception) return true
    return mode == BiliPaiNavMotionMode.CLASSIC_CARD
}

internal fun shouldUseClassicBackForVideoSharedElementReturn(
    currentKey: BiliPaiNavKey?,
    previousKey: BiliPaiNavKey?,
    cardTransitionEnabled: Boolean
): Boolean {
    return cardTransitionEnabled &&
        currentKey is BiliPaiNavKey.VideoDetail &&
        previousKey != null &&
        isCardReturnTargetNavKey(previousKey)
}

internal fun shouldUseNavigation3PredictivePop(mode: BiliPaiNavMotionMode): Boolean {
    return mode == BiliPaiNavMotionMode.PREDICTIVE_STABLE
}
