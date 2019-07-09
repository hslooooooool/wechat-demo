package qsos.lib.base.data

import com.google.gson.annotations.SerializedName

/**
 * @author : 华清松
 * @description : 朋友圈列表项数据实体
 */
data class WeChatBeen(
        var dataType: WeChatType,
        var data: Any
)

/**
 * @author : 华清松
 * @description : 朋友圈列表项类型枚举
 */
enum class WeChatType(val key: String) {
    ITEM("子项"), HEAD("顶部")
}

/**
 * @author : 华清松
 * @description : Tweet实体
 */
data class WeChatTweetBeen(
        /**正文*/
        var content: String? = "",
        /**发送对象*/
        var sender: WeChatSenderBean?,
        /**错误信息*/
        var error: String? = "",
        /**图片列表*/
        var images: ArrayList<WeChatImageBean>?,
        /**评论列表*/
        var comments: ArrayList<WeChatCommentBean>?,
        /**未知错误信息*/
        @SerializedName("unknown error") var unknownError: String?
)

/**
 * @author : 华清松
 * @description : 发送人实体
 */
data class WeChatSenderBean(
        /**用户名称*/
        var username: String? = "",
        /**别名*/
        var nick: String? = "匿名",
        /**头像链接*/
        var avatar: String? = ""
)

/**
 * @author : 华清松
 * @description : 图片实体
 */
class WeChatImageBean(
        /**图片链接*/
        var url: String? = ""
)

/**
 * @author : 华清松
 * @description : 评论实体
 */
data class WeChatCommentBean(
        /**评论内容*/
        var content: String? = "",
        /**评论人对象*/
        var sender: WeChatSenderBean?
)

/**
 * @author : 华清松
 * @description : 用户实体
 */
data class WeChatUserBeen(
        @SerializedName("profile-image") var profileImage: String? = "",
        var avatar: String? = "",
        var nick: String? = "",
        var username: String? = ""
)
