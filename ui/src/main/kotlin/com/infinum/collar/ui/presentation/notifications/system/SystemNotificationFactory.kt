package com.infinum.collar.ui.presentation.notifications.system

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.LongSparseArray
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.infinum.collar.ui.R
import com.infinum.collar.ui.data.models.local.CollarEntity
import com.infinum.collar.ui.data.models.local.EntityType
import com.infinum.collar.ui.extensions.isPermissionGranted
import com.infinum.collar.ui.extensions.presentationItemFormat
import com.infinum.collar.ui.presentation.CollarActivity
import com.infinum.collar.ui.presentation.notifications.NotificationFactory
import com.infinum.collar.ui.presentation.notifications.shared.CollarActivityCallbacks
import com.infinum.collar.ui.presentation.shared.Constants.KEY_ENTITY_ID
import java.util.Date
import me.tatarka.inject.annotations.Inject

@Inject
internal class SystemNotificationFactory(
    private val context: Context,
    private val callbacks: CollarActivityCallbacks
) : NotificationFactory {

    companion object {
        private const val NOTIFICATIONS_CHANNEL_ID = "collar_analytics"
        private const val NOTIFICATION_ID = 4578
        private const val INTERNAL_BUFFER_SIZE = 5
        private const val PERMISSION_REQUEST_CODE = 11
    }

    private val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(context)
    private val buffer = LongSparseArray<CollarEntity>()

    private val idsSet = HashSet<Long>()

    init {
        (context.applicationContext as Application).registerActivityLifecycleCallbacks(callbacks)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATIONS_CHANNEL_ID,
                context.getString(R.string.collar_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannels(listOf(notificationChannel))
        }
    }

    override fun showScreen(entity: CollarEntity) {
        addToBuffer(entity)
        buildNotification()
    }

    override fun showEvent(entity: CollarEntity) {
        addToBuffer(entity)
        buildNotification()
    }

    override fun showProperty(entity: CollarEntity) {
        addToBuffer(entity)
        buildNotification()
    }

    private fun addToBuffer(entity: CollarEntity) {
        synchronized(buffer) {
            idsSet.add(entity.hashCode().toLong())
            buffer.put(entity.timestamp, entity)
            if (buffer.size() > INTERNAL_BUFFER_SIZE) {
                buffer.removeAt(0)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun buildNotification() {
        val notificationLayout = RemoteViews(context.packageName, R.layout.collar_notification)
        val notificationLayoutExpanded = RemoteViews(context.packageName, R.layout.collar_notification_expanded)

        val builder = NotificationCompat.Builder(context, NOTIFICATIONS_CHANNEL_ID)
            .setLocalOnly(true)
            .setSmallIcon(R.drawable.collar_ic_notification)
            .setColor(ContextCompat.getColor(context, R.color.collar_color_primary))
            .setContentTitle(context.getString(R.string.collar_name))
            .setAutoCancel(true)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())

        synchronized(buffer) {
            var count = 0
            (buffer.size() - 1 downTo 0).forEach { i ->
                val bufferedEntity = buffer.valueAt(i)
                if ((bufferedEntity != null) && count < INTERNAL_BUFFER_SIZE) {
                    if (count == 0) {
                        builder.setContentText(bufferedEntity.name)
                        buildRemoteView(notificationLayout, bufferedEntity)
                    }
                    val itemView = RemoteViews(context.packageName, R.layout.collar_notification)
                    println("_BOJAN_ building $i")
                    buildRemoteView(itemView, bufferedEntity)
                    notificationLayoutExpanded.addView(
                        R.id.containerView,
                        itemView
                    )
                }
                count++
            }
            builder.setCustomContentView(notificationLayout)
            builder.setCustomBigContentView(notificationLayoutExpanded)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setSubText(idsSet.size.toString())
            } else {
                builder.setNumber(idsSet.size)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (context.isPermissionGranted(Manifest.permission.POST_NOTIFICATIONS)) {
                notificationManager.notify(NOTIFICATION_ID, builder.build())
            } else {
                callbacks.current()?.let {
                    ActivityCompat.requestPermissions(
                        it,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        PERMISSION_REQUEST_CODE
                    )
                }
            }
        } else {
            notificationManager.notify(NOTIFICATION_ID, builder.build())
        }
    }

    private fun buildRemoteView(remoteViews: RemoteViews, entity: CollarEntity) {
        when (entity.type) {
            EntityType.SCREEN -> R.drawable.collar_ic_screen_notification
            EntityType.EVENT -> R.drawable.collar_ic_event_notification
            EntityType.PROPERTY -> R.drawable.collar_ic_property_notification
            else -> null
        }?.let { remoteViews.setImageViewResource(R.id.iconView, it) }
        remoteViews.setTextViewText(R.id.nameView, entity.name)
        entity.parameters?.let {
            remoteViews.setTextViewText(R.id.valueView, it)
            remoteViews.setViewVisibility(R.id.valueView, View.VISIBLE)
        } ?: remoteViews.setViewVisibility(R.id.valueView, View.GONE)
        remoteViews.setTextViewText(
            R.id.timeView,
            Date(entity.timestamp).presentationItemFormat
        )
        remoteViews.setOnClickPendingIntent(
            R.id.containerView,
            buildPendingIntent(entity)
        )
    }

    private fun buildPendingIntent(entity: CollarEntity): PendingIntent =
        PendingIntent.getActivity(
            context,
            NOTIFICATION_ID + (entity.id ?: 0L).toInt(),
            Intent(context, CollarActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                entity.id?.let {
                    putExtra("com.infinum.collar.$KEY_ENTITY_ID", it)
                }
            },
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                else -> PendingIntent.FLAG_UPDATE_CURRENT
            }
        )
}
