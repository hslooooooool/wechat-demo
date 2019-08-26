package qsos.core.lib.view

import android.annotation.SuppressLint
import android.view.View
import qsos.lib.base.base.BaseFragment
import vip.qsos.lib_data.data.http.HttpCode

/**
 * @author : 华清松
 * @description : 业务模块公共 Fragment
 */
@SuppressLint("CheckResult")
abstract class BaseModuleFragment : BaseFragment() {

    override fun initView(view: View) {

    }

    override fun changHttpView(it: HttpCode) {

    }
}