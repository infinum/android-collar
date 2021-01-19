package co.infinum.collar.ui.presentation.decorations

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import co.infinum.collar.ui.R

internal class StartDotDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private val divider = ContextCompat.getDrawable(context, R.drawable.collar_decoration_dot)

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        if (parent.getChildAdapterPosition(view) == RecyclerView.NO_POSITION) {
            return
        }
        if (parent.getChildAdapterPosition(view) > 0) {
            return
        }

        outRect.top = divider?.intrinsicHeight ?: 0
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        divider?.let {
            if (parent.childCount > 0) {
                val firstChild = parent.getChildAt(0)
                val top = firstChild.top - it.intrinsicHeight
                val bottom: Int = top + it.intrinsicHeight

                it.setBounds(0, top, it.intrinsicWidth, bottom)
                it.draw(c)
            }
        }
    }
}
