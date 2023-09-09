package dev.boldizsar.zsolt.android.form.factor.toggler

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup

class AddToEmulatorAndDeviceToolbarAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val togglerAction = event.actionManager.getAction("dev.boldizsar.zsolt.android.form.factor.toggler.TogglerAction")

        // Older versions of Android Studio [2020.3.1 up to 2022.1.1] where only Emulator Toolbar existed.
        val emulatorToolbarGroup = event.actionManager.getAction("EmulatorToolbar") as? DefaultActionGroup
        emulatorToolbarGroup.addOrIgnore(togglerAction)

        // Android Studio [2022.2.1] where Device Mirroring was introduced with its initial tool window name.
        val deviceToolbarGroup = event.actionManager.getAction("DeviceToolbar") as? DefaultActionGroup
        deviceToolbarGroup.addOrIgnore(togglerAction)

        // Newer versions of Android Studio [2022.2.1+) where Device Mirroring got a new tool window name (Running Devices).
        val runningPhysicalDevicesGroup = event.actionManager.getActionOrStub("StreamingToolbarPhysicalDevice") as? DefaultActionGroup
        val runningVirtualDevicesGroup = event.actionManager.getActionOrStub("StreamingToolbarVirtualDevice") as? DefaultActionGroup
        runningPhysicalDevicesGroup.addOrIgnore(togglerAction)
        runningVirtualDevicesGroup.addOrIgnore(togglerAction)
    }

    private fun DefaultActionGroup?.addOrIgnore(action: AnAction) {
        this ?: return
        if (containsAction(action)) return

        add(action)
    }

}