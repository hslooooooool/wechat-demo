package qsos.app.demo.view.activity

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
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

    override val layoutId = qsos.app.demo.R.layout.app_activity_splash
    override val reload = false
    override var statusBarColor: Int? = qsos.app.demo.R.color.white

    override fun initData(savedInstanceState: Bundle?) {}

    @SuppressLint("CheckResult", "RestrictedApi")
    override fun initView() {
        super.initView()
        ActivityUtils.instance.finishAllButNotMe(this)

        val width = Resources.getSystem().displayMetrics.widthPixels
        val density = Resources.getSystem().displayMetrics.density
        val imageWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40f, Resources.getSystem().displayMetrics) * density
        // FIXME 临时处理图片适配
        BaseApplication.itemImageWidth = ((width - imageWidth) / 3).toInt()

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