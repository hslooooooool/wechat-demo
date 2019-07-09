package qsos.base.find.view.adapter

import android.view.View
import qsos.base.find.R
import qsos.base.find.view.holder.TweetItemCommentViewHolder
import qsos.lib.base.base.BaseAdapter
import qsos.lib.base.base.BaseHolder
import qsos.lib.base.data.WeChatCommentBean

/**
 * @author : 华清松
 * @description : 推特列表之评论列表容器
 */
class TweetCommentAdapter(
        list: ArrayList<WeChatCommentBean>
) : BaseAdapter<WeChatCommentBean>(list) {
    override fun getItemViewType(position: Int): Int {
        return R.layout.find_item_tweet_image
    }

    override fun getHolder(view: View, viewType: Int): BaseHolder<WeChatCommentBean>? {
        return TweetItemCommentViewHolder(view)
    }

    override fun getLayoutId(viewType: Int): Int? {
        return viewType
    }

    override fun onItemClick(view: View, position: Int, obj: Any?) {

    }

    override fun onItemLongClick(view: View, position: Int, obj: Any?) {

    }
}