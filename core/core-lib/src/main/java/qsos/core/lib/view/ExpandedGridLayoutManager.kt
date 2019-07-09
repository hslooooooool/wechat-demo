package qsos.core.lib.view

import android.content.Context
import android.view.View

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * @author : 华清松
 * @description : 完全展开的RecycleView
 */
class ExpandedGridLayoutManager : GridLayoutManager {

    private var measuredDimension: IntArray? = IntArray(2)

    constructor(context: Context, spanCount: Int) : super(context, spanCount)

    constructor(context: Context, spanCount: Int, orientation: Int, reverseLayout: Boolean)
            : super(context, spanCount, orientation, reverseLayout)

    override fun onMeasure(recycler: RecyclerView.Recycler, state: RecyclerView.State, widthSpec: Int, heightSpec: Int) {
        val widthMode = View.MeasureSpec.getMode(widthSpec)
        val heightMode = View.MeasureSpec.getMode(heightSpec)
        val widthSize = View.MeasureSpec.getSize(widthSpec)
        val heightSize = View.MeasureSpec.getSize(heightSpec)
        var measureWidth = 0
        var measureHeight = 0
        val count = itemCount
        val span = spanCount
        for (i in 0 until count) {
            measuredDimension = getChildDimension(recycler, i)
            if (orientation == LinearLayoutManager.HORIZONTAL) {
                if (i % span == 0) {
                    measureWidth += measuredDimension!![0]
                }
                measureHeight = Math.max(measureHeight, measuredDimension!![1])
            } else {
                if (measuredDimension != null && measuredDimension!!.isNotEmpty()) {
                    if (i % span == 0) {
                        measureHeight += measuredDimension!![1]
                    }
                    measureWidth = Math.max(measureWidth, measuredDimension!![0])
                }
            }
        }
        measureHeight += paddingBottom + paddingTop
        measureWidth += paddingLeft + paddingRight
        measureHeight = if (heightMode == View.MeasureSpec.EXACTLY) heightSize else measureHeight
        measureWidth = if (widthMode == View.MeasureSpec.EXACTLY) widthSize else measureWidth
        if (orientation == LinearLayoutManager.HORIZONTAL && measureWidth > widthSize) {
            if (widthMode == View.MeasureSpec.UNSPECIFIED) {
                setMeasuredDimension(measureWidth, measureHeight)
            } else {
                super.onMeasure(recycler, state, widthSpec, heightSpec)
            }
        } else if (orientation == RecyclerView.VERTICAL && measureHeight > heightSize) {
            if (heightMode == View.MeasureSpec.UNSPECIFIED) {
                setMeasuredDimension(measureWidth, measureHeight)
            } else {
                super.onMeasure(recycler, state, widthSpec, heightSpec)
            }
        } else {
            setMeasuredDimension(measureWidth, measureHeight)
        }
    }

    private fun getChildDimension(recycler: RecyclerView.Recycler, position: Int): IntArray? {
        try {
            val measuredDimension = IntArray(2)
            val view = recycler.getViewForPosition(position)
            //测量childView，以便获得宽高（包括ItemDecoration的限制）
            super.measureChildWithMargins(view, 0, 0)
            //获取childView，以便获得宽高（包括ItemDecoration的限制），以及边距
            val p = view.layoutParams as RecyclerView.LayoutParams
            measuredDimension[0] = getDecoratedMeasuredWidth(view) + p.leftMargin + p.rightMargin
            measuredDimension[1] = getDecoratedMeasuredHeight(view) + p.bottomMargin + p.topMargin
            return measuredDimension
        } catch (e: Exception) {
        }

        return null
    }
}