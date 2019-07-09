package qsos.core.lib.view.widget.label

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import qsos.lib.lib.R
import java.util.*

class StackLayout : RelativeLayout {

    private var itemMargin = 0

    private var items: MutableList<View>? = null
    private var newHeight = 0

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        loadAttrs(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        loadAttrs(context, attrs)
    }

    private fun loadAttrs(context: Context, attrs: AttributeSet) {
        try {
            //默认值
            itemMargin = dp2px(4f)

            //加载值
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.StackLabel)
            itemMargin = typedArray.getDimensionPixelOffset(R.styleable.StackLabel_itemMargin, itemMargin)
            typedArray.recycle()
        } catch (e: Exception) {
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        refreshViews()

        setMeasuredDimension(measuredWidth, newHeight)//设置宽高
    }

    private fun refreshViews() {
        val maxWidth = measuredWidth
        items = ArrayList()
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == View.VISIBLE) {
                items!!.add(getChildAt(i))
            }
        }

        newHeight = 0
        if (items != null && !items!!.isEmpty()) {
            for (i in items!!.indices) {
                val item = items!![i]

                var n_x = 0
                var n_y = 0
                var o_y = 0

                if (i != 0) {
                    n_x = items!![i - 1].x.toInt() + items!![i - 1].measuredWidth + itemMargin
                    n_y = items!![i - 1].y.toInt() + items!![i - 1].measuredHeight + itemMargin
                    o_y = items!![i - 1].y.toInt()
                }

                if (n_x + item.measuredWidth > maxWidth) {
                    n_x = 0
                    o_y = n_y
                }

                item.y = o_y.toFloat()
                item.x = n_x.toFloat()

                newHeight = (item.y + item.measuredHeight).toInt()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

    private fun dp2px(dpValue: Float): Int {
        return (0.5f + dpValue * Resources.getSystem().displayMetrics.density).toInt()
    }
}