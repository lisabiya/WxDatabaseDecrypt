package decrypt.wx.com.wxdatabasedecryptkey.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import decrypt.wx.com.wxdatabasedecryptkey.R
import decrypt.wx.com.wxdatabasedecryptkey.bean.Message

/**
 * Create by wakfu on 2020/7/9
 */
class ListAdapter(list: MutableList<Message?>?) : BaseQuickAdapter<Message?, BaseViewHolder>(R.layout.adapter_list, list) {

    override fun convert(holder: BaseViewHolder, item: Message?) {
        item?.run {
            holder.setText(R.id.tvNickName, nickName)
            holder.setText(R.id.tvContent, content)
        }
    }
}