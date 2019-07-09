package qsos.core.lib.view.adapter

import android.text.TextUtils
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.Gson
import qsos.lib.base.base.BaseAdapter
import qsos.lib.base.base.BaseHolder
import qsos.lib.base.data.app.FileEntity
import qsos.lib.base.data.app.FileTypeEnum
import qsos.lib.base.data.play.FileData
import qsos.lib.base.data.play.FileListData
import qsos.lib.base.routepath.PlayPath
import qsos.lib.lib.R

/**
 * @author : 华清松
 * @description : 普通图片列表容器
 */
class BaseImageAdapter(
        list: ArrayList<FileEntity>
) : BaseAdapter<FileEntity>(list) {

    override fun getHolder(view: View, viewType: Int): BaseHolder<FileEntity>? {
        return BaseImageViewHolder(view, this)
    }

    override fun getLayoutId(viewType: Int): Int? {
        return R.layout.item_image
    }

    override fun onItemClick(view: View, position: Int, obj: Any?) {
        when (view.id) {
            R.id.item_image -> {
                // 图片预览
                val optionsCompat = ActivityOptionsCompat.makeScaleUpAnimation(view, view.width / 2, view.height / 2, 0, 0)
                ARouter.getInstance().build(PlayPath.IMAGE_PREVIEW)
                        .withString(PlayPath.IMAGE_LIST, Gson().toJson(getFileListData(position, FileTypeEnum.IMAGE)))
                        .withOptionsCompat(optionsCompat)
                        .navigation()
            }
        }
    }

    override fun onItemLongClick(view: View, position: Int, obj: Any?) {

    }

    /**获取列表数据内某一类型的所有数据*/
    private fun getFileListData(position: Int, typeEnum: FileTypeEnum): FileListData {
        val imageList = arrayListOf<FileData>()
        data.forEach {
            if (typeEnum == it.typeEnum) {
                if (TextUtils.isEmpty(it.path)) {
                    imageList.add(FileData(it.filename, it.url))
                } else {
                    imageList.add(FileData(it.filename, it.path))
                }
            }
        }
        return FileListData(position, imageList)
    }
}