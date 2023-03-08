package dev.boldizsar.zsolt.android.form.factor.toggler

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

private const val NOTIFICATION_GROUP_ID = "Android Form Factor Toggler"

object NotificationProducer {

    fun showInfo(project: Project?, message: String) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup(NOTIFICATION_GROUP_ID)
            .createNotification(message, NotificationType.INFORMATION)
            .notify(project)
    }

    fun showError(project: Project?, message: String) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup(NOTIFICATION_GROUP_ID)
            .createNotification(message, NotificationType.ERROR)
            .notify(project)
    }

}