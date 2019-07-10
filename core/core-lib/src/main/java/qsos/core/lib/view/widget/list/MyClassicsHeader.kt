package qsos.core.lib.view.widget.list

import android.content.Context
import android.content.SharedPreferences
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import com.scwang.smartrefresh.layout.api.RefreshHeader
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.constant.RefreshState
import com.scwang.smartrefresh.layout.constant.SpinnerStyle
import com.scwang.smartrefresh.layout.internal.ArrowDrawable
import com.scwang.smartrefresh.layout.internal.InternalClassics
import com.scwang.smartrefresh.layout.internal.ProgressDrawable
import com.scwang.smartrefresh.layout.util.DensityUtil
import qsos.lib.lib.R

class MyClassicsHeader @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : InternalClassics<MyClassicsHeader>(context, attrs, defStyleAttr), RefreshHeader {

    private var time = "LAST_UPDATE_TIME"

    private var mShared: SharedPreferences

    init {

        val arrowView = mArrowView
        val progressView = mProgressView
        val density = DensityUtil()

        val ta = context.obtainStyledAttributes(attrs, R.styleable.ClassicsHeader)

        val lpArrow = arrowView.layoutParams as LayoutParams
        val lpProgress = progressView.layoutParams as LayoutParams
        val lpUpdateText = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        lpUpdateText.topMargin = ta.getDimensionPixelSize(R.styleable.ClassicsHeader_srlTextTimeMarginTop, density.dip2px(0f))
        lpProgress.rightMargin = ta.getDimensionPixelSize(R.styleable.ClassicsFooter_srlDrawableMarginRight, density.dip2px(20f))
        lpArrow.rightMargin = lpProgress.rightMargin
        lpArrow.width = ta.getLayoutDimension(R.styleable.ClassicsHeader_srlDrawableArrowSize, lpArrow.width)
        lpArrow.height = ta.getLayoutDimension(R.styleable.ClassicsHeader_srlDrawableArrowSize, lpArrow.height)
        lpProgress.width = ta.getLayoutDimension(R.styleable.ClassicsHeader_srlDrawableProgressSize, lpProgress.width)
        lpProgress.height = ta.getLayoutDimension(R.styleable.ClassicsHeader_srlDrawableProgressSize, lpProgress.height)
        lpArrow.width = ta.getLayoutDimension(R.styleable.ClassicsHeader_srlDrawableSize, lpArrow.width)
        lpArrow.height = ta.getLayoutDimension(R.styleable.ClassicsHeader_srlDrawableSize, lpArrow.height)
        lpProgress.width = ta.getLayoutDimension(R.styleable.ClassicsHeader_srlDrawableSize, lpProgress.width)
        lpProgress.height = ta.getLayoutDimension(R.styleable.ClassicsHeader_srlDrawableSize, lpProgress.height)
        mFinishDuration = ta.getInt(R.styleable.ClassicsHeader_srlFinishDuration, mFinishDuration)
        mSpinnerStyle = SpinnerStyle.values()[ta.getInt(R.styleable.ClassicsHeader_srlClassicsSpinnerStyle, mSpinnerStyle.ordinal)]

        if (ta.hasValue(R.styleable.ClassicsHeader_srlDrawableArrow)) {
            mArrowView.setImageDrawable(ta.getDrawable(R.styleable.ClassicsHeader_srlDrawableArrow))
        } else {
            mArrowDrawable = ArrowDrawable()
            mArrowDrawable.setColor(-0x99999a)
            mArrowView.setImageDrawable(mArrowDrawable)
        }

        if (ta.hasValue(R.styleable.ClassicsHeader_srlDrawableProgress)) {
            mProgressView.setImageDrawable(ta.getDrawable(R.styleable.ClassicsHeader_srlDrawableProgress))
        } else {
            mProgressDrawable = ProgressDrawable()
            mProgressDrawable.setColor(-0x99999a)
            mProgressView.setImageDrawable(mProgressDrawable)
        }

        if (ta.hasValue(R.styleable.ClassicsHeader_srlTextSizeTitle)) {
            mTitleText.setTextSize(TypedValue.COMPLEX_UNIT_PX, ta.getDimensionPixelSize(R.styleable.ClassicsHeader_srlTextSizeTitle, DensityUtil.dp2px(16f)).toFloat())
        } else {
            mTitleText.textSize = 16f
        }

        if (ta.hasValue(R.styleable.ClassicsHeader_srlPrimaryColor)) {
            setPrimaryColor(ta.getColor(R.styleable.ClassicsHeader_srlPrimaryColor, 0))
        }
        if (ta.hasValue(R.styleable.ClassicsHeader_srlAccentColor)) {
            setAccentColor(ta.getColor(R.styleable.ClassicsHeader_srlAccentColor, 0))
        }

        ta.recycle()

        time += context.javaClass.name
        mShared = context.getSharedPreferences("ClassicsHeader", Context.MODE_PRIVATE)
    }

    override fun onFinish(layout: RefreshLayout, success: Boolean): Int {
        // 延迟500毫秒之后再弹回
        return super.onFinish(layout, success)
    }

    override fun onStateChanged(refreshLayout: RefreshLayout, oldState: RefreshState, newState: RefreshState) {
        val arrowView = mArrowView
        when (newState) {
            RefreshState.None, RefreshState.PullDownToRefresh -> {
                arrowView.visibility = View.VISIBLE
                arrowView.animate().rotation(0f)
            }
            RefreshState.Refreshing, RefreshState.RefreshReleased -> arrowView.visibility = View.GONE
            RefreshState.ReleaseToRefresh -> arrowView.animate().rotation(180f)
            RefreshState.ReleaseToTwoLevel -> arrowView.animate().rotation(0f)
            RefreshState.Loading -> arrowView.visibility = View.GONE
            else -> {
            }
        }
    }
}
