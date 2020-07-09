package decrypt.wx.com.wxdatabasedecryptkey.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import decrypt.wx.com.wxdatabasedecryptkey.R
import decrypt.wx.com.wxdatabasedecryptkey.bean.Contact

/**
 * Create by wakfu on 2020/7/9
 *
 */
class ContactAdapter() : BaseQuickAdapter<Contact?, BaseViewHolder>(R.layout.adapter_contact_list) {
    constructor(dataList: MutableList<Contact?>) : this() {
        setNewInstance(dataList)
    }

    override fun convert(holder: BaseViewHolder, item: Contact?) {
        item?.run {
            holder.setText(R.id.tvName, userName)
            holder.setText(R.id.tvNickName, nickName)
        }
    }

}