package com.infinum.collar.ui.presentation.notifications.inapp.snackbar

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.ContentViewCallback
import com.infinum.collar.ui.CollarUi
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
        this.rootLayout.setOnClickListener { CollarUi.show() }
    }

    override fun animateContentIn(delay: Int, duration: Int) {
        listOf(viewBinding.iconView, viewBinding.nameView, viewBinding.valueView, viewBinding.timeView).forEach {
            it.alpha = 0f
            it.animate().alpha(1f).setDuration(duration.toLong()).setStartDelay(delay.toLong()).start()
        }
        with(viewBinding.actionButton) {
            alpha = 0f
            animate().alpha(1f).setDuration(duration.toLong()).setStartDelay(delay.toLong()).start()
        }
    }

    override fun animateContentOut(delay: Int, duration: Int) {
        listOf(viewBinding.iconView, viewBinding.nameView, viewBinding.valueView, viewBinding.timeView).forEach {
            it.alpha = 1f
            it.animate().alpha(0f).setDuration(duration.toLong()).setStartDelay(delay.toLong()).start()
        }
        with(viewBinding.actionButton) {
            alpha = 1f
            animate().alpha(0f).setDuration(duration.toLong()).setStartDelay(delay.toLong()).start()
        }
    }

    fun setIconBackgroundTint(@ColorRes colorResId: Int) {
        viewBinding.iconView.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, colorResId))
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

    fun setAction(listener: OnClickListener?) {
        viewBinding.actionButton.setOnClickListener(listener)
    }
}
