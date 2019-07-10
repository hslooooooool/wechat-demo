package qsos.core.lib.view.widget.image

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import qsos.lib.lib.R

/**
 * @author : 华清松
 * @description : 等高宽图片
 */
class RatioImageView : AppCompatImageView {

    /**宽高比例*/
    private var mRatio = 0f

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatioImageView)

        mRatio = typedArray.getFloat(R.styleable.RatioImageView_ratio, 0f)
        typedArray.recycle()
    }


    /**
     * 设置ImageView的宽高比
     *
     * @param ratio
     */
    fun setRatio(ratio: Float) {
        mRatio = ratio
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var mHeightMeasureSpec = heightMeasureSpec
        val width = MeasureSpec.getSize(widthMeasureSpec)
        if (mRatio != 0f) {
            val height = width / mRatio
            mHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height.toInt(), MeasureSpec.EXACTLY)
        }
        super.onMeasure(widthMeasureSpec, mHeightMeasureSpec)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val drawable = drawable
                drawable?.mutate()?.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY)
            }
            MotionEvent.ACTION_MOVE -> {
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                val drawableUp = drawable
                drawableUp?.mutate()?.clearColorFilter()
            }
        }

        return super.onTouchEvent(event)
    }
}
