package helium314.keyboard.latin.inputlogic

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FleksyPunctuationCycleTest {
    @Test fun defaultOrderWrapsInBothDirections() {
        val cycle = FleksyPunctuationCycle()
        assertNull(cycle.advance(1))
        cycle.begin()
        assertEquals(",?!:;.".toList(), (1..6).map { cycle.advance(1) })
        assertEquals(';', cycle.advance(-1))
        cycle.cancel()
        assertNull(cycle.advance(1))
    }

    @Test fun customOrderStartsAtPeriodAndInvalidOrderFallsBack() {
        val cycle = FleksyPunctuationCycle("!.,?")
        cycle.begin()
        assertEquals(',', cycle.advance(1))
        assertEquals(".,?!:;".toList(), FleksyPunctuationCycle.normalize(""))
        assertEquals(".,?!:;".toList(), FleksyPunctuationCycle.normalize("abc"))
    }
}
