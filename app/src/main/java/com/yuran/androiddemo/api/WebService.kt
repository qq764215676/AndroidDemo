package com.yuran.androiddemo.api

import com.yuran.androiddemo.api.bean.BaseBean
import com.yuran.androiddemo.api.bean.LoginBean
import com.yuran.androiddemo.api.bean.UserInfoBean
import retrofit2.http.*

/**Created by limengcheng on 2/1/21 11:08 AM.*/
interface WebService {

    /**
     * 账号密码登录
     */
    @POST("api/login")
    @FormUrlEncoded
    suspend fun api_login(@Field("account") account: String,
                          @Field("password") password: String): BaseBean<LoginBean>

    /**
     * 当前登录用户信息
     */
    @GET("api/userInfo")
    suspend fun api_userInfo(): BaseBean<UserInfoBean>

}