package qsos.base.find.data

import kotlinx.coroutines.CoroutineScope
import qsos.lib.netservice.ApiEngine
import qsos.lib.netservice.data.BaseHttpLiveData
import qsos.lib.netservice.expand.retrofitByDef
import qsos.lib.netservice.expand.retrofitWithLiveDataByDef
import vip.qsos.exception.GlobalException
import vip.qsos.exception.GlobalExceptionHelper
import kotlin.coroutines.CoroutineContext

/**
 * @author : 华清松
 * 聊天数据获取
 * TweetRepository 内部中 MutableLiveData 的对象必须为 val 不可变对象，防止外部篡改，外部仅观察数据。
 */
class TweetRepository(
        private val mCoroutineContext: CoroutineContext
) : ITweetRepo {

    val mDataTweetList: BaseHttpLiveData<List<EmployeeBeen>> = BaseHttpLiveData()

    val mDataUserInfo: BaseHttpLiveData<EmployeeBeen> = BaseHttpLiveData()

    override fun addOne(success: () -> Unit, fail: (msg: String) -> Unit) {
        CoroutineScope(mCoroutineContext).retrofitByDef<EmployeeBeen> {
            api = ApiEngine.createService(ApiTweet::class.java).one()
            onSuccess {
                success()
            }
            onFailed { code, error ->
                GlobalExceptionHelper.caughtException(GlobalException.ServerException(code, error)) {
                    fail("提交失败，请重试")
                }
            }
        }
    }

    override fun getOne() {
        CoroutineScope(mCoroutineContext).retrofitWithLiveDataByDef<EmployeeBeen> {
            api = ApiEngine.createService(ApiTweet::class.java).one()
            data = mDataUserInfo
        }
    }

    override fun getList() {
        CoroutineScope(mCoroutineContext).retrofitWithLiveDataByDef<List<EmployeeBeen>> {
            api = ApiEngine.createService(ApiTweet::class.java).list()
            data = mDataTweetList
        }
    }

}