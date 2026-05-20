package com.android.purebilibili.navigation3

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith

private const val NAV3_FALLBACK_FADE_MILLIS = 180
private const val NAV3_PREDICTIVE_FADE_MILLIS = 220

internal fun resolveBiliPaiNavContentTransform(
    routeTransition: BiliPaiNavRouteTransition
): ContentTransform {
    return when (routeTransition) {
        BiliPaiNavRouteTransition.NO_OP_SHARED_ELEMENT ->
            EnterTransition.None togetherWith ExitTransition.None
        BiliPaiNavRouteTransition.PREDICTIVE_PROGRESS ->
            fadeIn(animationSpec = tween(NAV3_PREDICTIVE_FADE_MILLIS)) togetherWith
                fadeOut(animationSpec = tween(NAV3_PREDICTIVE_FADE_MILLIS))
        BiliPaiNavRouteTransition.CLASSIC_CARD,
        BiliPaiNavRouteTransition.FALLBACK ->
            fadeIn(animationSpec = tween(NAV3_FALLBACK_FADE_MILLIS)) togetherWith
                fadeOut(animationSpec = tween(NAV3_FALLBACK_FADE_MILLIS))
    }
}
