package qsos.core.lib.view

import android.annotation.SuppressLint
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import qsos.lib.base.base.BaseFragment
import qsos.lib.base.base.BaseView
import qsos.lib.base.data.http.HttpCode

/**
 * @author : 华清松
 * @description : 业务模块公共 Fragment
 */
@SuppressLint("CheckResult")
abstract class BaseModuleFragment : BaseFragment() {

    var mHttpNetCode = MutableLiveData<HttpCode>()

    override fun initView(view: View) {
        mHttpNetCode.observe(this, Observer {
            changHttpView(it)
        })
    }

    override fun changHttpView(it: HttpCode) {
        when (it) {
            HttpCode.UNAUTHORIZED -> {
                changeBaseView(BaseView.STATE.SERVICE_ERROR)
            }
            HttpCode.NOT_NETWORK -> {
                changeBaseView(BaseView.STATE.NOT_NET)
            }
            HttpCode.NOT_FOUND -> {
                changeBaseView(BaseView.STATE.NOT_FOUND)
            }
            HttpCode.SERVER_ERROR -> {
                changeBaseView(BaseView.STATE.SERVICE_ERROR)
            }
            HttpCode.RESULT_NULL -> {
                changeBaseView(BaseView.STATE.RESULT_NULL)
            }
            HttpCode.SUCCESS -> changeBaseView(BaseView.STATE.OK)
            HttpCode.LOADING -> changeBaseView(BaseView.STATE.LOADING)
            else -> {
                changeBaseView(BaseView.STATE.SERVICE_ERROR)
            }
        }
    }
}