package qsos.lib.base.simple

import android.view.View
import androidx.annotation.LayoutRes
import qsos.lib.base.base.BaseAdapter
import qsos.lib.base.base.BaseHolder

/**
 * @author : 华清松
 * @description : 单类型Adapter
 */
class SimpleSingleAdapter<T>(
        @LayoutRes private val layoutId: Int,
        list: ArrayList<T>,
        private val setHolder: (holder: BaseHolder<T>, data: T, position: Int) -> Unit
) : BaseAdapter<T>(list) {

    override fun getHolder(view: View, viewType: Int): BaseHolder<T>? {
        return SimpleSingleHolder(view, setHolder)
    }

    override fun getLayoutId(viewType: Int): Int? {
        return layoutId
    }

    override fun onItemClick(view: View, position: Int, obj: Any?) {}

    override fun onItemLongClick(view: View, position: Int, obj: Any?) {}

    class SimpleSingleHolder<T>(
            val view: View,
            private val setHolder: (holder: BaseHolder<T>, data: T, position: Int) -> Unit
    ) : BaseHolder<T>(view) {
        override fun setData(data: T, position: Int) {
            setHolder(this, data, position)
        }

    }
}