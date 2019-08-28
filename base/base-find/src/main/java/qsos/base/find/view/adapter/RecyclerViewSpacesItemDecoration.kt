package qsos.base.find.view.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * @author : 华清松
 * 七月
 */
class RecyclerViewSpacesItemDecoration(
        private val top: Int = 0,
        private val bottom: Int = 0,
        private val left: Int = 0,
        private val right: Int = 0
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.top = top
        outRect.left = left
        outRect.right = right
        outRect.bottom = bottom
    }
}