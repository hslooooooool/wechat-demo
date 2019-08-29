package qsos.base.find.view.adapter

import android.view.View
import android.widget.Toast
import qsos.base.find.R
import qsos.base.find.view.holder.TweetItemCommentViewHolder
import qsos.core.lib.data.chat.WeChatCommentBean
import qsos.lib.base.base.adapter.BaseAdapter
import qsos.lib.base.base.holder.BaseHolder

/**
 * @author : 华清松
 * 推特列表之评论列表容器
 */
class TweetCommentAdapter(
        list: ArrayList<WeChatCommentBean>
) : BaseAdapter<WeChatCommentBean>(list) {
    override fun getItemViewType(position: Int): Int {
        return R.layout.find_item_tweet_comment
    }

    override fun getHolder(view: View, viewType: Int): BaseHolder<WeChatCommentBean> {
        return TweetItemCommentViewHolder(view, this)
    }

    override fun getLayoutId(viewType: Int): Int {
        return viewType
    }

    override fun onItemClick(view: View, position: Int, obj: Any?) {

    }

    override fun onItemLongClick(view: View, position: Int, obj: Any?) {
        when (view.id) {
            R.id.item_tweet_comment_content_tv -> {
                Toast.makeText(view.context, "菜单$obj", Toast.LENGTH_SHORT).show()
            }
        }
    }
}