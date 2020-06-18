package dev.boldizsar.zsolt.android.form.factor.toggler

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.util.IconLoader
import org.jetbrains.android.sdk.AndroidSdkUtils
import java.util.concurrent.TimeUnit
import javax.swing.Icon

private const val TABLET_MODE = "dev.boldizsar.zsolt.android.form.factor.toggler.tablet_mode"

class TogglerAction : AnAction() {

    private val enableTabletModeIcon: Icon? = IconLoader.findIcon("/icons/phone_active.svg")
    private val enablePhoneModeIcon: Icon? = IconLoader.findIcon("/icons/tablet_active.svg")
    private val properties: PropertiesComponent = PropertiesComponent.getInstance()

    override fun update(event: AnActionEvent) {
        super.update(event)
        // Set the availability based on whether a project is open
        event.presentation.isEnabledAndVisible = event.project != null
    }

    override fun actionPerformed(event: AnActionEvent) {
        val adb = AndroidSdkUtils.getDebugBridge(event.project!!) ?: throw Exception("Missing ADB")

        if (adb.devices.isEmpty()) {
            NotificationProducer.showInfo("No device/emulator connected.")
            return
        }

        if (adb.devices.size > 1) {
            NotificationProducer.showInfo("More than one device/emulator. Please detach all but one.")
            return
        }

        try {
            val firstConnectedDevice = adb.devices[0]

            firstConnectedDevice.isTablet { isTablet ->
                if (isTablet) {
                    NotificationProducer.showInfo("Tablets are not yet supported. Please check back later.")
                } else {
                    val command: String = if (properties.getBoolean(TABLET_MODE)) "wm density reset" else "wm density 240"
                    firstConnectedDevice.executeShellCommand(command, NoOpShellOutputReceiver, 15L, TimeUnit.SECONDS)

                    if (properties.getBoolean(TABLET_MODE)) {
                        properties.setValue(TABLET_MODE, false)
                        event.presentation.apply {
                            icon = enableTabletModeIcon
                            text = "Enable Tablet Mode"
                        }
                    } else {
                        properties.setValue(TABLET_MODE, true)
                        event.presentation.apply {
                            icon = enablePhoneModeIcon
                            text = "Enable Phone Mode"
                        }
                    }
                }
            }
        } catch (exception: Exception) {
            NotificationProducer.showError("Operation failed. ${exception.message}")
        }
    }

}