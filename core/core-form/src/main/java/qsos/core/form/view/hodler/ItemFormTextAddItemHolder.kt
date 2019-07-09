package qsos.core.form.view.hodler

import android.view.View
import kotlinx.android.synthetic.main.form_item_text_add_item.view.*
import qsos.lib.base.base.BaseHolder
import qsos.lib.base.callback.OnRecyclerViewItemClickListener
import qsos.lib.base.data.form.Value

/**
 * @author : 华清松
 * @description : 表单文本添加列表项视图
 */
class ItemFormTextAddItemHolder(
        itemView: View,
        private val itemClick: OnRecyclerViewItemClickListener
) : BaseHolder<Value>(itemView) {

    override fun setData(data: Value, position: Int) {

        itemView.form_item_text_add_item_name.text = data.input_value

        itemView.form_item_text_add_item_delete.setOnClickListener {
            itemClick.onItemClick(it, position, data)
        }
        itemView.form_item_text_add_item_edit.setOnClickListener {
            itemClick.onItemClick(it, position, data)
        }

    }

}
