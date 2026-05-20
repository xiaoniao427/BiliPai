package com.android.purebilibili.navigation3

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BiliPaiReturnSessionStateTest {

    @Test
    fun videoSourceRouteIsStoredOutsideCardPositionManager() {
        val state = BiliPaiReturnSessionState()
            .recordVideoSourceRoute("search")

        assertEquals("search", state.lastVideoSourceRoute)
        assertFalse(state.isReturningFromDetail)
        assertFalse(state.isQuickReturnFromDetail)
    }

    @Test
    fun returnSessionMarksQuickReturnFromElapsedTime() {
        val state = BiliPaiReturnSessionState()
            .markDetailEntered(nowMillis = 1_000L)
            .markReturning(nowMillis = 1_450L)

        assertTrue(state.isReturningFromDetail)
        assertTrue(state.isQuickReturnFromDetail)
    }

    @Test
    fun clearReturningKeepsSourceRouteForNextSharedElementMatch() {
        val state = BiliPaiReturnSessionState()
            .recordVideoSourceRoute("home")
            .markDetailEntered(nowMillis = 1_000L)
            .markReturning(nowMillis = 2_000L)
            .clearReturning()

        assertEquals("home", state.lastVideoSourceRoute)
        assertFalse(state.isReturningFromDetail)
        assertFalse(state.isQuickReturnFromDetail)
    }
}
