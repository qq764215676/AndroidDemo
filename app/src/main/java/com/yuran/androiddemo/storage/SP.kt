package com.yuran.androiddemo.storage

import android.content.SharedPreferences
import androidx.core.content.edit
import com.blankj.utilcode.util.SPUtils
import com.gudsen.library.storage.MySharedPreferences

/**Created by limengcheng on 2/1/21 4:04 PM.*/
object SP {
    const val KEY_AUTHORIZATION = "KEY_AUTHORIZATION"

    /*var authorization: String?
        get() = sharedPreferences.getString(KEY_AUTHORIZATION, "")
        set(value) = sharedPreferences.edit {
            putString(KEY_AUTHORIZATION, value)
        }*/

    // TODO: 2/5/21 感觉这种不封装也没关系。
    /*
    放到Repository中。因为是操作本地数据。
     */
    /*var authorization: String
        get() = SPUtils.getInstance().getString(KEY_AUTHORIZATION)
        set(value) = SPUtils.getInstance().put(KEY_AUTHORIZATION, value)*/
}