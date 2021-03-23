package com.yuran.androiddemo.application

import android.app.Application
import com.gudsen.library.util.ToastPlus
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger

/**Created by limengcheng on 2/1/21 11:01 AM.*/
class MainApplication : Application() {
    companion object {
        lateinit var instance: MainApplication

//        val sp: AppSharedPreferences by lazy { AppSharedPreferences(instance.defaultSharedPreferences) }
    }

    override fun onCreate() {
        super.onCreate()

        instance = this

        initLogger()
        initToastPlus()
    }

    private fun initLogger() {
        Logger.addLogAdapter(AndroidLogAdapter())
    }

    private fun initToastPlus() {
        ToastPlus.init(this)
    }
}