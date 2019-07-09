package qsos.core.lib.view.adapter

import android.view.View
import kotlinx.android.synthetic.main.item_image.view.*
import qsos.lib.base.base.BaseHolder
import qsos.lib.base.callback.OnRecyclerViewItemClickListener
import qsos.lib.base.data.app.FileEntity
import qsos.lib.base.utils.image.ImageLoaderUtils

/**
 * @author : 华清松
 * @description : 普通附件列表
 */
class BaseImageViewHolder(
        itemView: View,
        private val itemClick: OnRecyclerViewItemClickListener
) : BaseHolder<FileEntity>(itemView) {
    override fun setData(data: FileEntity, position: Int) {
        ImageLoaderUtils.display(itemView.context, itemView.item_image, data.url)

        itemView.item_image.setOnClickListener {
            itemClick.onItemClick(it, position, data)
        }
    }
}