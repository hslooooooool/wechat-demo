package qsos.app.demo.view.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import qsos.app.demo.R
import qsos.core.lib.view.BaseModuleActivity
import qsos.lib.base.base.BaseApplication
import qsos.lib.base.routepath.AppPath
import qsos.lib.base.routepath.FindPath
import qsos.lib.base.utils.activity.ActivityUtils


/**
 * @author : 华清松
 * @description : 闪屏界面
 */
@Route(group = AppPath.GROUP, path = AppPath.SPLASH)
class SplashActivity : BaseModuleActivity() {

    override val layoutId = R.layout.app_activity_splash
    override val reload = false

    override fun initData(savedInstanceState: Bundle?) {}

    @SuppressLint("CheckResult", "RestrictedApi")
    override fun initView() {
        super.initView()
        ActivityUtils.instance.finishAllButNotMe(this)

        mHandler.sendEmptyMessageDelayed(0, 1000)
    }

    override fun getData() {}

    private val mHandler = Handler {
        while (BaseApplication.buildFinish) {
            ARouter.getInstance().build(FindPath.TWEET_LIST).navigation()
            finishThis()
            break
        }
        return@Handler true
    }
}