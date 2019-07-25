package qsos.lib.base.base

import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.alibaba.android.arouter.launcher.ARouter
import qsos.lib.base.BuildConfig
import qsos.lib.base.helper.GlobalExceptionHelper
import qsos.lib.base.utils.data.SharedPreUtils
import timber.log.Timber
import timber.log.Timber.DebugTree

/**
 * @author : 华清松
 * @description : BaseApplication
 */
open class BaseApplication : MultiDexApplication() {

    companion object {
        lateinit var appContext: BaseApplication
        /**Application 初始化是否完成*/
        var buildFinish: Boolean = false
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
        /**阿里路由配置*/
        if (BuildConfig.DEBUG) {
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(this)

        /**全局异常捕获处理*/
        Thread.setDefaultUncaughtExceptionHandler(GlobalExceptionHelper)

        /**Timber 日志*/
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        /*是否第一次启动APP判断*/
        if (SharedPreUtils.getBoolean(base, "FirstLaunch")) {
            SharedPreUtils.saveBoolean(base, "FirstLaunch", true)
            // 首次启动
            Thread(Runnable {
                MultiDex.install(this)
                buildFinish = true
            }).start()
        } else {
            // 非首次启动
            MultiDex.install(this)
            buildFinish = true
        }
    }

}
