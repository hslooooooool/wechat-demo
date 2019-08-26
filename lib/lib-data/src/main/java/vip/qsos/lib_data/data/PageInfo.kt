package vip.qsos.lib_data.data

class PageInfo<T> {
    var pageNum: Int = 1
    var pageSize: Int = 20
    var size: Int = 0
    var orderBy: String? = null
    var startRow: Int = 0
    var endRow: Int = 0
    var total: Long = 0
    var pages: Int = 0
    var list: List<T> = arrayListOf()
    var firstPage: Int = 0
    var prePage: Int = 0
    var nextPage: Int = 0
    var lastPage: Int = 0
    var isFirstPage: Boolean = false
    var isLastPage: Boolean = false
    var hasPreviousPage: Boolean = false
    var hasNextPage: Boolean = false
}