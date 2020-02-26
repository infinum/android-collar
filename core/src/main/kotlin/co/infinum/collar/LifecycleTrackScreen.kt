@file:Suppress("ClassName")

package co.infinum.collar

import androidx.annotation.RestrictTo
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import java.io.Serializable

private object UninitializedValue

@RestrictTo(RestrictTo.Scope.LIBRARY)
class LifecycleTrackScreen<out T>(
    private val owner: LifecycleOwner,
    initializer: () -> T
) : Lazy<T>, Serializable {

    private var initializer: (() -> T)? = initializer

    @Volatile
    private var internalValue: Any? = UninitializedValue

    // final field is required to enable safe publication of constructed instance
    private val lock = this

    init {
        owner.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            fun onResume() {
                if (!isInitialized()) {
                    value
                }
                owner.lifecycle.removeObserver(this)
            }
        })
    }

    override val value: T
        get() {
            val oldValue = internalValue
            if (oldValue !== UninitializedValue) {
                @Suppress("UNCHECKED_CAST")
                return oldValue as T
            }

            return synchronized(lock) {
                val newValue = internalValue
                if (newValue !== UninitializedValue) {
                    @Suppress("UNCHECKED_CAST") (newValue as T)
                } else {
                    val typedValue = initializer!!()
                    internalValue = typedValue
                    initializer = null
                    typedValue
                }
            }
        }

    override fun isInitialized(): Boolean = internalValue !== UninitializedValue

    override fun toString(): String = if (isInitialized()) value.toString() else UninitializedValue.toString()
}