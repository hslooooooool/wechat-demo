package qsos.core.file

import android.annotation.SuppressLint
import android.content.Intent
import android.text.TextUtils
import com.qingmei2.rximagepicker.entity.Result
import io.reactivex.functions.Consumer
import qsos.core.lib.view.BaseModuleActivity
import qsos.lib.base.callback.OnTListener
import qsos.lib.base.data.app.FileEntity
import qsos.lib.base.data.app.FileTypeEnum
import qsos.lib.base.utils.file.ChatMediaPlayer
import qsos.lib.base.utils.file.FileUtils
import java.io.File

/**
 * @author : 华清松
 * @description : 业务模块公共 Activity
 */
@SuppressLint("CheckResult")
abstract class BaseFileModuleActivity : BaseModuleActivity(), IFileHelper.ITakeFile {

    private var mSendFileListener: IFileHelper.OnSendFileListener<FileTypeEnum>? = null

    /**设置文件上传监听*/
    fun setOnSendFileListener(fileListener: IFileHelper.OnSendFileListener<FileTypeEnum>) {
        this.mSendFileListener = fileListener
    }

    override fun uploadImageBeforeZip(file: File?, listener: OnTListener<File?>?) {
        if (listener == null) {
            TakeFileHelper(this).uploadImageBeforeZip(file, object : OnTListener<File?> {
                override fun getItem(item: File?) {
                    if (item == null) {
                        showToast("图片处理失败，无法上传，请重新选择")
                    } else {
                        mSendFileListener?.send(item, FileTypeEnum.IMAGE)
                    }
                }
            })
        } else {
            TakeFileHelper(this).uploadImageBeforeZip(file, listener)
        }
    }

    override fun takeCamera(next: Consumer<Result>) {
        TakeFileHelper(this).takeCamera(next)
    }

    override fun takeGallery(next: Consumer<Result>) {
        TakeFileHelper(this).takeGallery(next)
    }

    override fun takeWord(mimeTypes: ArrayList<String>, TAKE_FILE_CODE: Int) {
        TakeFileHelper(this).takeWord(mimeTypes, TAKE_FILE_CODE)
    }

    override fun takeVideo() {
        TakeFileHelper(this).takeVideo()
    }

    fun playAudio(path: String?, url: String?) {
        if (TextUtils.isEmpty(path)) {
            // 通过网络播放录音
            ChatMediaPlayer.init(mContext!!).listener(object : ChatMediaPlayer.PlayerListener {
                override fun onPlayerStop() {
                    showToast("播放完")
                }
            }).play(ChatMediaPlayer.PlayBuild(mContext!!, ChatMediaPlayer.PlayType.URL, url!!))
        } else {
            // 通过本地播放语音
            ChatMediaPlayer.init(mContext!!).listener(object : ChatMediaPlayer.PlayerListener {
                override fun onPlayerStop() {
                    showToast("播放完")
                }
            }).play(ChatMediaPlayer.PlayBuild(mContext!!, ChatMediaPlayer.PlayType.PATH, path!!))
        }
    }

    override fun onPause() {
        // NOTICE 进入后台一定要停止语音播放
        ChatMediaPlayer.stop()
        super.onPause()
    }

    override fun onDestroy() {
        // NOTICE 进入后台一定要释放语音播放
        ChatMediaPlayer.destroy()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            FileUtils.TAKE_VIDEO_CODE -> {
                if (data != null) {
                    val videoPath = data.getStringExtra("video_path")
                    if (TextUtils.isEmpty(videoPath)) {
                        showToast("视频不存在")
                    } else {
                        val file = File(videoPath!!)
                        // 上传视频
                        mSendFileListener?.send(file, FileTypeEnum.VIDEO)
                    }
                }
            }
            FileUtils.TAKE_FILE_CODE -> {
                RxTakeFileHelper.getFileFromData(this, data)
                        .subscribe(object : RxTakeFileHelper.TakeFileObserver() {

                            override fun onError(e: Throwable) {
                                showToast("文件选择失败，请重试")
                            }

                            override fun onNext(fileList: List<File>) {
                                for (file in fileList) {
                                    if (FileEntity.getFileType(file.extension) == FileTypeEnum.WORD) {
                                        // 上传文档
                                        mSendFileListener?.send(file, FileTypeEnum.WORD)
                                    } else {
                                        showToast("不支持此类型的文档上传")
                                    }
                                }
                            }
                        })
            }
        }
    }

}
