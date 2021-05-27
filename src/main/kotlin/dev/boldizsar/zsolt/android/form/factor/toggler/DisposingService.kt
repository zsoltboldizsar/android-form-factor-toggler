package dev.boldizsar.zsolt.android.form.factor.toggler

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service

@Service
class DisposingService : Disposable {
    override fun dispose() {
        // Not doing anything currently as this only helps dispose resources in TogglerAction.
    }
}