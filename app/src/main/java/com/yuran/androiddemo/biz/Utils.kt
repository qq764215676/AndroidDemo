package com.yuran.androiddemo.biz

/**Created by limengcheng on 2/1/21 4:18 PM.*/
object Utils {
    fun tokenToAuthorization(token: String) = "Bearer $token"
}