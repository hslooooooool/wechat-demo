package qsos.base.find.data

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import qsos.core.lib.data.chat.WeChatTweetBeen
import qsos.core.lib.data.chat.WeChatUserBeen
import qsos.lib.netservice.data.HttpLiveData
import kotlin.coroutines.CoroutineContext

/**
 * @author : 华清松
 * 推特数据 Model
 */
class TweetModelIml : ITweetModel, ViewModel() {

    /**创建协程Context，绑定生命周期*/
    override val mJob: CoroutineContext = Dispatchers.Main + Job()

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