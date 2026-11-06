// SPDX-License-Identifier: GPL-3.0-only
package helium314.keyboard.keyboard.internal

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SwipeWordDeleteRepeaterTest {
    private class Harness {
        val pending = mutableListOf<Pair<Runnable, Long>>()
        var deletes = 0
        var remaining = 10
        val repeater = SwipeWordDeleteRepeater(
            post = { task, delay -> pending.add(task to delay); Unit },
            remove = { task -> pending.removeAll { it.first === task }; Unit },
            deleteWord = { if (remaining > 0) { remaining--; deletes++; true } else false }
        )
        fun tick() = pending.removeAt(0).first.run()
    }

    @Test fun waitsThenRepeatsAtConfiguredInterval() {
        val h = Harness()
        h.repeater.start(400, 250)
        assertEquals(0, h.deletes)
        assertEquals(400L, h.pending.single().second)
        h.tick()
        assertEquals(1, h.deletes)
        assertEquals(250L, h.pending.single().second)
        h.tick()
        assertEquals(2, h.deletes)
    }

    @Test fun releaseBeforeHoldDelayDoesNotDeleteAnotherWord() {
        val h = Harness()
        h.repeater.start(400, 250)
        val stale = h.pending.single().first
        h.repeater.stop()
        stale.run()
        assertEquals(0, h.deletes)
        assertTrue(h.pending.isEmpty())
    }

    @Test fun releaseWhileRepeatingStopsImmediately() {
        val h = Harness()
        h.repeater.start(400, 250)
        h.tick()
        val stale = h.pending.single().first
        h.repeater.stop()
        stale.run()
        assertEquals(1, h.deletes)
        assertTrue(h.pending.isEmpty())
    }

    @Test fun stopsAtBeginningOfText() {
        val h = Harness()
        h.remaining = 1
        h.repeater.start(400, 250)
        h.tick()
        h.tick()
        assertEquals(1, h.deletes)
        assertTrue(h.pending.isEmpty())
    }

    @Test fun aNewGestureInvalidatesAnOldCallback() {
        val h = Harness()
        h.repeater.start(400, 250)
        val stale = h.pending.single().first
        h.repeater.start(600, 300)
        stale.run()
        assertEquals(0, h.deletes)
        assertEquals(600L, h.pending.single().second)
        h.tick()
        assertEquals(300L, h.pending.single().second)
    }
}
