package com.yuran.androiddemo.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.gudsen.library.extension.info
import com.gudsen.library.json.GsonTypeAdapterFactory
import com.gudsen.library.third.okhttp.AddHeadersInterceptor
import com.gudsen.library.third.okhttp.LogInterceptor
import com.gudsen.library.util.ToastPlus
import com.orhanobut.logger.Logger
import com.yuran.androiddemo.api.bean.BaseBean
import com.yuran.androiddemo.storage.Repository
import com.yuran.androiddemo.storage.SP
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**Created by limengcheng on 2/1/21 11:09 AM.*/
object RetrofitUtils {
    val webService: WebService by lazy {
        Retrofit.Builder()
                .baseUrl(Constants.URL)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().registerTypeAdapterFactory(GsonTypeAdapterFactory()).create()))
                .client(OkHttpClient.Builder()
                        .addInterceptor(AddHeadersInterceptor(mapOf(Constants.HEADER_AUTHORIZATION to /*MainApplication.sp.authorization*/ /*SP.authorization*/Repository.authorization)))
                        .addInterceptor(Interceptor {
                            //异常处理拦截器，要在下面的拦截器之前添加？应该不会消耗异常吧。
                            try {
                                val response = it.proceed(it.request())//如果网络请求失败，proceed会抛异常
                                //网络请求成功。开始处理业务
                                val json = response.peekBody((1024 * 1024).toLong()).string()
                                try {
                                    val bean: BaseBean<Any> = Gson().fromJson(json, object : TypeToken<BaseBean<Any>>() {}.type)
                                    when (bean.status) {
                                        Constants.STATUS_TOKEN_FAILURE -> {
//                                            Utils.tokenFailure()
                                        }
                                    }
                                } catch (e: Exception) {//后台返回乱码(比如html代码)时，会走这里
//                                    if ((e is MalformedJsonException) or (e is JsonSyntaxException) or (e is IllegalStateException)) {//还要加，Gson解析json空串时有空指针异常
//                                        val err = "接口返回数据异常"
//                                        Logger.e(err)
//                                        ToastPlus.globalShowLongCatchNPE(err)
//                                    }/* else {//
//                                        //retrofit2.HttpException: HTTP 404 走这里
//                                        //错了，和这个没关系。因为之前走到这里没抛异常的原因，被其他拦截器抛了这个异常
//                                        throw e
//                                    }*/
                                    val err = "接口返回数据异常：${e.info}"
                                    Logger.e(err)
                                    ToastPlus.globalShowLongCatchNPE(err)
                                }
                                return@Interceptor response //解析错误的处理，只在这里打印提示。然后不规范的json继续返回出去，协程请求还能继续捕获到MJE
                            } catch (e: Exception) {
                                //网络请求失败
                                e.printStackTrace()

                                val err = "Retrofit请求失败，返回 ${e.info}"
                                Logger.e(err)
                                ToastPlus.globalShowLongCatchNPE(err)

                                throw e
                            }
                        })
                        .addInterceptor(LogInterceptor())
                        .build())
                .build()
                .create(WebService::class.java)
    }
}