package dev.boldizsar.zsolt.android.form.factor.toggler

import com.android.ddmlib.IDevice
import com.android.ddmlib.MultiLineReceiver
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

private const val PHYSICAL_SIZE = "Physical size: "
private const val DELIMITER = "x"
private const val BASELINE_DENSITY = 160
private const val MINIMUM_TABLET_DENSITY = 600

suspend fun IDevice.awaitIsTablet(): Boolean = suspendCancellableCoroutine { continuation ->
    this.executeShellCommand("wm size", object : MultiLineReceiver() {
        override fun processNewLines(lines: Array<out String>?) {
            if (lines == null || lines.isEmpty() || (lines.isNotEmpty() && lines[0].isEmpty())) {
                continuation.cancel()
                return
            }

            for (line in lines) {
                if (line.startsWith(PHYSICAL_SIZE)) {
                    val (screenWidth, screenHeight) = line.substringAfter(PHYSICAL_SIZE).split(DELIMITER)
                    val densityMultiplier = this@awaitIsTablet.density / BASELINE_DENSITY.toDouble()
                    val widthDp = screenWidth.toInt() / densityMultiplier
                    val heightDp = screenHeight.toInt() / densityMultiplier

                    val isTablet = widthDp > MINIMUM_TABLET_DENSITY && heightDp > MINIMUM_TABLET_DENSITY
                    continuation.resume(isTablet)

                    break
                }
            }
        }

        override fun isCancelled() = false

    })

}