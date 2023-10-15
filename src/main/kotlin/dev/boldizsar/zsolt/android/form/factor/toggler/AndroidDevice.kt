package dev.boldizsar.zsolt.android.form.factor.toggler

data class AndroidDevice(
    val serialNumber: String,
    val displayMode: DisplayMode = DisplayMode.PHONE
)