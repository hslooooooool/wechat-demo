package qsos.core.play.image

import android.app.Activity
import android.view.View
import kotlinx.android.synthetic.main.play_item_image.view.*
import qsos.core.play.R
import qsos.lib.base.base.BaseAdapter
import qsos.lib.base.base.BaseHolder
import vip.qsos.lib_data.data.play.FileData
import qsos.lib.base.utils.image.ImageLoaderUtils

/**
 * @author : 华清松
 * @description : 图片预览容器
 */
class ImageAdapter(files: ArrayList<FileData>) : BaseAdapter<FileData>(files) {

    override fun getHolder(view: View, viewType: Int): BaseHolder<FileData>? {
        return ImageHolder(view)
    }

    override fun getLayoutId(viewType: Int): Int? {
        return R.layout.play_item_image
    }

    override fun onItemClick(view: View, position: Int, obj: Any?) {
        (mContext as Activity).finish()
    }

    override fun onItemLongClick(view: View, position: Int, obj: Any?) {

    }

}

class ImageHolder(itemView: View) : BaseHolder<FileData>(itemView) {
    override fun setData(data: FileData, position: Int) {
        ImageLoaderUtils.display(itemView.context, itemView.play_image_iv, data.url)
    }

}