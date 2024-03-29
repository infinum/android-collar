package com.infinum.collar.ui.presentation.notifications.inapp.snackbar

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.text.method.ScrollingMovementMethod
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.ContentViewCallback
import com.infinum.collar.ui.databinding.CollarViewSnackbarBinding

internal class CollarSnackbarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), ContentViewCallback {

    private val viewBinding = CollarViewSnackbarBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    ).apply {
        this.valueView.movementMethod = ScrollingMovementMethod()
    }

    override fun animateContentIn(delay: Int, duration: Int) {
        listOf(viewBinding.iconView, viewBinding.nameView, viewBinding.valueView, viewBinding.timeView).forEach {
            it.alpha = 0f
            it.animate().alpha(1f).setDuration(duration.toLong()).setStartDelay(delay.toLong()).start()
        }
        with(viewBinding.shareButton) {
            alpha = 0f
            animate().alpha(1f).setDuration(duration.toLong()).setStartDelay(delay.toLong()).start()
        }
        with(viewBinding.openButton) {
            alpha = 0f
            animate().alpha(1f).setDuration(duration.toLong()).setStartDelay(delay.toLong()).start()
        }
    }

    override fun animateContentOut(delay: Int, duration: Int) {
        listOf(viewBinding.iconView, viewBinding.nameView, viewBinding.valueView, viewBinding.timeView).forEach {
            it.alpha = 1f
            it.animate().alpha(0f).setDuration(duration.toLong()).setStartDelay(delay.toLong()).start()
        }
        with(viewBinding.shareButton) {
            alpha = 1f
            animate().alpha(0f).setDuration(duration.toLong()).setStartDelay(delay.toLong()).start()
        }
        with(viewBinding.openButton) {
            alpha = 1f
            animate().alpha(0f).setDuration(duration.toLong()).setStartDelay(delay.toLong()).start()
        }
    }

    fun setIconBackgroundTint(@ColorRes colorResId: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            viewBinding.iconView.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, colorResId))
        } else {
            viewBinding.iconView.background.colorFilter =
                PorterDuffColorFilter(ContextCompat.getColor(context, colorResId), PorterDuff.Mode.SRC_ATOP)
            viewBinding.iconView.drawable.colorFilter =
                PorterDuffColorFilter(ContextCompat.getColor(context, android.R.color.white), PorterDuff.Mode.SRC_ATOP)
        }
    }

    fun setIconResource(@DrawableRes drawableResId: Int) {
        viewBinding.iconView.setImageResource(drawableResId)
    }

    fun setTitle(text: String?) {
        viewBinding.nameView.text = text
    }

    fun setValue(text: String?) {
        viewBinding.valueView.text = text
    }

    fun setTime(text: String?) {
        viewBinding.timeView.text = text
    }

    fun setShareAction(listener: OnClickListener?) {
        viewBinding.shareButton.setOnClickListener(listener)
    }

    fun setOpenAction(listener: OnClickListener?) {
        viewBinding.openButton.setOnClickListener(listener)
    }
}
