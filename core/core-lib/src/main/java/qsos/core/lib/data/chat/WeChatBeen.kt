package qsos.core.lib.data.chat

import com.google.gson.annotations.SerializedName

/**
 * @author : 华清松
 * Tweet实体
 */
data class WeChatTweetBeen(
        /**正文*/
        var content: String? = "",
        /**发送对象*/
        var sender: WeChatSenderBean?,
        /**错误信息*/
        var error: String? = "",
        /**未知错误信息*/
        @SerializedName("unknown error") var unknownError: String?
) {

    /**图片列表*/
    var images: ArrayList<WeChatImageBean>? = null
        get() {
            return if (field == null) arrayListOf() else field
        }

    /**评论列表*/
    var comments: ArrayList<WeChatCommentBean>? = null
        get() {
            return if (field == null) arrayListOf() else field
        }

}

/**
 * @author : 华清松
 * 发送人实体
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
 * 图片实体
 */
class WeChatImageBean(
        /**图片链接*/
        var url: String? = ""
)

/**
 * @author : 华清松
 * 评论实体
 */
data class WeChatCommentBean(
        /**评论内容*/
        var content: String? = "",
        /**评论人对象*/
        var sender: WeChatSenderBean?
)

/**
 * @author : 华清松
 * 用户实体
 */
data class WeChatUserBeen(
        @SerializedName("profile-image") var profileImage: String? = "",
        var avatar: String? = "",
        var nick: String? = "",
        var username: String? = ""
)
