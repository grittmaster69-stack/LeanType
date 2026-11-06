// SPDX-License-Identifier: GPL-3.0-only
package helium314.keyboard.latin.inputlogic

/** UTF-16 lengths for one visible swipe-delete action around the actual cursor. */
internal object SwipeDeleteUnit {
    data class Plan(val before: Int, val after: Int, val replacement: String = "", val word: String? = null)

    fun plan(before: String, after: String): Plan? {
        val text = before + after
        val cursor = before.length
        if (text.isEmpty()) return null

        fun previous(index: Int) = index - Character.charCount(Character.codePointBefore(text, index))
        fun next(index: Int) = index + Character.charCount(Character.codePointAt(text, index))
        fun whitespace(index: Int) = Character.isWhitespace(Character.codePointAt(text, index))
        fun coreWord(index: Int): Boolean {
            if (index !in text.indices) return false
            var at = index
            var cp = Character.codePointAt(text, at)
            while (Character.getType(cp) in listOf(
                    Character.NON_SPACING_MARK.toInt(), Character.COMBINING_SPACING_MARK.toInt(),
                    Character.ENCLOSING_MARK.toInt())) {
                if (at == 0) return false
                at = previous(at)
                cp = Character.codePointAt(text, at)
            }
            return Character.isLetterOrDigit(cp)
        }
        fun word(index: Int): Boolean {
            if (index !in text.indices) return false
            if (coreWord(index)) return true
            val cp = Character.codePointAt(text, index)
            return (cp == '\''.code || cp == '’'.code) && index > 0 &&
                coreWord(previous(index)) && coreWord(next(index))
        }

        // A caret touching a word, including its start, targets that whole word.
        val left = if (cursor > 0) previous(cursor) else -1
        val anchor = when {
            left >= 0 && word(left) -> left
            cursor < text.length && word(cursor) && (left < 0 || whitespace(left)) -> cursor
            else -> {
                var i = cursor
                while (i > 0 && whitespace(previous(i))) i = previous(i)
                if (i == 0) -1 else previous(i)
            }
        }
        if (anchor < 0) return if (cursor > 0) Plan(cursor, 0) else null
        val isWord = word(anchor)
        var start = anchor
        var end = next(anchor)
        while (start > 0) {
            val i = previous(start)
            if (whitespace(i) || word(i) != isWord) break
            start = i
        }
        while (end < text.length && !whitespace(end) && word(end) == isWord) end = next(end)

        val tokenEnd = end
        end = maxOf(end, cursor)
        if (isWord) {
            // Keep paragraphs, but avoid a doubled gap after removing a word in the middle.
            while (end < text.length && whitespace(end) &&
                text[end] != '\n' && text[end] != '\r') end = next(end)
        }
        // Removing punctuation must not remove the space separating its neighboring words.
        val keepSpacing = if (!isWord && tokenEnd < cursor) text.substring(tokenEnd, cursor) else ""
        return Plan(cursor - start, end - cursor, keepSpacing,
            if (isWord) text.substring(start, tokenEnd) else null)
    }
}
