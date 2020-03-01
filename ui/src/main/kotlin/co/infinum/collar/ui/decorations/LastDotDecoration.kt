package co.infinum.collar.ui.decorations

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import co.infinum.collar.ui.R


class LastDotDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private val divider = ContextCompat.getDrawable(context, R.drawable.collar_decoration_dot)

    private val startMargin = context.resources.getDimensionPixelSize(R.dimen.collar_divider_start_margin)

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val itemCount = state.itemCount
        if (parent.getChildAdapterPosition(view) != itemCount - 1) {
            return
        }

        outRect.bottom = divider?.intrinsicHeight ?: 0
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        divider?.let {
            if (parent.childCount > 0) {
                val lastChild = parent.getChildAt(parent.childCount - 1)
                val lastChildLayoutParams = lastChild.layoutParams as RecyclerView.LayoutParams
                val top = lastChild.bottom + lastChildLayoutParams.bottomMargin
                val bottom: Int = top + it.intrinsicHeight

                it.setBounds(startMargin, top, startMargin + it.intrinsicWidth, bottom)
                it.draw(c)
            }
        }
    }
}