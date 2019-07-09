package qsos.base.find.view.holder

import android.annotation.SuppressLint
import android.view.View
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.find_item_tweet_head.view.*
import qsos.lib.base.base.BaseHolder
import qsos.lib.base.data.WeChatBeen
import qsos.lib.base.data.WeChatUserBeen
import qsos.lib.base.utils.image.GlideApp

/**
 * @author : 华清松
 * @description : 用户信息布局
 */
class TweetHeadViewHolder(
        itemView: View
) : BaseHolder<WeChatBeen>(itemView) {

    @SuppressLint("SetTextI18n")
    override fun setData(data: WeChatBeen, position: Int) {
        if (data.data is WeChatUserBeen) {
            val userBeen = data.data as WeChatUserBeen
            GlideApp.with(itemView.context).load(userBeen.profileImage)
                    // 磁盘缓存
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(itemView.item_tweet_head_profile_iv)
            GlideApp.with(itemView.context).load(userBeen.avatar)
                    // 圆角设置
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(itemView.item_tweet_head_avatar_iv)

            itemView.item_tweet_head_name_tv.text = userBeen.nick
        }
    }
}