package com.infinum.collar.ui.presentation.notifications.inapp.snackbar

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.BaseTransientBottomBar

internal class CollarSnackbar(
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

        @Suppress("LongParameterList")
        fun make(
            parentLayout: FrameLayout?,
            @ColorRes backgroundTint: Int,
            @DrawableRes icon: Int,
            title: String?,
            message: String?,
            time: String?,
            listener: View.OnClickListener? = null
        ): CollarSnackbar =
            parentLayout?.let {
                CollarSnackbarView(it.context)
                    .apply {
                        setIconBackgroundTint(backgroundTint)
                        setIconResource(icon)
                        setTitle(title)
                        setValue(message)
                        setTime(time)
                        setAction(listener)
                    }.let { collarSnackbarView ->
                        CoordinatorLayout(it.context).let { coordinatorLayout ->
                            it.addView(coordinatorLayout)
                            CollarSnackbar(coordinatorLayout, collarSnackbarView)
                        }
                    }
            } ?: throw IllegalArgumentException(
                "No suitable parent found for CollarSnackbar. Please provide a valid Activity."
            )
    }
}
