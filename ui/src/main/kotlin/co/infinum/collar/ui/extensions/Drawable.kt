package co.infinum.collar.ui.extensions

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable

fun Drawable.toBitmap(): Bitmap {
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
