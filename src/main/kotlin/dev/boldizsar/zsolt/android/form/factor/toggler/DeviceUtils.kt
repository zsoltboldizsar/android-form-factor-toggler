package dev.boldizsar.zsolt.android.form.factor.toggler

import com.android.ddmlib.IDevice
import com.android.ddmlib.MultiLineReceiver

private const val PHYSICAL_SIZE = "Physical size: "
private const val DELIMITER = "x"
private const val BASELINE_DENSITY = 160
private const val MINIMUM_TABLET_DENSITY = 600

fun IDevice.isTablet(callback: (isTablet: Boolean) -> Unit) {
    this.executeShellCommand("wm size", object : MultiLineReceiver() {

        override fun processNewLines(lines: Array<out String>?) {
            if (lines == null) return
            if (lines.isNotEmpty() && lines[0].isEmpty()) return

            var isTablet = false
            for (line in lines) {
                if (line.startsWith(PHYSICAL_SIZE)) {
                    val (screenWidth, screenHeight) = line.substringAfter(PHYSICAL_SIZE).split(DELIMITER)
                    val densityMultiplier = this@isTablet.density / BASELINE_DENSITY.toDouble()
                    val widthDp = screenWidth.toInt() / densityMultiplier
                    val heightDp = screenHeight.toInt() / densityMultiplier

                    isTablet = widthDp > MINIMUM_TABLET_DENSITY && heightDp > MINIMUM_TABLET_DENSITY
                    break
                }
            }
            callback(isTablet)
        }

        override fun isCancelled() = false

    })

}