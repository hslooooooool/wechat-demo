package qsos.core.lib.view.widget.hover

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import qsos.lib.lib.R

/**
 * 类似于 android.support.design.widget.NavigationView
 */
class HoverView : LinearLayout, ViewStateManager {

    private var mContext: Context? = null

    var topFill = 0.0F
    var topHover = 0.4F
    var bottomHeight = 0.0F

    val container: HoverViewContainer?
        get() = if (this.parent is HoverViewContainer) this.parent as HoverViewContainer else null

    constructor(context: Context) : super(context) {
        init(context)
        initAttrs(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
        initAttrs(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
        initAttrs(context, attrs)
    }

    private fun init(context: Context) {
        mContext = context
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.HoverView)
        topFill = ta.getFloat(R.styleable.HoverView_mTopFill, topFill)
        topHover = ta.getFloat(R.styleable.HoverView_mTopHover, topHover)
        bottomHeight = ta.getFloat(R.styleable.HoverView_mBottomHeight, 0F)
        ta.recycle()
    }

    override val state: ViewState
        get() = if (container != null) container!!.state else ViewState.CLOSE

    override fun changeState(viewState: ViewState) {
        changeState(viewState, true)
    }

    override fun changeState(viewState: ViewState, isSmoothScroll: Boolean) {
        if (container != null) {
            container!!.changeState(viewState, isSmoothScroll)
        }
    }

}
