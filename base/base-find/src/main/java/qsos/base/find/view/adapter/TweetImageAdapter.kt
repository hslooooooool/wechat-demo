package qsos.base.find.view.adapter

import android.view.View
import qsos.base.find.R
import qsos.base.find.view.holder.TweetItemImageViewHolder
import qsos.lib.base.base.BaseAdapter
import qsos.lib.base.base.BaseHolder
import qsos.lib.base.data.WeChatImageBean

/**
 * @author : 华清松
 * @description : 推特列表之图片列表容器
 */
class TweetImageAdapter(
        list: ArrayList<WeChatImageBean>
) : BaseAdapter<WeChatImageBean>(list) {
    override fun getItemViewType(position: Int): Int {
        return R.layout.find_item_tweet_image
    }

    override fun getHolder(view: View, viewType: Int): BaseHolder<WeChatImageBean>? {
        return TweetItemImageViewHolder(view)
    }

    override fun getLayoutId(viewType: Int): Int? {
        return viewType
    }

    override fun onItemClick(view: View, position: Int, obj: Any?) {

    }

    override fun onItemLongClick(view: View, position: Int, obj: Any?) {

    }
}