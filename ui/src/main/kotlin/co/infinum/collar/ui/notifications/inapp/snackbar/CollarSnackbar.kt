package co.infinum.collar.ui.notifications.inapp.snackbar

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.BaseTransientBottomBar

class CollarSnackbar(
    parent: ViewGroup,
    content: CollarSnackbarView
) : BaseTransientBottomBar<CollarSnackbar>(parent, content, content) {

    init {
        with(getView()) {
            setBackgroundColor(ContextCompat.getColor(view.context, android.R.color.transparent))
            setPadding(0, 0, 0, 0)
        }
    }

    companion object {
        fun make(
            parentLayout: FrameLayout?,
            @DrawableRes background: Int,
            @DrawableRes icon: Int,
            title: String?,
            message: String?,
            time: String?,
            listener: View.OnClickListener? = null
        ): CollarSnackbar =
            parentLayout?.let {
                CollarSnackbarView(it.context)
                    .apply {
                        setIconBackgroundResource(background)
                        setIconResource(icon)
                        setTitle(title)
                        setValue(message)
                        setTime(time)
                        setAction(listener)
                    }
                    .run {
                        CollarSnackbar(parentLayout, this)
                    }
            } ?: throw IllegalArgumentException(
                "No suitable parent found for CollarSnackbar. Please provide a valid Activity."
            )
    }
}