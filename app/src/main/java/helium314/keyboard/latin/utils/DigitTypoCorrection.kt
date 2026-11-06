// SPDX-License-Identifier: GPL-3.0-only
package helium314.keyboard.latin.utils

/**
 * Allow the normal confidence checks to evaluate one stray interior digit.
 * This does not generate or force a correction: the dictionary must supply it.
 */
internal object DigitTypoCorrection {
    fun isLikelyTypo(typed: String, suggested: String?): Boolean {
        if (typed.length < 4 || suggested.isNullOrEmpty()) return false
        val digits = typed.indices.filter { typed[it].isDigit() }
        if (digits.size != 1) return false
        val index = digits.single()
        if (index == 0 || index == typed.lastIndex) return false
        val letters = typed.filterNot { it.isDigit() }
        if (letters.length < 3 || !letters.all { it.isLetter() }) return false
        // Preserve acronyms and mixed-case identifiers; permit lowercase and titlecase words.
        if (!letters.drop(1).all { it.isLowerCase() }) return false
        if (!suggested.all { it.isLetter() }) return false
        val prefix = typed.substring(0, index)
        val suffix = typed.substring(index + 1)
        if (!suggested.startsWith(prefix, ignoreCase = true) ||
            !suggested.endsWith(suffix, ignoreCase = true)) return false
        // The digit may be an accidental insertion or replace one or two letters.
        val replacementLength = suggested.length - prefix.length - suffix.length
        return replacementLength in 0..2
    }
}
