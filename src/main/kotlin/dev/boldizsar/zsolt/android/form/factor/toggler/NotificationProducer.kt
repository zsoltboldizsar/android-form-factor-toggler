package dev.boldizsar.zsolt.android.form.factor.toggler

import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType

private const val DISPLAY_ID = "Android Form Factor Toggler"
private const val TITLE = "Android Form Factor Toggler"
private const val EMPTY_SUBTITLE = ""

object NotificationProducer {

    fun showInfo(message: String) {
        NotificationGroup.balloonGroup(DISPLAY_ID)
            .createNotification(TITLE, EMPTY_SUBTITLE, message, NotificationType.INFORMATION)
            .notify(null)
    }

    fun showError(message: String) {
        NotificationGroup.balloonGroup(DISPLAY_ID)
            .createNotification(TITLE, EMPTY_SUBTITLE, message, NotificationType.ERROR)
            .notify(null)
    }

}