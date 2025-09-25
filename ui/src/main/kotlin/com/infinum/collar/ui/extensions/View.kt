package com.infinum.collar.ui.extensions

import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams

internal fun View.applyLeftAndRightEdgeToEdgeInsets(
    typeMask: Int = WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout(),
    shouldPropagateInsets: Boolean = false,
): Unit = applyEdgeToEdgeInsets(typeMask, shouldPropagateInsets) { left, _, right, _ ->
    leftMargin = left
    rightMargin = right
}

internal fun View.applyTopLeftAndRightEdgeToEdgeInsets(
    typeMask: Int = WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout(),
    shouldPropagateInsets: Boolean = false,
): Unit = applyEdgeToEdgeInsets(typeMask, shouldPropagateInsets) { left, top, right, _ ->
    leftMargin = left
    topMargin = top
    rightMargin = right
}

internal fun View.applyBottomLeftAndRightEdgeToEdgeInsets(
    typeMask: Int = WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout(),
    shouldPropagateInsets: Boolean = false,
): Unit = applyEdgeToEdgeInsets(typeMask, shouldPropagateInsets) { left, _, right, bottom ->
    leftMargin = left
    rightMargin = right
    bottomMargin = bottom
}

private fun View.applyEdgeToEdgeInsets(
    typeMask: Int = WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout(),
    shouldPropagateInsets: Boolean = false,
    block: ViewGroup.MarginLayoutParams.(left: Int, top: Int, right: Int, bottom: Int) -> Unit,
) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val windowInsets = insets.getInsets(typeMask)
        view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            val left = marginLeft + windowInsets.left
            val top = marginTop + windowInsets.top
            val right = marginRight + windowInsets.right
            val bottom = marginBottom + windowInsets.bottom
            block(left, top, right, bottom)
        }
        if (shouldPropagateInsets) insets else WindowInsetsCompat.CONSUMED
    }
}
