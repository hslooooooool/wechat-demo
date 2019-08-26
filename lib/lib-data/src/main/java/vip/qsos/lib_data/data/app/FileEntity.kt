package vip.qsos.lib_data.data.app

/**
 * @author : 华清松
 * @description : 文件实体类
 */
open class FileEntity {
    companion object {
        /**根据文件后缀获取文件类型*/
        fun getFileType(extension: String?): FileTypeEnum {
            return when (extension?.toLowerCase()) {
                "png", "jpg", "jpeg", "gif" -> FileTypeEnum.IMAGE
                "mp3", "wav", "raw", "amr" -> FileTypeEnum.AUDIO
                "mp4", "flv", "avi", "3gp", "webm", "ts", "ogv", "m3u8", "asf", "wmv", "rm", "rmvb", "mov", "mkv" -> FileTypeEnum.VIDEO
                "doc", "docx", "pdf", "txt", "ppt", "pptx", "xls", "xlsx" -> FileTypeEnum.WORD
                else -> FileTypeEnum.OTHER
            }
        }
    }

    /**文件名*/
    var filename: String? = ""
    /**文件链接*/
    var url: String? = null
    /**文件本地路径*/
    var path: String? = null
    /**文件后缀*/
    var type: String? = "jpg"
    /**文件类型*/
    val typeEnum: FileTypeEnum?
        get() = getFileType(type)

}
