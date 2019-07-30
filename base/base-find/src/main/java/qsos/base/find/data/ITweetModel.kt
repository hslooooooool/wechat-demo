package qsos.base.find.data

import qsos.lib.base.data.HttpLiveData
import qsos.lib.base.data.WeChatTweetBeen
import qsos.lib.base.data.WeChatUserBeen

/**
 * @author : 华清松
 * @description :朋友圈数据
 */
interface ITweetModel {

    /**获取用户信息*/
    fun dataUserInfo(): HttpLiveData<WeChatUserBeen>

    /**获取Tweet列表*/
    fun dataTweetList(): HttpLiveData<List<WeChatTweetBeen>>

}

/**
 * @author : 华清松
 * @description : 朋友圈数据接口
 */
interface ITweetRepo {

    /**获取用户信息*/
    fun getUserInfo()

    /**获取Tweet列表*/
    fun getTweetList()

}