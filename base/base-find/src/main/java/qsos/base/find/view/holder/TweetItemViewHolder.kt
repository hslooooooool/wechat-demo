package qsos.base.find.view.holder

import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import kotlinx.android.synthetic.main.find_item_tweet.view.*
import qsos.base.find.view.adapter.TweetCommentAdapter
import qsos.lib.base.base.BaseHolder
import qsos.lib.base.data.WeChatTweetBeen
import qsos.lib.base.utils.image.ImageLoaderUtils

/**
 * @author : 华清松
 * @description : 推特列表项布局
 */
class TweetItemViewHolder(
        itemView: View
) : BaseHolder<WeChatTweetBeen>(itemView) {

    override fun setData(data: WeChatTweetBeen, position: Int) {
        // 加载头像
        ImageLoaderUtils.displayRounded(itemView.context, itemView.item_tweet_head_iv, data.sender?.avatar)

        itemView.item_tweet_nick_tv.text = data.sender?.nick
        itemView.item_tweet_content_tv.text = data.content

        val images = arrayListOf<String>()
        data.images?.forEach {
            if (!TextUtils.isEmpty(it.url)) images.add(it.url!!)
        }
        itemView.item_tweet_image_ngl.setUrlList(images)

        itemView.item_tweet_comment_rv.layoutManager = LinearLayoutManager(itemView.context)
        itemView.item_tweet_comment_rv.adapter = TweetCommentAdapter(data.comments
                ?: arrayListOf())
        (itemView.item_tweet_comment_rv.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }
}