package qsos.base.find.view.holder

import android.annotation.SuppressLint
import android.view.View
import com.noober.menu.FloatMenu
import kotlinx.android.synthetic.main.find_item_tweet_comment.view.*
import qsos.lib.base.base.holder.BaseHolder
import qsos.lib.base.callback.OnListItemClickListener
import vip.qsos.lib_data.data._do.chat.WeChatCommentBean

/**
 * @author : 华清松
 * 推特列表项之图片列表项布局
 */
class TweetItemCommentViewHolder(
        itemView: View,
        private val itemListener: OnListItemClickListener
) : BaseHolder<WeChatCommentBean>(itemView) {
    var floatMenu: FloatMenu? = null
    @SuppressLint("SetTextI18n")
    override fun setData(data: WeChatCommentBean, position: Int) {
        itemView.item_tweet_comment_name_tv.text = data.sender?.nick
        itemView.item_tweet_comment_content_tv.text = ":${data.content}"
        if (floatMenu == null) {
            floatMenu = FloatMenu(itemView.context, itemView.item_tweet_comment_content_tv)
            floatMenu?.items("回复", "删除")
        }
        floatMenu?.setOnItemClickListener { _, index ->
            itemListener.onItemLongClick(itemView.item_tweet_comment_content_tv, position, index)
        }

        itemView.item_tweet_comment_content_tv.setOnLongClickListener {
            floatMenu?.show()
            return@setOnLongClickListener true
        }
    }
}