package qsos.lib.base.data.http

/**
 * @author : 华清松
 * @description : 网络请求状态实体
 */
data class DataStateEntity(var httpCode: IDataStateCode, var httpMsg: String = "")

interface IDataStateCode {
    val stateCode: Int
    val stateMsg: String
}

enum class DataState(val code: Int, val msg: String) : IDataStateCode {
    ERROR(-1, "加载失败") {
        override val stateCode = code
        override val stateMsg = msg
    },
    LOADING(0, "加载中") {
        override val stateCode = code
        override val stateMsg = msg
    },
    SUCCESS(1, "加载成功") {
        override val stateCode = code
        override val stateMsg = msg
    }
}

fun main() {
    println(DataState.ERROR.stateCode)
    println(DataState.ERROR.stateMsg)
    println(DataState.LOADING.stateCode)
    println(DataState.LOADING.stateMsg)
    println(DataState.SUCCESS.stateCode)
    println(DataState.SUCCESS.stateMsg)
}