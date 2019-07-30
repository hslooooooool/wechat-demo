package qsos.base.find.data

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import qsos.lib.base.data.HttpLiveData
import qsos.lib.base.data.WeChatTweetBeen
import qsos.lib.base.data.WeChatUserBeen
import qsos.lib.base.data.http.BaseDataState
import qsos.lib.base.data.http.DataState
import qsos.lib.base.data.http.DataStateEntity
import qsos.lib.netservice.ApiEngine
import qsos.lib.netservice.expand.retrofit
import kotlin.coroutines.CoroutineContext

/**
 * @author : 华清松
 * @description : 推特数据 Model
 * 注意 TweetModelIml(val mTweetRepository: TweetRepository = TweetRepository()) 的写法。
 * 为什么这样写？为什么不直接写在 TweetModelIml 类内部？
 * 因为在不同的活动中（Activity/Fragment），如果需要通过相同的方法获取不一样的数据，又不改变当前的方法构造，
 * 则可以通过 TweetRepository() 传递判断参数，当然，我们应该限制参数是影响 TweetRepository() 内所有请求结果的。
 * 比如不同的用户角色进入某活动，活动内所有数据都是根据当前用户角色进行获取，且你不需要缓存当前用户角色信息。
 */
class TweetModelIml(
        private val mCoroutineContext: CoroutineContext,
        val mTweetRepository: TweetRepository = TweetRepository()
) : ITweetModel, ViewModel() {

    private lateinit var mDataTweetList: HttpLiveData<List<WeChatTweetBeen>>
    private lateinit var mDataUserInfo: HttpLiveData<WeChatUserBeen>

    override fun dataTweetList(): HttpLiveData<List<WeChatTweetBeen>> {
        if (!::mDataTweetList.isInitialized) {
            mDataTweetList = HttpLiveData()
            getTweet()
        }
        return mDataTweetList
    }

    override fun dataUserInfo(): HttpLiveData<WeChatUserBeen> {
        if (!::mDataUserInfo.isInitialized) {
            mDataUserInfo = HttpLiveData()
            getUser()
        }
        return mDataUserInfo
    }

    fun getTweet() {
        CoroutineScope(mCoroutineContext).retrofit<List<WeChatTweetBeen>> {
            api = ApiEngine.createService(ApiTweet::class.java).getTweet()

            onStart {
                mDataTweetList.httpState.postValue(DataStateEntity(DataState.LOADING))
            }
            onSuccess {
                mDataTweetList.httpState.postValue(DataStateEntity(DataState.SUCCESS))
                mDataTweetList.postValue(it)
            }
            onFailed { code, msg ->
                mDataTweetList.httpState.postValue(DataStateEntity(BaseDataState(code, msg)))
            }
        }
    }

    fun getUser() {
        CoroutineScope(mCoroutineContext).retrofit<WeChatUserBeen> {
            api = ApiEngine.createService(ApiTweet::class.java).getUser()

            onStart {
                mDataUserInfo.httpState.postValue(DataStateEntity(DataState.LOADING))
            }
            onSuccess {
                mDataUserInfo.httpState.postValue(DataStateEntity(DataState.SUCCESS))
                mDataUserInfo.postValue(it)
            }
            onFailed { code, msg ->
                mDataUserInfo.httpState.postValue(DataStateEntity(BaseDataState(code, msg)))
            }
        }
    }
}