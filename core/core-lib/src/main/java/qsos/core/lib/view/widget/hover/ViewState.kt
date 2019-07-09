package qsos.core.lib.view.widget.hover

/**
 * [HoverView] 的状态
 */
enum class ViewState {
    // 全屏
    FILL {
        override fun getTop(hoverView: HoverView): Int {
            return getTopByScale(hoverView, hoverView.topFill)
        }
    },
    // 半空悬停
    HOVER {
        override fun getTop(hoverView: HoverView): Int {
            return getTopByScale(hoverView, hoverView.topHover)
        }
    },
    // 关闭: 完全藏在屏幕底部
    CLOSE {
        override fun getTop(hoverView: HoverView): Int {
            return getTopByScale(hoverView, hoverView.bottomHeight)
        }
    };


    /**
     * FILL, HOVER... 各自状态对应高度: 即 View.getTop() 属性
     * @param hoverView 指定的 hoverView
     * @return
     */
    internal abstract fun getTop(hoverView: HoverView): Int

    fun getTopByScale(hoverView: HoverView, scale: Float): Int {
        return if (hoverView.container == null) 0 else {
            (scale * hoverView.container!!.measuredHeight).toInt()
        }
    }
}
