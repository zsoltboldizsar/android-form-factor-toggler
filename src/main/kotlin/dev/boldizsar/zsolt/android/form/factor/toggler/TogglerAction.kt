package dev.boldizsar.zsolt.android.form.factor.toggler

import com.android.ddmlib.IDevice
import com.android.tools.idea.streaming.emulator.EmulatorToolWindowPanel
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.IconLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.android.sdk.AndroidSdkUtils
import java.util.concurrent.TimeUnit
import javax.swing.Icon

private const val IS_TABLET_MODE = "dev.boldizsar.zsolt.android.form.factor.toggler.is_tablet_mode"

class TogglerAction : AnAction(), Disposable {

    private val enableTabletModeIcon: Icon = IconLoader.getIcon("/icons/phone_active.svg", javaClass)
    private val enablePhoneModeIcon: Icon = IconLoader.getIcon("/icons/tablet_active.svg", javaClass)
    private val properties: PropertiesComponent = PropertiesComponent.getInstance()
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    init {
        Disposer.register(service<DisposingService>(), this)
    }

    override fun update(event: AnActionEvent) {
        super.update(event)
        // Set the availability based on whether a project is open
        event.presentation.isEnabledAndVisible = event.project != null
    }

    override fun actionPerformed(event: AnActionEvent) {
        val adb = AndroidSdkUtils.getDebugBridge(event.project!!)
        if (adb == null) {
            NotificationProducer.showInfo(event.project, "Couldn't load Android tools. Make sure to open an Android project and try again.")
            return
        }

        if (adb.devices.isEmpty()) {
            NotificationProducer.showInfo(event.project, "No device/emulator connected.")
            return
        }

        try {
            if (event.inputEvent == null) {
                NotificationProducer.showError(event.project, "Sorry, for now, you cannot use the action as a command. Please use it via Emulator/Device toolbar action.")
                return
            }

            val deviceInFocus = getDeviceInFocus(event)
            val currentDevice = adb.devices.find { it.serialNumber == deviceInFocus.serialNumber }

            if (currentDevice == null) {
                NotificationProducer.showError(event.project, "Failed to identify current device.")
                return
            }

            coroutineScope.launch {
                val isTablet = currentDevice.isTablet()
                if (isTablet) {
                    NotificationProducer.showInfo(event.project, "Tablets are not yet supported. Please check back later.")
                    return@launch
                }

                withContext(Dispatchers.IO) {
                    val command: String = if (properties.getBoolean(IS_TABLET_MODE + currentDevice.serialNumber)) "wm density reset" else "wm density 240"
                    currentDevice.executeShellCommand(command, NoOpShellOutputReceiver, 15L, TimeUnit.SECONDS)

                    withContext(Dispatchers.Default) {
                        properties.setValue(IS_TABLET_MODE + currentDevice.serialNumber, !properties.getBoolean(IS_TABLET_MODE + currentDevice.serialNumber))

                        refreshUiState(event, currentDevice)
                    }
                }
            }
        } catch (exception: Exception) {
            NotificationProducer.showError(event.project, "Operation failed. ${exception.message}")
        }
    }

    private fun refreshUiState(event: AnActionEvent, device: IDevice) {
        event.presentation.apply {
            icon = if (properties.getBoolean(IS_TABLET_MODE + device.serialNumber)) enablePhoneModeIcon else enableTabletModeIcon
            text = if (properties.getBoolean(IS_TABLET_MODE + device.serialNumber)) "Enable Phone Mode" else "Enable Tablet Mode"
        }
    }

    override fun dispose() {
        // Clean-up resources here (listeners, heavy objects, etc.)
    }

    private fun getDeviceInFocus(event: AnActionEvent): AndroidDevice {
        val serialNumber = (event.inputEvent.component.parent.parent.parent as EmulatorToolWindowPanel).emulator.emulatorId.serialNumber
        return AndroidDevice(serialNumber = serialNumber)
    }

}