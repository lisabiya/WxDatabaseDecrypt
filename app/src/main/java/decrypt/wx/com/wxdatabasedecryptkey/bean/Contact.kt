package decrypt.wx.com.wxdatabasedecryptkey.bean

/**
 * Create by wakfu on 2020/7/9
 *
 */

fun main(args: Array<String>) {

}

open class BaseBean(var msgType: String = "base", var message: String = "") {
}

class Contact(var userName: String, var nickName: String) : BaseBean("contact") {

    override fun toString(): String {
        return "Contact(userName='$userName', nickName='$nickName')"
    }


}

class ChatRoom() : BaseBean("chatRoom") {
    var userName: String = ""

    constructor(type: String, userName: String, nickName: String) : this()

    override fun toString(): String {
        return "ChatRoom(userName='$userName')"
    }

}

class Message(var bizChatId: String,
              var content: String,
              var createTime: String,
              var flag: String,
              var isSend: String,
              var msgId: String,
              var msgSeq: String,
              var msgSvrId: String,
              var status: String,
              var talker: String,
              var nickName: String,
              var talkerId: String,
              var type: String) : BaseBean("message") {

    override fun toString(): String {
        return "Message(bizChatId='$bizChatId', content='$content', createTime='$createTime', flag='$flag', isSend='$isSend', msgId='$msgId', msgSeq='$msgSeq', msgSvrId='$msgSvrId', status='$status', talker='$talker', talkerId='$talkerId', type='$type')"
    }
}

