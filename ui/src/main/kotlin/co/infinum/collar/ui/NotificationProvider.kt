package co.infinum.collar.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.LongSparseArray
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import co.infinum.collar.ui.data.room.entity.CollarEntity
import co.infinum.collar.ui.data.room.entity.EventEntity
import co.infinum.collar.ui.data.room.entity.PropertyEntity
import co.infinum.collar.ui.data.room.entity.ScreenEntity

class NotificationProvider(private val context: Context) {

    companion object {
        private const val NOTIFICATIONS_CHANNEL_ID = "collar_analytics"
        private const val NOTIFICATION_ID = 4578
        private const val BUFFER_SIZE = 10
    }

    private val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val buffer = LongSparseArray<CollarEntity>()
    private val idsSet = HashSet<Long>()

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATIONS_CHANNEL_ID,
                context.getString(R.string.collar_name),
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannels(listOf(notificationChannel))
        }
    }

    fun showScreen(entity: ScreenEntity) {
        addToBuffer(entity)
        buildNotification()
    }

    fun showEvent(entity: EventEntity) {
        addToBuffer(entity)
        buildNotification()
    }

    fun showProperty(entity: PropertyEntity) {
        addToBuffer(entity)
        buildNotification()
    }

    private fun addToBuffer(entity: CollarEntity) {
        entity.timestamp?.let {
            synchronized(buffer) {
                idsSet.add(entity.hashCode().toLong())
                buffer.put(it, entity)
                if (buffer.size() > BUFFER_SIZE) {
                    buffer.removeAt(0)
                }
            }
        }
    }

    private fun buildNotification() {
        val builder = NotificationCompat.Builder(context, NOTIFICATIONS_CHANNEL_ID)
            .setContentIntent(
                PendingIntent.getActivity(
                    context,
                    NOTIFICATION_ID,
                    Intent(context, CollarActivity::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .setLocalOnly(true)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(ContextCompat.getColor(context, R.color.collar_color_primary))
            .setContentTitle(context.getString(R.string.collar_name))
            .setAutoCancel(true)
        val inboxStyle = NotificationCompat.InboxStyle()

        synchronized(buffer) {
            var count = 0
            (buffer.size() - 1 downTo 0).forEach { i ->
                val bufferedEntity = buffer.valueAt(i)
                if ((bufferedEntity != null) && count < BUFFER_SIZE) {
                    if (count == 0) {
                        builder.setContentText(bufferedEntity.name)
                    }
                    inboxStyle.addLine(bufferedEntity.name)
                }
                count++
            }
            builder.setStyle(inboxStyle)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setSubText(idsSet.size.toString())
            } else {
                builder.setNumber(idsSet.size)
            }
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
}