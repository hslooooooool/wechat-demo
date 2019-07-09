package qsos.base.find.view.holder

import android.annotation.SuppressLint
import android.view.View
import kotlinx.android.synthetic.main.find_item_tweet_comment.view.*
import qsos.lib.base.base.BaseHolder
import qsos.lib.base.data.WeChatCommentBean

/**
 * @author : 华清松
 * @description : 推特列表项之图片列表项布局
 */
class TweetItemCommentViewHolder(
        itemView: View
) : BaseHolder<WeChatCommentBean>(itemView) {
    @SuppressLint("SetTextI18n")
    override fun setData(data: WeChatCommentBean, position: Int) {
        itemView.item_tweet_comment_name_tv.text = data.sender?.nick
        itemView.item_tweet_comment_content_tv.text = ":${data.content}"
    }
}