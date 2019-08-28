package qsos.base.find.data

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import qsos.lib.netservice.data.HttpLiveData
import vip.qsos.lib_data.data._do.chat.WeChatTweetBeen
import vip.qsos.lib_data.data._do.chat.WeChatUserBeen

/**
 * @author : 华清松
 * 推特数据 Model
 * 注意 TweetModelIml(val mTweetRepository: TweetRepository = TweetRepository()) 的写法。
 * 为什么这样写？为什么不直接写在 TweetModelIml 类内部？
 * 因为在不同的活动中（Activity/Fragment），如果需要通过相同的方法获取不一样的数据，又不改变当前的方法构造，
 * 则可以通过 TweetRepository() 传递判断参数，当然，我们应该限制参数是影响 TweetRepository() 内所有请求结果的。
 * 比如不同的用户角色进入某活动，活动内所有数据都是根据当前用户角色进行获取，且你不需要缓存当前用户角色信息。
 */
class TweetModelIml : ITweetModel, ViewModel() {

    /**创建协程Context，绑定生命周期*/
    private val mJob = Dispatchers.Main + Job()
    private val mTweetRepository: TweetRepository = TweetRepository(mJob)

    override fun mTweetList(): HttpLiveData<List<WeChatTweetBeen>> {
        return this.mTweetRepository.mDataTweetList
    }

    override fun mUserInfo(): HttpLiveData<WeChatUserBeen> {
        return this.mTweetRepository.mDataUserInfo
    }

    override fun getUserInfo() {
        mTweetRepository.getUserInfo()
    }

    override fun getTweetList() {
        mTweetRepository.getTweetList()
    }

    override fun postForm(success: () -> Unit) {
        mTweetRepository.postForm(success)
    }

    override fun onCleared() {
        mJob.cancel()
        super.onCleared()
    }
}