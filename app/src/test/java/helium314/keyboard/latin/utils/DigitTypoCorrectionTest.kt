// SPDX-License-Identifier: GPL-3.0-only
package helium314.keyboard.latin.utils

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class DigitTypoCorrectionTest {
    @Test fun allowsSingleInteriorDigitInWordLikeTypos() {
        for ((typed, corrected) in listOf(
            "Disas9mbled" to "Disassembled",
            "disas9mbled" to "disassembled",
            "he1lo" to "hello",
            "he9llo" to "hello",
            "w9rd" to "word"
        )) assertTrue(DigitTypoCorrection.isLikelyTypo(typed, corrected), typed)
    }

    @Test fun preservesNumbersCodesAndNonWordSyntax() {
        for (typed in listOf("12345", "S25", "L5P", "MK9G1000", "abc123", "123abc",
            "iPhone9", "A9CD", "abc9Def", "3d", "b2b", "he11o", "part-9name", "name9@example.com")) {
            assertFalse(DigitTypoCorrection.isLikelyTypo(typed, "hello"), typed)
        }
        assertFalse(DigitTypoCorrection.isLikelyTypo("S25", "See"))
        assertFalse(DigitTypoCorrection.isLikelyTypo("L5P", "Lap"))
        assertFalse(DigitTypoCorrection.isLikelyTypo("A9CD", "Acd"))
        assertFalse(DigitTypoCorrection.isLikelyTypo("abc9Def", "Abcdef"))
    }

    @Test fun requiresAnOtherwiseMatchingLetterOnlySuggestion() {
        assertFalse(DigitTypoCorrection.isLikelyTypo("he9lo", null))
        assertFalse(DigitTypoCorrection.isLikelyTypo("he9lo", "help"))
        assertFalse(DigitTypoCorrection.isLikelyTypo("he9lo", "he9lo"))
        assertFalse(DigitTypoCorrection.isLikelyTypo("he9lo", "he three lo"))
        assertFalse(DigitTypoCorrection.isLikelyTypo("he9lo", "hextralo"))
    }
}
