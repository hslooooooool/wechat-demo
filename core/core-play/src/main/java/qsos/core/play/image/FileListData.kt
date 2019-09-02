package qsos.core.play.image

/**
 * @author : 华清松
 * 预览图片列表数据
 */
data class FileListData(
        var position: Int = 0,
        var imageList: ArrayList<FileData>
)

/**
 * @author : 华清松
 * 预览图片数据
 */
data class FileData(
        /**文件名称*/
        var name: String?,
        /**链接，可以为文件路径或网络连接*/
        var url: String?
)