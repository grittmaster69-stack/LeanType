// SPDX-License-Identifier: GPL-3.0-only
package helium314.keyboard.keyboard.internal

/** A single outstanding callback, canceled on release and on every new touch. */
internal class SwipeWordDeleteRepeater(
    private val post: (Runnable, Long) -> Unit,
    private val remove: (Runnable) -> Unit,
    private val deleteWord: () -> Boolean
) {
    private var callback: Runnable? = null

    fun start(delayMs: Long, intervalMs: Long) {
        stop()
        val task = object : Runnable {
            override fun run() {
                if (callback !== this) return
                if (!deleteWord()) {
                    stop()
                    return
                }
                if (callback === this) post(this, intervalMs.coerceIn(100L, 1000L))
            }
        }
        callback = task
        post(task, delayMs.coerceIn(100L, 1500L))
    }

    fun stop() {
        callback?.let(remove)
        callback = null
    }
}
