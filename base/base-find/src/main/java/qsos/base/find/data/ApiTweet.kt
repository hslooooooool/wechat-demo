package qsos.base.find.data

import qsos.core.lib.data.chat.WeChatTweetBeen
import qsos.core.lib.data.chat.WeChatUserBeen
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers

/**
 * @author : 华清松
 * 朋友圈接口
 */
interface ApiTweet {

    /**获取用户信息*/
    @GET("/user/jsmith")
    fun getUser(): Call<WeChatUserBeen>

    /**获取推特列表*/
    @GET("/user/jsmith/tweets")
    fun getTweet(): Call<List<WeChatTweetBeen>>

}