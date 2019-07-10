package qsos.lib.base.base

import android.content.Context

/**
 * @author : 华清松
 * @description : View 接口
 */
interface BaseView {
    /**LayoutId*/
    val defLayoutId: Int

    /**获取上下文*/
    val mContext: Context?

    /** UI 是否存活*/
    val isActive: Boolean

    /**显示消息*/
    fun showToast(msg: String?)

    /**结束当前页面*/
    fun finishThis()

}