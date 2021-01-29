package co.infinum.collar.ui.presentation.decorations

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import co.infinum.collar.ui.R
import kotlin.math.roundToInt

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
                val line = firstChild.findViewById<View>(R.id.line)

                val left = (((line.right - line.left) / 2.0f + line.left) - it.intrinsicWidth / 2.0f).roundToInt()
                val right = left + it.intrinsicWidth
                val top = firstChild.top - it.intrinsicHeight
                val bottom: Int = top + it.intrinsicHeight

                it.setBounds(left, top, right, bottom)
                it.draw(c)
            }
        }
    }
}
