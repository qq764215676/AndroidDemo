package com.yuran.androiddemo.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.gudsen.library.activity.BaseActivity
import com.gudsen.library.extension.info
import com.gudsen.library.util.ToastPlus
import com.yuran.androiddemo.R
import com.yuran.androiddemo.api.RetrofitUtils
import com.yuran.androiddemo.api.bean.UserInfoBean
import com.yuran.androiddemo.application.MainApplication
import kotlinx.android.synthetic.main.person_info_activity.*
import kotlinx.coroutines.launch

/**Created by limengcheng on 2/1/21 10:58 AM.*/
class PersonInfoActivity : BaseActivity(R.layout.person_info_activity) {
    val viewModel: PersonInfoActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getUserInfo().observe(this, Observer {
            Glide.with(this).load(it.avatar).apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(iv_avatar)
            tv_nickName.text = it.nickname
        })
        viewModel.setUserInfo()

        wcv.start()
        cpv.progress = 66

        btn_num1.setOnClickListener {
            cpv.progress = 1
        }

        btn_num99.setOnClickListener {
            cpv.progress = 99
        }
    }

    override fun onDestroy() {
        wcv.stopImmediately()

        super.onDestroy()
    }
}

class PersonInfoActivityViewModel : ViewModel() {
    /*
    调用ViewModel的设置数据的方法，然后把ViewModel中的数据传给UI控件
    如何把UI控件的数据保存到ViewModel？
     */
    private val userInfo by lazy { MutableLiveData<UserInfoBean>() }

    fun getUserInfo(): LiveData<UserInfoBean> = userInfo

    fun setUserInfo() {
        viewModelScope.launch {
            try {
                userInfo.value = RetrofitUtils.webService.api_userInfo().data
            } catch (e: Exception) {
                ToastPlus.globalShowLongCatchNPE(e.info)// TODO: 2/1/21 有覆盖拦截器里显示的问题，解决一下
            }
        }
    }
}