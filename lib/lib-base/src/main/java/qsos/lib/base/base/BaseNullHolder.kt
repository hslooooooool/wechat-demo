package qsos.lib.base.base

import android.view.View

/**
 * @author : 华清松
 * @desc : 子项布局 Holder
 */
class BaseNullHolder<T>(
        itemView: View
) : BaseHolder<T>(itemView) {
    override fun setData(data: T, position: Int) {

    }
}
