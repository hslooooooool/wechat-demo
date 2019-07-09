package qsos.lib.base.base

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * @author : 华清松
 * @desc : 子项布局 Holder
 */
@SuppressLint("SetTextI18n")
abstract class BaseHolder<T>(
        itemView: View
) : RecyclerView.ViewHolder(itemView) {

    /**设置数据 */
    abstract fun setData(data: T, position: Int)

    /** 释放资源 */
    open fun onRelease() {

    }

}
