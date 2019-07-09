package qsos.core.form.view.hodler

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.form_item_text_add.view.*
import kotlinx.android.synthetic.main.form_normal_title.view.*
import qsos.core.form.view.adapter.FormAddTextAdapter
import qsos.lib.base.base.BaseHolder
import qsos.lib.base.callback.OnRecyclerViewItemClickListener
import qsos.lib.base.data.form.FormItem

/**
 * @author : 华清松
 * @description : 表单文本添加列表项视图
 */
class ItemFormTextAddHolder(
        itemView: View,
        private val itemClick: OnRecyclerViewItemClickListener
) : BaseHolder<FormItem>(itemView) {

    override fun setData(data: FormItem, position: Int) {

        /*绑定数据*/
        itemView.item_form_key.text = data.form_item_key

        itemView.item_form_text_add_rv.layoutManager = LinearLayoutManager(itemView.context)
        itemView.item_form_text_add_rv.adapter = FormAddTextAdapter(data.form_item_value!!.values
                ?: arrayListOf())

        /*监听*/
        itemView.item_form_key.setOnClickListener {
            itemClick.onItemClick(it, position, data)
        }
        itemView.item_form_text_add.setOnClickListener {
            itemClick.onItemClick(it, position, data)
        }

    }

}
