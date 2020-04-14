package co.infinum.collar.ui.notifications.inapp.snackbar

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.DrawableRes
import co.infinum.collar.ui.R
import com.google.android.material.snackbar.ContentViewCallback
import com.google.android.material.textview.MaterialTextView

class CollarSnackbarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), ContentViewCallback {

    private var iconView: ImageView
    private var nameView: MaterialTextView
    private var valueView: MaterialTextView
    private var timeView: MaterialTextView
    private var actionButton: Button

    init {
        View.inflate(context, R.layout.collar_view_snackbar, this).run {
            iconView = findViewById(R.id.iconView)
            nameView = findViewById(R.id.nameView)
            valueView = findViewById(R.id.valueView)
            timeView = findViewById(R.id.timeView)
            actionButton = findViewById(R.id.actionButton)
        }
    }

    override fun animateContentIn(delay: Int, duration: Int) {
        listOf(iconView, nameView, valueView, timeView).forEach {
            it.alpha = 0f
            it.animate().alpha(1f).setDuration(duration.toLong()).setStartDelay(delay.toLong()).start()
        }
        with(actionButton) {
            alpha = 0f
            animate().alpha(1f).setDuration(duration.toLong()).setStartDelay(delay.toLong()).start()
        }
    }

    override fun animateContentOut(delay: Int, duration: Int) {
        listOf(iconView, nameView, valueView, timeView).forEach {
            it.alpha = 1f
            it.animate().alpha(0f).setDuration(duration.toLong()).setStartDelay(delay.toLong()).start()
        }
        with(actionButton) {
            alpha = 1f
            animate().alpha(0f).setDuration(duration.toLong()).setStartDelay(delay.toLong()).start()
        }
    }

    fun setIconBackgroundResource(@DrawableRes drawableResId: Int) {
        iconView.setBackgroundResource(drawableResId)
    }

    fun setIconResource(@DrawableRes drawableResId: Int) {
        iconView.setImageResource(drawableResId)
    }

    fun setTitle(text: String?) {
        nameView.text = text
    }

    fun setValue(text: String?) {
        valueView.text = text
    }

    fun setTime(text: String?) {
        timeView.text = text
    }

    fun setAction(listener: OnClickListener?) {
        actionButton.setOnClickListener(listener)
    }
}