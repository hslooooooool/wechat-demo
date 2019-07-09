package qsos.core.form.view.hodler

import android.text.TextUtils
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.form_item_file_item.view.*
import qsos.core.form.R
import qsos.lib.base.base.BaseHolder
import qsos.lib.base.callback.OnRecyclerViewItemClickListener
import qsos.lib.base.data.app.FileTypeEnum
import qsos.lib.base.data.form.Value
import qsos.lib.base.data.play.FileData
import qsos.lib.base.data.play.FileListData
import qsos.lib.base.routepath.PlayPath
import qsos.lib.base.utils.ToastUtils
import qsos.lib.base.utils.file.ChatMediaPlayer
import qsos.lib.base.utils.file.FileUtils
import qsos.lib.base.utils.image.ImageLoaderUtils

/**
 * @author : 华清松
 * @description : 图片文件布局
 */
class FormFileHolder(
        itemView: View,
        private val itemClick: OnRecyclerViewItemClickListener
) : BaseHolder<Value>(itemView) {

    override fun setData(data: Value, position: Int) {

        when (data.file_type) {
            FileTypeEnum.IMAGE.name -> {
                ImageLoaderUtils.display(itemView.context, itemView.iv_item_form_file_icon, data.file_url)
            }
            FileTypeEnum.VIDEO.name -> {
                ImageLoaderUtils.display(itemView.context, itemView.iv_item_form_file_icon, R.drawable.take_video)
            }
            FileTypeEnum.AUDIO.name -> {
                ImageLoaderUtils.display(itemView.context, itemView.iv_item_form_file_icon, R.drawable.take_audio)
            }
            FileTypeEnum.WORD.name -> {
                ImageLoaderUtils.display(itemView.context, itemView.iv_item_form_file_icon, R.drawable.file)
            }
        }

        itemView.tv_item_form_file_name.text = data.file_name

        itemView.iv_item_form_file_icon.setOnClickListener {
            when (data.file_type) {
                FileTypeEnum.IMAGE.name -> {
                    imagePreview(it, data)
                }
                FileTypeEnum.VIDEO.name -> {
                    videoPreview(data)
                }
                FileTypeEnum.AUDIO.name -> {
                    audioPlay(data.file_url, data.file_url)
                }
            }
        }
        itemView.iv_item_form_file_delete.setOnClickListener {
            itemClick.onItemClick(it, position, data)
        }
    }

    private fun imagePreview(view: View, value: Value) {
        val optionsCompat = ActivityOptionsCompat.makeScaleUpAnimation(view,
                view.width / 2, view.height / 2, 0, 0)
        ARouter.getInstance().build(PlayPath.IMAGE_PREVIEW)
                .withString(PlayPath.IMAGE_LIST, Gson().toJson(FileListData(0,
                        arrayListOf(FileData(value.file_name, value.file_url)))))
                .withOptionsCompat(optionsCompat)
                .navigation()
    }

    private fun videoPreview(value: Value) {
        // 视频播放
        val fileUrl = value.file_url
        val saveName = FileUtils.getFileNameByUrl(fileUrl)
        val savePath = "${FileUtils.VIDEO_PATH}/$saveName"
        ARouter.getInstance().build(PlayPath.VIDEO_PREVIEW)
                .withString(PlayPath.VIDEO_URL, fileUrl)
                .withString(PlayPath.VIDEO_PATH, fileUrl ?: savePath)
                .withString(PlayPath.VIDEO_NAME, value.file_name ?: saveName)
                .navigation()
    }

    private fun audioPlay(path: String?, url: String?) {
        val mPlayType: ChatMediaPlayer.PlayType
        val mPlay: String
        if (TextUtils.isEmpty(path)) {
            mPlayType = ChatMediaPlayer.PlayType.URL
            mPlay = url!!
        } else {
            mPlayType = ChatMediaPlayer.PlayType.PATH
            mPlay = path!!
        }
        if (TextUtils.isEmpty(mPlay)) {
            ToastUtils.showToast(itemView.context, "音频播放失败,文件不存在")
            return
        }
        ChatMediaPlayer.init(itemView.context).listener(object : ChatMediaPlayer.PlayerListener {
            override fun onPlayerStop() {

            }
        }).play(ChatMediaPlayer.PlayBuild(itemView.context, mPlayType, mPlay))
    }
}
