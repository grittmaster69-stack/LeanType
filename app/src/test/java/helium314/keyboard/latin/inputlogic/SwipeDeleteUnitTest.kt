// SPDX-License-Identifier: GPL-3.0-only
package helium314.keyboard.latin.inputlogic

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SwipeDeleteUnitTest {
    private fun result(before: String, after: String): String {
        val p = SwipeDeleteUnit.plan(before, after) ?: return before + after
        return before.dropLast(p.before) + p.replacement + after.drop(p.after)
    }

    @Test fun separatesPunctuationFromThePrecedingWord() {
        assertEquals("hello ", result("hello. ", ""))
        assertEquals("hello ", result("hello... ", ""))
        assertEquals("hello ", result("hello?! ", ""))
        assertEquals("hello", result("hello.", ""))
        assertEquals("hello ", result("hello👩‍🔧 ", ""))
        assertEquals("well-", result("well-known", ""))
    }

    @Test fun deletesTheWholeWordAtEveryCursorPosition() {
        for (i in 0..3) {
            val text = "one two three"
            assertEquals("one three", result(text.substring(0, 4 + i), text.substring(4 + i)))
        }
        assertEquals("world", result("", "hello world"))
        assertEquals("world", result("hel", "lo world"))
        assertEquals("one ", result("one partial", ""))
    }

    @Test fun respectsWordMarksAndInternalApostrophes() {
        assertEquals("one next", result("one don", "'t next"))
        assertEquals("one next", result("one we’", "re next"))
        assertEquals("next", result("cafe", "\u0301 next"))
        assertEquals("", result("𐐀word", ""))
    }

    @Test fun keepsNeighboringPunctuationAndParagraphs() {
        assertEquals("one . three", result("one tw", "o. three"))
        assertEquals("one \nthree", result("one tw", "o\nthree"))
        assertEquals("hello  world", result("hello. ", " world"))
        assertNull(SwipeDeleteUnit.plan("", ""))
        assertEquals("", result("   ", ""))
    }
}
