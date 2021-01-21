package dev.boldizsar.zsolt.android.form.factor.toggler

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.util.IconLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.android.sdk.AndroidSdkUtils
import java.util.concurrent.TimeUnit
import javax.swing.Icon

private const val IS_TABLET_MODE = "dev.boldizsar.zsolt.android.form.factor.toggler.is_tablet_mode"

class TogglerAction : AnAction() {

    private val enableTabletModeIcon: Icon? = IconLoader.findIcon("/icons/phone_active.svg")
    private val enablePhoneModeIcon: Icon? = IconLoader.findIcon("/icons/tablet_active.svg")
    private val properties: PropertiesComponent = PropertiesComponent.getInstance()
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    init {
        properties.setValue(IS_TABLET_MODE, false)
    }

    override fun update(event: AnActionEvent) {
        super.update(event)
        // Set the availability based on whether a project is open
        event.presentation.isEnabledAndVisible = event.project != null
    }

    override fun actionPerformed(event: AnActionEvent) {
        val adb = AndroidSdkUtils.getDebugBridge(event.project!!)
        if (adb == null) {
            NotificationProducer.showInfo("Couldn't load Android tools. Make sure to open an Android project and try again.")
            return
        }

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

            coroutineScope.launch {
                val isTablet = firstConnectedDevice.awaitIsTablet()
                if (isTablet) {
                    NotificationProducer.showInfo("Tablets are not yet supported. Please check back later.")
                    return@launch
                }

                withContext(Dispatchers.IO) {
                    val command: String = if (properties.getBoolean(IS_TABLET_MODE)) "wm density reset" else "wm density 240"
                    firstConnectedDevice.executeShellCommand(command, NoOpShellOutputReceiver, 15L, TimeUnit.SECONDS)

                    withContext(Dispatchers.Default) {
                        properties.setValue(IS_TABLET_MODE, !properties.getBoolean(IS_TABLET_MODE))

                        refreshUiState(event)
                    }
                }
            }
        } catch (exception: Exception) {
            NotificationProducer.showError("Operation failed. ${exception.message}")
        }
    }

    private fun refreshUiState(event: AnActionEvent) {
        event.presentation.apply {
            icon = if (properties.getBoolean(IS_TABLET_MODE)) enablePhoneModeIcon else enableTabletModeIcon
            text = if (properties.getBoolean(IS_TABLET_MODE)) "Enable Phone Mode" else "Enable Tablet Mode"
        }
    }

}