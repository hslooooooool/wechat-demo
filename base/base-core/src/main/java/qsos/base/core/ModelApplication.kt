package qsos.base.core

import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import qsos.lib.base.base.BaseApplication

/**
 * @author : 华清松
 * @description : 通用业务模块 Application ，你可以重写此类替换统一的刷新样式
 */
abstract class ModelApplication : BaseApplication() {
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
}