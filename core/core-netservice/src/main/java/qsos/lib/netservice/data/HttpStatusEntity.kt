package qsos.lib.netservice.data

/**
 * @author : 华清松
 * 自行定义与拓展网络请求状态对象
 */
data class BaseDataStatus(override val stateCode: Int, override val stateMsg: String) : IDataStatusCode {
    companion object {
        fun base(status: DataStatusEnum): BaseDataStatus {
            return BaseDataStatus(status.code, status.msg)
        }
    }
}

/**
 * @author : 华清松
 * 网络请求状态参数
 */
interface IDataStatusCode {
    /**请求状态码*/
    val stateCode: Int
    /**请求回执信息*/
    val stateMsg: String
}

/**
 * @author : 华清松
 * 自行定义与拓展网络请求状态枚举，根据定义的回执码，判断网络请求结果
 */
enum class DataStatusEnum(val code: Int, val msg: String) : IDataStatusCode {
    NO_NET(-2, "没有网络") {
        override val stateCode = code
        override val stateMsg = msg
    },
    ERROR(-1, "请求失败") {
        override val stateCode = code
        override val stateMsg = msg
    },
    LOADING(0, "加载中") {
        override val stateCode = code
        override val stateMsg = msg
    },
    FINISH(1, "请求完成") {
        override val stateCode = code
        override val stateMsg = msg
    },
    SUCCESS(200, "请求成功") {
        override val stateCode = code
        override val stateMsg = msg
    }
}
