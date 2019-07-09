package qsos.base.find.data

import android.annotation.SuppressLint
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import qsos.lib.base.data.WeChatTweetBeen
import qsos.lib.base.data.WeChatUserBeen
import qsos.lib.netservice.ApiEngine
import qsos.lib.netservice.ObservableService
import qsos.lib.netservice.file.BaseRepository

/**
 * @author : 华清松
 * @description : 聊天数据获取
 */
@SuppressLint("CheckResult")
object TweetRepository : ITweetRepo, BaseRepository() {
    /**推特列表数据*/
    val dataWeChatTweetList = MutableLiveData<List<WeChatTweetBeen>>()
    /**用户数据*/
    val dataWeChatUserInfo = MutableLiveData<WeChatUserBeen>()

    override fun getUserInfo() {
        ObservableService.setObservable(
                ApiEngine.createService(ApiTweet::class.java).getUserInfo()
        ).subscribe(
                {
                    dataWeChatUserInfo.postValue(it)
                },
                {
                    dataWeChatUserInfo.postValue(WeChatUserBeen())
                }
        )
    }

    override fun getTweetList() {
        ObservableService.setObservable(
                ApiEngine.createService(ApiTweet::class.java).getTweetList()
        ).subscribe(
                {
                    // 数据校验
                    val data = arrayListOf<WeChatTweetBeen>()
                    it.forEach { tweet ->
                        if (!tweet.images.isNullOrEmpty() && !TextUtils.isEmpty(tweet.content)) {
                            data.add(tweet)
                        }
                    }
                    dataWeChatTweetList.postValue(data)
                },
                {
                    dataWeChatTweetList.postValue(arrayListOf())
                }
        )
    }

}