package com.yuran.androiddemo.storage

import com.blankj.utilcode.util.SPUtils

/**Created by limengcheng on 2/1/21 5:14 PM.
 * 本地数据操作(数据库、文件(比如缓存)、SP等等)、网络数据操作；考虑应该封装一个本地仓库、一个网络仓库，本地仓库应该还分SP仓库、数据库仓库、缓存仓库
 * 网络数据操作可以加入添加到缓存的功能，本地数据操作可以封装在IO协程中操作的功能
 * */
object Repository {
    var authorization: String
        get() = SPUtils.getInstance().getString(SP.KEY_AUTHORIZATION)
        set(value) = SPUtils.getInstance().put(SP.KEY_AUTHORIZATION, value)
}