package qsos.base.core

import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import qsos.core.lib.config.AbsNetServiceConfig
import qsos.lib.base.base.BaseApplication
import timber.log.Timber
import vip.qsos.exception.GlobalExceptionHelper

/**
 * @author : 华清松
 * 通用业务模块 Application ，你可以重写此类替换统一的刷新样式
 */
abstract class ModelApplication : BaseApplication(), AbsNetServiceConfig {
    init {
        // 设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
            layout.setPrimaryColorsId(R.color.white, R.color.black)
            ClassicsHeader(context)
        }
        // 设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout ->
            layout.setPrimaryColorsId(R.color.white, R.color.black)
            ClassicsFooter(context)
        }
    }

    override fun onCreate() {
        super.onCreate()
        /**Timber 日志*/
        Timber.plant(Timber.DebugTree())
        /**全局异常捕获处理*/
        Thread.setDefaultUncaughtExceptionHandler(GlobalExceptionHelper)

        config()
    }
}