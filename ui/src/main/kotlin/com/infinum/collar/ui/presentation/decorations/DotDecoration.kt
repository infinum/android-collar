package com.infinum.collar.ui.presentation.decorations

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.infinum.collar.ui.R
import kotlin.math.roundToInt

internal class DotDecoration(
    context: Context,
    private val decoration: Decoration
) : RecyclerView.ItemDecoration() {

    private val divider = ContextCompat.getDrawable(context, R.drawable.collar_decoration_dot)

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        if (parent.getChildAdapterPosition(view) != RecyclerView.NO_POSITION) {
            when (decoration) {
                Decoration.START -> {
                    if (parent.getChildAdapterPosition(view) <= 0) {
                        outRect.top = divider?.intrinsicHeight ?: 0
                    }
                }
                Decoration.END -> {
                    if (parent.getChildAdapterPosition(view) == state.itemCount - 1) {
                        outRect.bottom = divider?.intrinsicHeight ?: 0
                    }
                }
            }
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        divider?.let {
            if (parent.childCount > 0) {
                val child = getChild(parent)
                val line = child.findViewById<View>(R.id.line)
                val left = (((line.right - line.left) / 2.0f + line.left) - it.intrinsicWidth / 2.0f).roundToInt()
                val right = left + it.intrinsicWidth
                val top = getTopBound(child, it)
                val bottom: Int = top + it.intrinsicHeight
                it.setBounds(left, top, right, bottom)
                it.draw(c)
            }
        }
    }

    private fun getChild(parent: RecyclerView): View =
        when (decoration) {
            Decoration.START -> parent.getChildAt(0)
            Decoration.END -> parent.getChildAt(parent.childCount - 1)
        }

    private fun getTopBound(child: View, divider: Drawable): Int =
        when (decoration) {
            Decoration.START -> child.top - divider.intrinsicHeight
            Decoration.END -> child.bottom
        }
}
