package qsos.core.play

/**
 * @author : 华清松
 * 通用界面 路由地址
 */
object PlayPath {

    const val GROUP = "PLAY_PATH"
    /*视频预览*/
    const val VIDEO_PREVIEW = "/$GROUP/VIDEO_PREVIEW"
    /*视频连接*/
    const val VIDEO_URL = "VIDEO_URL"
    /*视频名称*/
    const val VIDEO_NAME = "VIDEO_NAME"
    /*视频路径*/
    const val VIDEO_PATH = "VIDEO_PATH"

    /*图片预览-放大缩小模式*/
    const val IMAGE_PREVIEW = "/$GROUP/IMAGE_PREVIEW"
    /*图片预览-画廊模式*/
    const val IMAGE_PREVIEW_GALLERY = "/$GROUP/IMAGE_PREVIEW_GALLERY"
    /*图片列表*/
    const val IMAGE_LIST = "IMAGE_LIST"

}