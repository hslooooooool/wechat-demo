package qsos.core.lib.view.widget.hover

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper

/**
 * [HoverView] 的容器,
 * 类似于[androidx.drawerlayout.widget.DrawerLayout]
 */
class HoverViewContainer : FrameLayout, ViewStateManager {
    override val state: ViewState
        get() = mViewState

    private var mContext: Context? = null

    private var mBottomDrag: ViewDragHelper? = null
    private val mBottomCallback = DragCallback()

    private var mBottomView: HoverView? = null

    // ------ 滑动部分 end ------

    private var mViewState = ViewState.CLOSE

    private inner class DragCallback : ViewDragHelper.Callback() {
        // 仅捕获 mBottomView
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return child === mBottomView
        }

        // 控制边界, 防止 mBottomView 的头部超出边界
        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            if (child === mBottomView) {
                var newTop = top
                newTop = Math.max(newTop, ViewState.FILL.getTop(mBottomView!!))
                return newTop
            }
            return top
        }

        // 手指释放的时候回调
        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            if (releasedChild === mBottomView) {
                val curTop = releasedChild.getTop()

                setClosestStateIfBetween(ViewState.FILL, ViewState.HOVER, curTop)
                setClosestStateIfBetween(ViewState.HOVER, ViewState.CLOSE, curTop)
            }
        }

        private fun setClosestStateIfBetween(beginState: ViewState, endState: ViewState, curTop: Int) {
            val beginTop = getTopOfState(beginState)
            val endTop = getTopOfState(endState)
            /*整个视图的顶点在起始点和结束点之间时修正其停止所在位置——顶部，底部，
            * 否者滑动到哪里就停在哪里
            */
            if (curTop in beginTop..endTop) {
                changeState(if (curTop < (beginTop + endTop) / 2) beginState else endState)
            }
            /*禁止到比CLOSE状态还低的状态*/
            if (curTop > endTop) {
                changeState(endState)
            }
        }
    }

    internal fun getTopOfState(viewState: ViewState): Int {
        return viewState.getTop(mBottomView!!)
    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        mContext = context
        mBottomDrag = ViewDragHelper.create(this, TOUCH_SLOP_SENSITIVITY, mBottomCallback)
        mBottomDrag!!.setEdgeTrackingEnabled(ViewDragHelper.EDGE_BOTTOM)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        mBottomView = findHoverView()
    }

    private fun findHoverView(): HoverView? {
        for (i in 0 until childCount) {
            if (getChildAt(i) is HoverView) {
                return getChildAt(i) as HoverView
            }
        }
        return null
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return mBottomDrag!!.shouldInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        mBottomDrag!!.processTouchEvent(ev)
        return true
    }

    // ------ 滑动部分 begin ------
    private fun smoothScrollTo(finalTop: Int) {
        mBottomDrag!!.smoothSlideViewTo(mBottomView!!, 0, finalTop)
        ViewCompat.postInvalidateOnAnimation(this)
    }

    // smoothScrollTo() 中用到 mScroller,
    // 不要忘了配合 computeScroll()
    override fun computeScroll() {
        if (mBottomDrag!!.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    private fun scrollTo(finalTop: Int) {
        ViewCompat.offsetTopAndBottom(mBottomView!!, finalTop - top)
        ViewCompat.postInvalidateOnAnimation(this)
    }

    override fun changeState(viewState: ViewState) {
        changeState(viewState, true)
    }

    override fun changeState(viewState: ViewState, isSmoothScroll: Boolean) {
        mViewState = viewState
        if (isSmoothScroll) {
            smoothScrollTo(getTopOfState(viewState))
        } else {
            scrollTo(getTopOfState(viewState))
        }
    }

    companion object {
        const val TOUCH_SLOP_SENSITIVITY = 1f
    }
}
