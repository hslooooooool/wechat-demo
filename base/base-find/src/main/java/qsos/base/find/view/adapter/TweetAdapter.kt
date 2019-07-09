package qsos.base.find.view.adapter

import android.view.View
import qsos.base.find.R
import qsos.base.find.view.holder.TweetItemViewHolder
import qsos.base.find.view.holder.TweetHeadViewHolder
import qsos.lib.base.base.BaseAdapter
import qsos.lib.base.base.BaseHolder
import qsos.lib.base.data.WeChatBeen

/**
 * @author : 华清松
 * @description : 朋友圈列表容器
 */
class TweetAdapter(
        list: ArrayList<WeChatBeen>
) : BaseAdapter<WeChatBeen>(list) {
    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> R.layout.find_item_tweet_head
            else -> R.layout.find_item_tweet
        }
    }

    override fun getHolder(view: View, viewType: Int): BaseHolder<WeChatBeen>? {
        return when (viewType) {
            R.layout.find_item_tweet_head -> TweetHeadViewHolder(view)
            else -> TweetItemViewHolder(view)
        }
    }

    override fun getLayoutId(viewType: Int): Int? {
        return viewType
    }

    override fun onItemClick(view: View, position: Int, obj: Any?) {

    }

    override fun onItemLongClick(view: View, position: Int, obj: Any?) {

    }
}