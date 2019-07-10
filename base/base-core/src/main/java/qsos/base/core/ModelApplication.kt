package qsos.base.core

import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import qsos.core.file.FileApplication
import qsos.core.lib.view.widget.list.MyClassicsHeader

/**
 * @author : 华清松
 * @description : 通用业务模块 Application ，你可以重写此类替换统一的刷新样式
 */
abstract class ModelApplication : FileApplication() {
    init {
        // 设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
            layout.setPrimaryColorsId(R.color.white, R.color.black)
            MyClassicsHeader(context)
        }
        // 设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout ->
            layout.setPrimaryColorsId(R.color.white, R.color.black)
            ClassicsFooter(context)
        }
    }
}