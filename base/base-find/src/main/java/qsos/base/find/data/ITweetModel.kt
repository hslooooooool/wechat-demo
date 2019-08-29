package qsos.base.find.data

import qsos.lib.netservice.data.HttpLiveData
import vip.qsos.lib_data.data._do.chat.WeChatTweetBeen
import vip.qsos.lib_data.data._do.chat.WeChatUserBeen

/**
 * @author : 华清松
 * @description :朋友圈数据
 */
interface ITweetModel : ITweetRepo {

    /**用户信息*/
    fun mUserInfo(): HttpLiveData<WeChatUserBeen>

    /**Tweet列表*/
    fun mTweetList(): HttpLiveData<List<WeChatTweetBeen>>

}

/**
 * @author : 华清松
 * 朋友圈数据接口
 */
interface ITweetRepo {

    /**获取用户信息*/
    fun getUserInfo()

    /**获取Tweet列表*/
    fun getTweetList()

    fun postForm(success: () -> Unit)

}