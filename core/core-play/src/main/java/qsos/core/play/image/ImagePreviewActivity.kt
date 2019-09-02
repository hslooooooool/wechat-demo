package qsos.core.play.image

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.gson.Gson
import kotlinx.android.synthetic.main.play_activity_image.*
import qsos.core.lib.utils.image.ImageLoaderUtils
import qsos.core.play.PlayPath
import qsos.core.play.R
import qsos.lib.base.base.activity.BaseActivity
import qsos.lib.base.utils.ToastUtils.showToast

/**
 * @author : 华清松
 * 图片预览-放大缩小模式
 */
@SuppressLint("CheckResult", "SetTextI18n")
@Route(group = PlayPath.GROUP, path = PlayPath.IMAGE_PREVIEW)
class ImagePreviewActivity(
        override val layoutId: Int? = R.layout.play_activity_image,
        override val reload: Boolean = false
) : BaseActivity() {

    @Autowired(name = PlayPath.IMAGE_LIST)
    @JvmField
    var imageListDataString: String? = null

    private var imageListData: FileListData? = null

    private var mTotal: Int = 0
    private var mPosition: Int = 0
    private var mUrl: String? = ""
    private var mName: String? = ""

    override fun initData(savedInstanceState: Bundle?) {
        imageListData = Gson().fromJson(imageListDataString, FileListData::class.java)

        if (imageListData == null) {
            showToast(this, "图片预览失败，数据错误")
            finish()
            return
        }
        mPosition = imageListData!!.position
        mTotal = imageListData!!.imageList.size - 1
    }

    override fun initView() {
        play_image_last.setOnClickListener {
            when {
                mPosition <= 0 -> {
                    mPosition = 0
                    getData()
                    showToast(this, "已是第一张了")
                }
                mPosition > 0 -> {
                    mPosition--
                    getData()
                }
            }
        }

        play_image_next.setOnClickListener {
            when {
                mPosition >= mTotal -> {
                    mPosition = mTotal
                    getData()
                    showToast(this, "已是最后一张了")
                }
                mPosition < mTotal -> {
                    mPosition++
                    getData()
                }
            }
        }

        play_image_piv.setOnClickListener {
            finish()
        }

        getData()
    }

    override fun getData() {
        mUrl = imageListData!!.imageList[mPosition].url
        mName = imageListData!!.imageList[mPosition].name
        ImageLoaderUtils.displayNormal(mContext, play_image_piv, mUrl)

        val str = "${mPosition + 1}/${mTotal + 1}"
        play_image_desc.text = "${if (mTotal == 0) "" else str} $mName"

        val visibility = if (mTotal == 0) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }

        play_image_last.visibility = visibility
        play_image_next.visibility = visibility

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out)
    }

}