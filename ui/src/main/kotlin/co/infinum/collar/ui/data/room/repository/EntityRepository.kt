package co.infinum.collar.ui.data.room.repository

import android.content.Context
import co.infinum.collar.ui.data.room.CollarDatabase
import co.infinum.collar.ui.data.room.dao.EventsDao
import co.infinum.collar.ui.data.room.dao.PropertiesDao
import co.infinum.collar.ui.data.room.dao.ScreensDao
import co.infinum.collar.ui.data.room.entity.EventEntity
import co.infinum.collar.ui.data.room.entity.PropertyEntity
import co.infinum.collar.ui.data.room.entity.ScreenEntity
import java.util.concurrent.Executor
import java.util.concurrent.Executors

internal object EntityRepository {

    private val executor: Executor = Executors.newSingleThreadExecutor()

    private lateinit var screens: ScreensDao
    private lateinit var events: EventsDao
    private lateinit var properties: PropertiesDao

    fun initialize(context: Context) {
        val database = CollarDatabase.create(context)
        screens = database.screensDao()
        events = database.eventsDao()
        properties = database.propertiesDao()
    }

    fun saveScreen(entity: ScreenEntity) =
        executor.execute {
            screens.save(entity)
        }

    fun saveEvent(entity: EventEntity) =
        executor.execute {
            events.save(entity)
        }

    fun saveProperty(entity: PropertyEntity) =
        executor.execute {
            properties.save(entity)
        }

    fun loadScreens() = screens.load()

    fun loadEvents() = events.load()

    fun loadProperties() = properties.load()

    fun clearAll() {
        executor.execute {
            screens.delete()
            events.delete()
            properties.delete()
        }
    }
}