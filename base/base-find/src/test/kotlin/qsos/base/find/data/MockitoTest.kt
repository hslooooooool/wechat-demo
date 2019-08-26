package qsos.base.find.data

import org.junit.Assert.assertNotNull
import org.junit.Test
import org.mockito.Mockito.mock
import vip.qsos.lib_data.data.*

class MockitoTest {

    @Test
    fun testMockWeChatBeen() {
        val mWeChatUserBeen = mock<WeChatUserBeen>(WeChatUserBeen::class.java)
        assertNotNull(mWeChatUserBeen)
        val mWeChatTweetBeen = mock<WeChatTweetBeen>(WeChatTweetBeen::class.java)
        assertNotNull(mWeChatTweetBeen)
        val mWeChatSenderBean = mock<WeChatSenderBean>(WeChatSenderBean::class.java)
        assertNotNull(mWeChatSenderBean)
        val mWeChatImageBean = mock<WeChatImageBean>(WeChatImageBean::class.java)
        assertNotNull(mWeChatImageBean)
        val mWeChatCommentBean = mock<WeChatCommentBean>(WeChatCommentBean::class.java)
        assertNotNull(mWeChatCommentBean)
    }
}
