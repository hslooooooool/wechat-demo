package qsos.app.demo.view.activity

import android.os.Bundle
import android.os.Handler
import com.alibaba.android.arouter.launcher.ARouter
import qsos.app.demo.R
import qsos.base.core.view.BaseModuleActivity
import qsos.lib.base.base.BaseApplication
import qsos.lib.base.utils.ActivityManager
import vip.qsos.lib_data.router.FindPath


/**
 * @author : 华清松
 * 闪屏界面
 */
class SplashActivity : BaseModuleActivity() {

    override val layoutId = R.layout.app_activity_splash
    override val reload = false

    override fun initData(savedInstanceState: Bundle?) {}

    override fun initView() {
        super.initView()
        ActivityManager.finishAllButNotMe(this)

        mHandler.sendEmptyMessageDelayed(0, 1000)
    }

    override fun getData() {}

    private val mHandler = Handler {
        while (BaseApplication.buildFinish) {
            ARouter.getInstance().build(FindPath.TWEET_LIST).navigation()
            finish()
            break
        }
        return@Handler true
    }
}