package com.android.purebilibili.core.ui.transition

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.launch

@Composable
internal fun Modifier.videoCardReturnRebound(
    bvid: String,
    sourceRoute: String?
): Modifier {
    val density = LocalDensity.current
    val returnTransitionState = LocalVideoCardReturnTransitionState.current
    val shouldPlayReturnRebound = shouldPlayVideoCardReturnRebound(
        cardBvid = bvid,
        cardSourceRoute = sourceRoute,
        returningSourceKey = returnTransitionState.sourceKey,
        returningSourceRoute = returnTransitionState.sourceRoute,
        isReturningFromDetail = returnTransitionState.isReturningFromDetail,
        sharedTransitionReady = returnTransitionState.sharedTransitionReady
    )
    val returnReboundSpec = remember(shouldPlayReturnRebound) {
        resolveVideoCardReturnReboundSpec(shouldPlayReturnRebound)
    }
    val returnReboundScale = remember(bvid, sourceRoute) { Animatable(1f) }
    val returnReboundTranslationY = remember(bvid, sourceRoute) { Animatable(0f) }

    LaunchedEffect(
        shouldPlayReturnRebound,
        returnTransitionState.sourceKey,
        returnReboundSpec
    ) {
        if (!returnReboundSpec.enabled) {
            returnReboundScale.snapTo(1f)
            returnReboundTranslationY.snapTo(0f)
            return@LaunchedEffect
        }

        returnReboundScale.snapTo(returnReboundSpec.startScale)
        returnReboundTranslationY.snapTo(returnReboundSpec.startTranslationYDp)
        val reboundSpring = spring<Float>(
            dampingRatio = returnReboundSpec.dampingRatio,
            stiffness = returnReboundSpec.stiffness
        )
        launch {
            returnReboundScale.animateTo(
                targetValue = 1f,
                animationSpec = reboundSpring
            )
        }
        launch {
            returnReboundTranslationY.animateTo(
                targetValue = 0f,
                animationSpec = reboundSpring
            )
        }
    }

    return graphicsLayer {
        scaleX = returnReboundScale.value
        scaleY = returnReboundScale.value
        translationY = returnReboundTranslationY.value * density.density
    }
}
