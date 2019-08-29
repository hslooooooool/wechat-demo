package qsos.base.find.view.adapter

import android.view.View
import qsos.base.find.R
import qsos.base.find.view.holder.TweetItemViewHolder
import qsos.core.lib.data.chat.WeChatTweetBeen
import qsos.lib.base.base.adapter.BaseAdapter
import qsos.lib.base.base.holder.BaseHolder

/**
 * @author : 华清松
 * 朋友圈列表容器
 */
class TweetAdapter(
        list: ArrayList<WeChatTweetBeen>
) : BaseAdapter<WeChatTweetBeen>(list) {

    override fun getItemViewType(position: Int): Int {
        return R.layout.find_item_tweet
    }

    override fun getHolder(view: View, viewType: Int): BaseHolder<WeChatTweetBeen> {
        return TweetItemViewHolder(view)
    }

    override fun getLayoutId(viewType: Int): Int {
        return viewType
    }

    override fun onItemClick(view: View, position: Int, obj: Any?) {}

    override fun onItemLongClick(view: View, position: Int, obj: Any?) {}
}