package qsos.lib.base.utils.record

/**
* @author : 华清松
* @description : 录制状态
*/
enum class RecordStateEnum {
    /**未开始*/
    NORMAL,
    /**按下屏幕,已开始*/
    START,
    /**录制中*/
    DOING,
    /**录制中,意向取消*/
    CANCEL_WANT,
    /**录制中,不取消*/
    CANCEL_NO,
    /**录制停止,已取消*/
    CANCEL_YES,
    /**录制停止,获取录制的文件,为后续使用*/
    STOP,
    /**录制停止,录制失败*/
    FAIL,
    /**录制停止,可发送*/
    SUCCESS,
}