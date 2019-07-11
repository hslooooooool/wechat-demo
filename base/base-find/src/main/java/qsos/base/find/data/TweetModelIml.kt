package qsos.base.find.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import qsos.lib.base.data.WeChatTweetBeen
import qsos.lib.base.data.WeChatUserBeen

/**
 * @author : 华清松
 * @description : 朋友圈数据Model
 */
class TweetModelIml : ITweetModel, ViewModel() {
    val mTweetRepository = TweetRepository()
    private var tweetList: MutableLiveData<List<WeChatTweetBeen>>? = null
    private var userInfo: MutableLiveData<WeChatUserBeen>? = null

    override fun dataTweetList(): LiveData<List<WeChatTweetBeen>> {
        if (tweetList == null) {
            tweetList = mTweetRepository.dataWeChatTweetList
            mTweetRepository.getTweetList()
        }
        return tweetList!!
    }

    override fun dataUserInfo(): LiveData<WeChatUserBeen> {
        if (userInfo == null) {
            userInfo = mTweetRepository.dataWeChatUserInfo
            mTweetRepository.getUserInfo()
        }
        return userInfo!!
    }

}