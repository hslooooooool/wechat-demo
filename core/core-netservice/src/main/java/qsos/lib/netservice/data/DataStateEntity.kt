package qsos.lib.netservice.data

/**
 * @author : 华清松
 * 网络请求状态实体
 */
data class DataStateEntity(var httpCode: IDataStateCode, var httpMsg: String = httpCode.stateMsg)

interface IDataStateCode {
    val stateCode: Int
    val stateMsg: String
}

/**
 * @author : 华清松
 * 自行定义与拓展网络请求状态枚举
 */
enum class DataState(val code: Int, val msg: String) : IDataStateCode {
    NO_NET(-2, "没有网络") {
        override val stateCode = code
        override val stateMsg = msg
    },
    ERROR(-1, "加载失败") {
        override val stateCode = code
        override val stateMsg = msg
    },
    LOADING(0, "加载中") {
        override val stateCode = code
        override val stateMsg = msg
    },
    SUCCESS(200, "加载成功") {
        override val stateCode = code
        override val stateMsg = msg
    }
}

/**
 * @author : 华清松
 * 自行定义与拓展网络请求状态对象
 */
data class BaseDataState(override val stateCode: Int, override val stateMsg: String) : IDataStateCode