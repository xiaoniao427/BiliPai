package com.android.purebilibili.feature.home

import java.io.File
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HomeHeroFlyoutStructureTest {

    @Test
    fun homeScreenDelaysNavigationUntilHeroFlyoutFinishes() {
        val source = loadSource("app/src/main/java/com/android/purebilibili/feature/home/HomeScreen.kt")
        val flyoutEffectSource = source
            .substringAfter("LaunchedEffect(pendingHeroFlyoutRequest)")
            .substringBefore("//  包装 onVideoClick")
        val clickWrapperSource = source
            .substringAfter("val wrappedOnVideoClick")
            .substringBefore("val onTodayWatchVideoClick")

        assertTrue(source.contains("pendingHeroFlyoutRequest"))
        assertTrue(source.contains("shouldRunHomeHeroFlyoutBeforeNavigation(request)"))
        assertTrue(source.contains("delay(resolveHomeHeroFlyoutNavigationDelayMillis())"))
        assertTrue(source.contains("onVideoClick(pendingRequest)"))
        assertTrue(source.contains("heroFlyoutBvid = pendingHeroFlyoutRequest?.bvid"))
        assertTrue(flyoutEffectSource.contains("hideTopTabsForForwardDetailNav = true"))
        assertTrue(flyoutEffectSource.contains("setBottomBarVisible(false)"))
        assertTrue(flyoutEffectSource.contains("isVideoNavigating = true"))
        assertTrue(clickWrapperSource.indexOf("pendingHeroFlyoutRequest = request") < clickWrapperSource.indexOf("hideTopTabsForForwardDetailNav = true"))
    }

    @Test
    fun ordinaryHomeVideoCardConsumesHeroFlyoutState() {
        val pageSource = loadSource("app/src/main/java/com/android/purebilibili/feature/home/HomeCategoryPage.kt")
        val cardSource = loadSource("app/src/main/java/com/android/purebilibili/feature/home/components/cards/VideoCard.kt")

        assertTrue(pageSource.contains("heroFlyoutBvid: String? = null"))
        assertTrue(pageSource.contains("heroFlyoutActive = heroFlyoutBvid == video.bvid"))
        assertTrue(cardSource.contains("heroFlyoutActive: Boolean = false"))
        assertTrue(cardSource.contains("resolveHomeHeroFlyoutFrame("))
    }

    @Test
    fun ordinaryHomeVideoCardUsesCoverPrimarySharedTransitionPolicy() {
        val cardSource = loadSource("app/src/main/java/com/android/purebilibili/feature/home/components/cards/VideoCard.kt")

        assertTrue(cardSource.contains("resolveHomeVideoSharedTransitionMotionSpec("))
        assertTrue(cardSource.contains("resolveHomeVideoSharedTransitionCornerSpec("))
        assertTrue(cardSource.contains("durationMillis = homeSharedTransitionMotionSpec.durationMillis"))
        assertTrue(cardSource.contains("videoCoverSharedElementKey("))
        assertFalse(cardSource.contains("使用 renderInSharedTransitionScopeOverlayOption 控制可见性"))
    }

    private fun loadSource(path: String): String {
        val normalizedPath = path.removePrefix("app/")
        val sourceFile = listOf(
            File(path),
            File(normalizedPath)
        ).firstOrNull { it.exists() }
        require(sourceFile != null) { "Cannot locate $path from ${File(".").absolutePath}" }
        return sourceFile.readText()
    }
}
