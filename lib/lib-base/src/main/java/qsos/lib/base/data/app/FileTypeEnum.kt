package qsos.lib.base.data.app

/**
 * @author : 华清松
 * @description : 文件类型枚举
 */
enum class FileTypeEnum(val type: String) {
    PHOTO("照片"),
    IMAGE("图片"),
    VIDEO("视频"),
    AUDIO("音频"),
    WORD("文档"),
    OTHER("其它")
}