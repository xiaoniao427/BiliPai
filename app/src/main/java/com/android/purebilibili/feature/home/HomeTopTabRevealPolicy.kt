package com.android.purebilibili.feature.home

fun resolveHomeTopTabsRevealDelayMs(
    isReturningFromDetail: Boolean,
    cardTransitionEnabled: Boolean,
    isQuickReturnFromDetail: Boolean
): Long {
    // 返回首页不再做额外延迟；是否可见由当前折叠态统一裁决，避免下滑隐藏后返场闪现。
    return 0L
}

fun resolveHomeTopTabsVisible(
    isDelayedForCardSettle: Boolean,
    isForwardNavigatingToDetail: Boolean,
    isReturningFromDetail: Boolean,
    topTabsCollapsed: Boolean = false
): Boolean {
    if (topTabsCollapsed) return false
    if (isReturningFromDetail) return true
    return !isDelayedForCardSettle && !isForwardNavigatingToDetail
}
