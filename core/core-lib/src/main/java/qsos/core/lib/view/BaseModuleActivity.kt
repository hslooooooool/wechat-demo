package qsos.core.lib.view

import android.annotation.SuppressLint
import qsos.lib.base.base.BaseActivity
import qsos.lib.base.data.http.HttpCode

/**
 * @author : 华清松
 * @description : 业务模块公共 Activity。通用业务这里处理，比如统一的网络返回错误页面处理
 */
@SuppressLint("CheckResult")
abstract class BaseModuleActivity : BaseActivity() {

    override fun initView() {

    }

    override fun changHttpView(it: HttpCode) {

    }
}
