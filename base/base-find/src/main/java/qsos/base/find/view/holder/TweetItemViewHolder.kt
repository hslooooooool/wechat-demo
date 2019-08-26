package qsos.base.find.view.holder

import android.text.TextUtils
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.find_item_tweet.view.*
import qsos.base.find.view.adapter.TweetCommentAdapter
import qsos.core.lib.view.widget.image.NineGridLayout
import qsos.lib.base.base.BaseHolder
import vip.qsos.lib_data.data.WeChatTweetBeen
import vip.qsos.lib_data.data.play.FileData
import vip.qsos.lib_data.data.play.FileListData
import vip.qsos.lib_data.router.PlayPath
import qsos.lib.base.utils.ToastUtils
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

        itemView.item_tweet_image_ngl.setOnClickListener(object : NineGridLayout.OnImageClickListener {
            override fun onClickImage(view: View, position: Int, urls: List<String>) {
                val fileDataList = arrayListOf<FileData>()
                urls.forEach {
                    fileDataList.add(FileData(it, it))
                }
                val fileListData = FileListData(position, fileDataList)
                val optionsCompat = ActivityOptionsCompat.makeScaleUpAnimation(view, view.width / 2, view.height / 2, 0, 0)
                ARouter.getInstance().build(PlayPath.IMAGE_PREVIEW)
                        .withString(PlayPath.IMAGE_LIST, Gson().toJson(fileListData))
                        .withOptionsCompat(optionsCompat)
                        .navigation()
            }

            override fun onLongClickImage(view: View, position: Int, url: String, index: Int) {
                ToastUtils.showToast(itemView.context, "菜单$index")
            }
        })

        itemView.item_tweet_comment_rv.layoutManager = LinearLayoutManager(itemView.context)
        itemView.item_tweet_comment_rv.adapter = TweetCommentAdapter(data.comments
                ?: arrayListOf())
        (itemView.item_tweet_comment_rv.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }
}