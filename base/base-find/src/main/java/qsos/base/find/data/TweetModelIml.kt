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
    private var tweetList: MutableLiveData<List<WeChatTweetBeen>>? = null
    private var userInfo: MutableLiveData<WeChatUserBeen>? = null

    override fun dataTweetList(): LiveData<List<WeChatTweetBeen>> {
        if (tweetList == null) {
            tweetList = TweetRepository.dataWeChatTweetList
            TweetRepository.getTweetList()
        }
        return tweetList!!
    }

    override fun dataUserInfo(): LiveData<WeChatUserBeen> {
        if (userInfo == null) {
            userInfo = TweetRepository.dataWeChatUserInfo
            TweetRepository.getUserInfo()
        }
        return userInfo!!
    }

}