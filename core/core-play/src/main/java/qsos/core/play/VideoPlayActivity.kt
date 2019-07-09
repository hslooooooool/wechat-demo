package qsos.core.play

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.dueeeke.videocontroller.StandardVideoController
import kotlinx.android.synthetic.main.play_activity_main.*
import qsos.lib.base.base.BaseNormalActivity
import qsos.lib.base.routepath.PlayPath

/**
 * @author : 华清松
 * @description : 视屏播放主页
 */
@Route(group = PlayPath.GROUP, path = PlayPath.VIDEO_PREVIEW)
class VideoPlayActivity(
        override val layoutId: Int? = R.layout.play_activity_main
) : BaseNormalActivity() {

    @Autowired(name = PlayPath.VIDEO_URL)
    @JvmField
    var videoUrl: String? = null
    @Autowired(name = PlayPath.VIDEO_NAME)
    @JvmField
    var videoName: String? = null
    @Autowired(name = PlayPath.VIDEO_PATH)
    @JvmField
    var videoPath: String? = null

    private lateinit var videoController: StandardVideoController

    override fun initData(savedInstanceState: Bundle?) {
    }

    override fun initView() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR

        if (TextUtils.isEmpty(videoUrl)) {
            if (!TextUtils.isEmpty(videoPath)) {
                play_main_vv.setUrl(videoPath)
            } else {
                showToast("播放链接错误")
                finishThis()
                return
            }
        } else {
            play_main_vv.setUrl(videoUrl)
        }
        videoController = StandardVideoController(this)
        videoController.setTitle(videoName)
        play_main_vv.setVideoController(videoController)

        // 自动开始播放
        play_main_vv.start()
    }

    override fun getData() {
    }

    override fun onBackPressed() {
        if (play_main_vv.onBackPressed()) return
        super.onBackPressed()
    }

    // 记录退出时播放状态 回来的时候继续播放
    private var playFlag: Boolean = false
    // 记录销毁时的进度 回来继续该进度播放
    private var position: Long = 0

    private val handler = Handler()

    public override fun onResume() {
        super.onResume()
        if (playFlag) {
            play_main_vv.resume()
        }
        handler.removeCallbacks(runnable)
        if (position > 0) {
            play_main_vv.seekTo(position)
            position = 0
        }
    }

    public override fun onPause() {
        super.onPause()
        // 暂停
        playFlag = play_main_vv.isPlaying
        play_main_vv.pause()
    }

    public override fun onStop() {
        super.onStop()
        // 进入后台不马上销毁,延时15秒
        handler.postDelayed(runnable, 1000 * 15)
    }

    public override fun onDestroy() {
        super.onDestroy()
        play_main_vv.release()
        handler.removeCallbacks(runnable)
    }

    private val runnable = Runnable {
        play_main_vv.release()
    }

}