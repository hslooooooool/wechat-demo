package qsos.lib.base.base

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import qsos.lib.base.R
import qsos.lib.base.data.http.HttpCode
import qsos.lib.base.utils.LogUtil

/**
 * @author : 华清松
 * @description : Base Fragment
 */
@SuppressLint("SetTextI18n")
abstract class BaseFragment : Fragment(), BaseView {

    override var isActive: Boolean = false
        protected set(value) {
            field = value
        }

    override var mContext: Context? = null
        protected set(value) {
            field = value
        }

    private var mainView: View? = null

    /**设置视图ID*/
    abstract val layoutId: Int?

    /**视图重载是否重新加载数据*/
    abstract val reload: Boolean

    override val defLayoutId: Int
        get() = R.layout.fragment_default

    /*注意调用顺序*/

    /**初始化数据*/
    abstract fun initData(savedInstanceState: Bundle?)

    /**初始化视图*/
    abstract fun initView(view: View)

    /**获取数据*/
    abstract fun getData()

    /**网络请求统一更新布局*/
    abstract fun changHttpView(it: HttpCode)

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        initData(bundle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?): View? {
        LogUtil.i("创建:${javaClass.name}")

        mContext = context

        mainView = if (layoutId == null) {
            inflater.inflate(defLayoutId, container, false)
        } else {
            inflater.inflate(layoutId!!, container, false)
        }

        return mainView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // kotlin 务必在此进行 initView 操作，否则将出现空指针异常
        initView(view)
    }

    override fun onResume() {
        super.onResume()
        // 页面重现，重新加载数据
        if (reload) {
            getData()
        }
    }

    override fun finishThis() {
        (mContext as Activity).finish()
    }

    override fun showToast(msg: String?) {
        Toast.makeText(mContext, if (TextUtils.isEmpty(msg)) "没有信息" else msg, Toast.LENGTH_SHORT).show()
    }

    /**隐藏输入键盘*/
    fun hideKeyboard() {
        val imm: InputMethodManager = mContext?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm.isActive) {
            if (activity?.currentFocus?.windowToken != null) {
                imm.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }
    }

}
