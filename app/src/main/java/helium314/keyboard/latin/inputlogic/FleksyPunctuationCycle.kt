package helium314.keyboard.latin.inputlogic

/**
 * Punctuation order and cycling state for an optional Fleksy-style gesture.
 *
 * This class deliberately does not edit an InputConnection: the caller must first
 * verify that the cursor still follows the punctuation + trailing space created
 * by double-space, then replace only the punctuation character atomically.
 */
internal class FleksyPunctuationCycle(
    configuredOrder: String = ".,?!:;"
) {
    val order: List<Char> = normalize(configuredOrder)
    private var index: Int = 0
    var active: Boolean = false
        private set

    /** Call immediately after a successful double-space period insertion. */
    fun begin() {
        index = order.indexOf('.').coerceAtLeast(0)
        active = true
    }

    /** Positive steps represent downward swipes, negative steps upward. */
    fun advance(steps: Int): Char? {
        if (!active || steps == 0) return null
        index = Math.floorMod(index + steps, order.size)
        return order[index]
    }

    /** Cancel on cursor movement, editing, or unrelated key input. */
    fun cancel() {
        active = false
    }

    companion object {
        fun normalize(value: String): List<Char> {
            val chars = value.filter { !it.isWhitespace() }.distinct()
            return if (chars.isNotEmpty() && '.' in chars) chars else ".,?!:;".toList()
        }
    }
}
