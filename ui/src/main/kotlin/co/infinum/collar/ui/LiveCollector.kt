package co.infinum.collar.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.annotation.CallSuper
import co.infinum.collar.Collector
import co.infinum.collar.Event
import co.infinum.collar.Property
import co.infinum.collar.Screen
import co.infinum.collar.ui.data.room.entity.CollarEntity
import co.infinum.collar.ui.data.room.entity.EntityType
import co.infinum.collar.ui.data.room.repository.EntityRepository

open class LiveCollector(
    context: Context,
    private val showNotification: Boolean = true
) : Collector {

    private val notificationProvider: NotificationProvider = NotificationProvider(context)

    init {
        EntityRepository.initialize(context)
    }

    @CallSuper
    override fun onScreen(screen: Screen) {
        val entity = CollarEntity(
            type = EntityType.SCREEN,
            timestamp = System.currentTimeMillis(),
            name = screen.name
        )
        EntityRepository.saveScreen(entity)
        if (showNotification) {
            notificationProvider.showScreen(entity)
        }
    }

    @CallSuper
    override fun onEvent(event: Event) {
        val entity = CollarEntity(
            type = EntityType.EVENT,
            timestamp = System.currentTimeMillis(),
            name = event.name,
            parameters = event.params?.let { bundleToMap(it) }
        )
        EntityRepository.saveEvent(entity)
        if (showNotification) {
            notificationProvider.showEvent(entity)
        }
    }

    @CallSuper
    override fun onProperty(property: Property) {
        val entity = CollarEntity(
            type = EntityType.PROPERTY,
            timestamp = System.currentTimeMillis(),
            name = property.name,
            value = property.value
        )
        EntityRepository.saveProperty(entity)
        if (showNotification) {
            notificationProvider.showProperty(entity)
        }
    }

    private fun bundleToMap(bundle: Bundle): String {
        val map = mutableMapOf<String, String>()

        val ks: Set<String> = bundle.keySet()
        val iterator = ks.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            val value = bundle.get(key)
            val valueAsString = value?.let {
                when (value) {
                    is String -> bundle.getString(key)
                    is Boolean -> bundle.getBoolean(key).toString()
                    is Byte -> bundle.getByte(key).toString()
                    is Char -> bundle.getChar(key).toString()
                    is Double -> bundle.getDouble(key).toString()
                    is Float -> bundle.getFloat(key).toString()
                    is Int -> bundle.getInt(key).toString()
                    is Long -> bundle.getLong(key).toString()
                    is Short -> bundle.getShort(key).toString()
                    else -> {
                        Log.w(CollarUi.javaClass.simpleName, "Illegal value type ${value.javaClass.canonicalName} for key \"$key\"")
                        ""
                    }
                }
            }.orEmpty()

            if (valueAsString.isBlank().not()) {
                map[key] = valueAsString
            } else {
                Log.w(CollarUi.javaClass.simpleName, "Value for key \"$key\" is empty")
            }
        }
        return map.toList().joinToString("\n") { "${it.first} = ${it.second}" }
    }
}