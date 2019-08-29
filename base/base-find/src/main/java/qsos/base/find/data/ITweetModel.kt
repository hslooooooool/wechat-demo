package qsos.base.find.data

import qsos.core.lib.data.chat.WeChatTweetBeen
import qsos.core.lib.data.chat.WeChatUserBeen
import qsos.lib.netservice.IBaseModel
import qsos.lib.netservice.data.HttpLiveData

/**
 * @author : 华清松
 * @description :朋友圈数据
 */
interface ITweetModel : IBaseModel, ITweetRepo {

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