package qsos.core.lib.view.widget.hover

/**
 * [HoverView] 的控制接口,
 * 根据 [ViewState] 进行控制
 */
interface ViewStateManager {

    /**
     * 获取当前状态
     *
     * @return 当前状态
     */
    val state: ViewState

    /**
     * 根据 viewState,
     * 切换 [HoverView] 的状态, 带动画:
     *
     * 1. [ViewState.FILL]:   全屏
     * 2. [ViewState.HOVER]:  悬停
     * 3. [ViewState.CLOSE]:  关闭(隐藏)
     *
     * @param viewState 给定的 [ViewState]
     */
    fun changeState(viewState: ViewState)

    /**
     * 由 isSmoothScroll 控制是否带有动画,
     * 其余同 [.changeState]
     *
     * @param viewState 给定的 [ViewState]
     * @param isSmoothScroll 是否需要动画
     */
    fun changeState(viewState: ViewState, isSmoothScroll: Boolean)
}
