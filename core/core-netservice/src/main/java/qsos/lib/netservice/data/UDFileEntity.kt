package qsos.lib.netservice.data

/**
 * @author : 华清松
 * 上传下载文件进度
 */
data class UDFileEntity(
        /**网络路径*/
        var url: String?,
        /**保存路径*/
        var path: String?,
        /**文件名称*/
        var filename: String?,
        /**百分比进度*/
        var progress: Int = 0
) {
    var id: String? = null
    /**伴随携带的数据，用于数据传递使用，比如发送消息中的文件时，文件上传成功返回结果时，可通过value得知是哪条消息的文件上传成功*/
    var adjoint: Any? = null
    /**文件类型*/
    var type: String? = null

    /**上传下载成功*/
    var loadSuccess = false
}