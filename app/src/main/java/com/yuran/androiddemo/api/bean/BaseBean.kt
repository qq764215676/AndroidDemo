package com.yuran.androiddemo.api.bean

/**Created by limengcheng on 2/1/21 11:21 AM.*/
data class BaseBean<T>(var status: Int, var msg: String, var data: T?)
