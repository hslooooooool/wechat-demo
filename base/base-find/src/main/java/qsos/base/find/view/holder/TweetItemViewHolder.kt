package qsos.base.find.view.holder

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.find_item_tweet.view.*
import qsos.base.find.view.adapter.RecyclerViewSpacesItemDecoration
import qsos.base.find.view.adapter.TweetCommentAdapter
import qsos.base.find.view.adapter.TweetImageAdapter
import qsos.lib.base.base.BaseHolder
import qsos.lib.base.data.WeChatBeen
import qsos.lib.base.data.WeChatTweetBeen
import qsos.lib.base.utils.image.GlideApp

/**
 * @author : 华清松
 * @description : 推特列表项布局
 */
class TweetItemViewHolder(
        itemView: View
) : BaseHolder<WeChatBeen>(itemView) {

    override fun setData(data: WeChatBeen, position: Int) {
        if (data.data is WeChatTweetBeen) {
            val tweetBeen = data.data as WeChatTweetBeen
            GlideApp.with(itemView.context).load(tweetBeen.sender?.avatar)
                    // 圆角设置
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(itemView.item_tweet_head_iv)

            itemView.item_tweet_nick_tv.text = tweetBeen.sender?.nick
            itemView.item_tweet_content_tv.text = tweetBeen.content

            itemView.item_tweet_image_rv.layoutManager = GridLayoutManager(itemView.context, 3)
            itemView.item_tweet_image_rv.adapter = TweetImageAdapter(tweetBeen.images
                    ?: arrayListOf())
            itemView.item_tweet_image_rv.addItemDecoration(RecyclerViewSpacesItemDecoration(10, 0, 0, 5))

            itemView.item_tweet_comment_rv.layoutManager = LinearLayoutManager(itemView.context)
            itemView.item_tweet_comment_rv.adapter = TweetCommentAdapter(tweetBeen.comments
                    ?: arrayListOf())

        }
    }
}