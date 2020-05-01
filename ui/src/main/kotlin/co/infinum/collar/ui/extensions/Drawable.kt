package co.infinum.collar.ui.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import co.infinum.collar.ui.R
import kotlin.math.roundToInt

internal fun Drawable.toBitmap(): Bitmap {
    if (this is BitmapDrawable) {
        return bitmap
    }

    val width = if (bounds.isEmpty) intrinsicWidth else bounds.width()
    val height = if (bounds.isEmpty) intrinsicHeight else bounds.height()

    return Bitmap.createBitmap(width.coerceAtLeast(1), height.coerceAtLeast(1), Bitmap.Config.ARGB_8888).also {
        val canvas = Canvas(it)
        setBounds(0, 0, canvas.width, canvas.height)
        draw(canvas)
    }
}

@Suppress("MaxLineLength", "MaximumLineLength")
internal fun Drawable?.addBadge(context: Context): Drawable? {
    val collarLogo = ContextCompat.getDrawable(context, R.drawable.collar_ic_logo)
    return this?.let {
        val iconSize = context.resources.getDimensionPixelSize(R.dimen.collar_application_icon_size)
        val badgeSize = (iconSize / 2.0f).roundToInt()
        val root = Bitmap.createBitmap(iconSize, iconSize, Bitmap.Config.ARGB_8888)
        val app = Bitmap.createScaledBitmap(it.toBitmap(), badgeSize, badgeSize, false)
        Canvas(root).apply {
            collarLogo?.toBitmap()?.let { bitmap ->
                drawBitmap(
                    bitmap,
                    (iconSize - collarLogo.intrinsicWidth) / 2.0f,
                    (iconSize - collarLogo.intrinsicHeight) / 2.0f,
                    Paint().apply { isDither = true }
                )
            }
            drawBitmap(
                app,
                (iconSize - badgeSize).toFloat(),
                (iconSize - badgeSize).toFloat(),
                Paint().apply { isDither = true }
            )
        }.run {
            BitmapDrawable(context.resources, root)
        }
    } ?: collarLogo
}
