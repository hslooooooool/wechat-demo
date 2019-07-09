package qsos.lib.base.data.mine

/**
 * @author : 华清松
 * @description : 我的界面功能列表项实体
 */
data class MineItemEntity(
        var id: Int,
        var name: String,
        var icon: String?,
        var type: Int,
        var num: Int?
)