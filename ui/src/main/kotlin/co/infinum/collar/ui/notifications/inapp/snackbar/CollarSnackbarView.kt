package co.infinum.collar.ui.notifications.inapp.snackbar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import co.infinum.collar.ui.databinding.CollarViewSnackbarBinding
import com.google.android.material.snackbar.ContentViewCallback

class CollarSnackbarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), ContentViewCallback {

    private val viewBinding = CollarViewSnackbarBinding.inflate(LayoutInflater.from(context), this, true)

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

    fun setIconBackgroundResource(@DrawableRes drawableResId: Int) {
        viewBinding.iconView.setBackgroundResource(drawableResId)
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
