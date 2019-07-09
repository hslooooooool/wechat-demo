package qsos.lib.base.data

import java.util.*

open class BaseHttpEntity : BaseEntity() {

    // ("实体编号（唯一标识）")
    var id: String? = null

    // (value = "删除标记:正常")
    var DEL_FLAG_NORMAL: Int = 1

    // (value = "删除标记:删除")
    var DEL_FLAG_DELETE: Int = 0

    // ("创建人")
    var createBy: String? = null

    // ("更新人")
    var updateBy: String? = null

    // ("创建日期")
    var createDate: Date? = null

    // ("更新日期")
    var updateDate: Date? = null

    // ("更新日期 - 查询大于条件")
    var updateDateGT: Date? = null

    // ("更新日期 - 查询小于条件")
    var updateDateLT: Date? = null

    // (value = "状态", hidden = true)
    var status: Int? = null

    // ("备注")
    var remarks: String? = null

    // ("排序字段，当指定值时会通过该字段排序")
    var orderField: String? = null

    // ("排序方式，值只能为ASC和DESC，默认ASC")
    var orderType = "ASC"

}
