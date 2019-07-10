package qsos.core.lib.view

import android.annotation.SuppressLint
import qsos.lib.base.base.BaseActivity
import qsos.lib.base.data.http.HttpCode

/**
 * @author : 华清松
 * @description : 业务模块公共 Activity
 */
@SuppressLint("CheckResult")
abstract class BaseModuleActivity : BaseActivity() {

    override fun initView() {

    }

    override fun changHttpView(it: HttpCode) {

    }
}
