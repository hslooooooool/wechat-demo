package qsos.base.find.view.holder

import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.find_item_tweet_image.view.*
import qsos.lib.base.base.BaseApplication
import qsos.lib.base.base.BaseHolder
import qsos.lib.base.data.WeChatImageBean
import qsos.lib.base.utils.image.GlideApp

/**
 * @author : 华清松
 * @description : 推特列表项之图片列表项布局
 */
class TweetItemImageViewHolder(
        itemView: View
) : BaseHolder<WeChatImageBean>(itemView) {

    override fun setData(data: WeChatImageBean, position: Int) {
        itemView.item_tweet_image_iv.layoutParams = ViewGroup.LayoutParams(BaseApplication.itemImageWidth, BaseApplication.itemImageWidth)
        GlideApp.with(itemView.context).load(data.url).into(itemView.item_tweet_image_iv)
    }
}