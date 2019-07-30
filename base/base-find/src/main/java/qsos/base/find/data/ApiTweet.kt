package qsos.base.find.data

import io.reactivex.Observable
import qsos.lib.base.data.WeChatTweetBeen
import qsos.lib.base.data.WeChatUserBeen
import retrofit2.Call
import retrofit2.http.GET

/**
 * @author : 华清松
 * @description : 朋友圈接口
 */
interface ApiTweet {

    /**获取用户信息*/
    @GET("/user/jsmith")
    fun getUserInfo(): Observable<WeChatUserBeen>

    /**获取用户信息*/
    @GET("/user/jsmith")
    fun getUser(): Call<WeChatUserBeen>

    /**获取推特列表*/
    @GET("/user/jsmith/tweets")
    fun getTweetList(): Observable<List<WeChatTweetBeen>>

    /**获取推特列表*/
    @GET("/user/jsmith/tweets")
    fun getTweet(): Call<List<WeChatTweetBeen>>

}