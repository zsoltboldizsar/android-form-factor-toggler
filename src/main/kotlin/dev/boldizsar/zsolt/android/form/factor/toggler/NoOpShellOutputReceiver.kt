package dev.boldizsar.zsolt.android.form.factor.toggler

import com.android.ddmlib.MultiLineReceiver

object NoOpShellOutputReceiver : MultiLineReceiver() {
    override fun processNewLines(p0: Array<out String>?) {
        // Do nothing
    }

    override fun isCancelled() = false
}