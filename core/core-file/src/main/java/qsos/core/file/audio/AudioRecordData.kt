package qsos.core.file.audio

/**
 * @author : 华清松
 * @description : 录音数据，录制中实时更新
 */
data class AudioRecordData(
        /**已录制时长，秒*/
        var recordTime: Int = -1,
        /**录制状态*/
        var recordState: AudioRecordState,
        /**录音存储位置*/
        var audioPath: String
)

/**
 * @author : 华清松
 * @description : 录音状态
 */
enum class AudioRecordState(private val value: String) {
    PREPARE("准备中..."),
    RECORDING("正在录制，请说话"),

    CANCEL_WANT("意向取消"),
    CANCEL_REFUSE("取消意向"),

    CANCEL("录音取消"),

    /**失败只有两种情况，主动停止完成录制，被动停止发生错误*/
    STOP("停止录音"),
    FAIL("录音失败"),

    SUCCESS("完成录音")
}

/**
 * @author : 华清松
 * @description : 录音初始化配置
 */
data class AudioRecordConfig(
        var limitMinTime: Int = 1,
        var limitMaxTime: Int = 60,
        var audioFormat: AudioFormat = AudioFormat.AMR
) {
    open class Builder {
        private var limitMinTime: Int = 1
        private var limitMaxTime: Int = 60
        private var audioFormat: AudioFormat = AudioFormat.AMR

        open fun setLimitMinTime(minTime: Int): Builder {
            this.limitMinTime = minTime
            return this
        }

        open fun setLimitMaxTime(maxTime: Int): Builder {
            this.limitMaxTime = maxTime
            return this
        }

        open fun setAudioFormat(format: AudioFormat): Builder {
            this.audioFormat = format
            return this
        }

        open fun build(): AudioRecordConfig {
            return AudioRecordConfig(limitMinTime, limitMaxTime, audioFormat)
        }
    }
}

/**
 * @author : 华清松
 * @description : 录制格式，支持 amr wav 格式，默认 amr 格式,，禁止在此接口中去响应录音状态
 */
enum class AudioFormat {
    AMR, WAV
}

/**
 * @author : 华清松
 * @description : 录音控制接口
 */
interface IControlView {
    /**开始录音,NOTICE 请开启权限*/
    fun start()

    /**意向取消*/
    fun cancelWant()

    /**放弃意向*/
    fun cancelRefuse()

    /**停止录音*/
    fun stop()
}

/**
 * @author : 华清松
 * @description : 响应录音状态交互接口，禁止在此接口中去控制录音操作
 */
interface IRespondView {
    /**录音准备中*/
    fun vPrepare()

    /**录音时长需更新*/
    fun vRecording(recordTime: Int)

    /**意向取消*/
    fun vCancelWant()

    /**取消意向*/
    fun vCancelRefuse()

    /**录音已取消*/
    fun vCancel()

    /**录音已失败*/
    fun vFail(msg: String)

    /**录音已停止*/
    fun vStop()
}