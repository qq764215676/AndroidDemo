package com.yuran.androiddemo.activity

import android.app.ProgressDialog
import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.ToastUtils
import com.gudsen.library.extension.dataBinding
import com.gudsen.library.extension.info
import com.gudsen.library.extension.setTextNoSame
import com.gudsen.library.util.ToastPlus
import com.yuran.androiddemo.R
import com.yuran.androiddemo.api.Constants
import com.yuran.androiddemo.api.RetrofitUtils
import com.yuran.androiddemo.biz.Utils
import com.yuran.androiddemo.databinding.LoginActivityBinding
import com.yuran.androiddemo.storage.Repository
import kotlinx.android.synthetic.main.login_activity.*
import kotlinx.coroutines.launch

// TODO: 2/1/21 EditText怎么配合ViewModel使用；优化SP的用法
class LoginActivity : BaseActivity() {
    val viewModel: LoginActivityViewModel by viewModels()

    //    val viewDataBinding: LoginActivityBinding by lazy { DataBindingUtil.setContentView(this, R.layout.login_activity) }
//    val viewDataBinding: LoginActivityBinding by dataBinding(R.layout.login_activity)
//    val viewDataBinding by lazy { LoginActivityBinding.inflate(layoutInflater) }
    val viewDataBinding: LoginActivityBinding by dataBinding()//这种最简洁。不依托于基类

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewDataBinding.btnLogin.setBackgroundColor(Color.RED)
        viewDataBinding.vm = viewModel

        //这行代码要加上，因为可能有xml中赋值的情况。这个时候需要初始化给ViewModel
        //在observer之前调用。考虑到旋转屏幕Activity销毁重建，observer刚把数据赋给UI，又被这里的代码重置了(这里的代码应该只在第一次进入Activity时设置给ViewModel)。虽然没测试过
//        viewModel.setAccount(et_account.text.toString())
        viewModel.setPassword(et_password.text.toString())

        et_account.doOnTextChanged { text, start, before, count ->
            viewModel.setAccount(text.toString())
            /*if (text != viewModel.getAccount().value) {
                viewModel.setAccount(text.toString())
            }*/
        }
        viewModel.getAccount().observe(this, Observer {
            if (it != et_account.text.toString()) {
                et_account.setText(it)
            }
            et_account.setTextNoSame(it)
        })

        /*et_password.doOnTextChanged { text, start, before, count -> viewModel.setPassword(text.toString()) }
        viewModel.getPassword().observe(this, Observer { et_password.setTextNoSame(it) })*/

        btn_login.setOnClickListener {
            lifecycleScope.launch {
                /*val progressDialog = ProgressDialog(this@LoginActivity)
                progressDialog.setTitle("提示")
                progressDialog.setMessage("正在登录...")
                progressDialog.show()*/

                val progressDialog = ProgressDialog.show(this@LoginActivity, "提示", "正在登录...")

                try {
                    val bean = RetrofitUtils.webService.api_login(
                        viewModel.getAccount().value!!,
                        viewModel.getPassword().value!!
                    )
                    when (bean.status) {
                        Constants.STATUS_OK -> {
                            /*
                            Toast可以直接在P层调用，不需让Activity实现，因为Toast不依赖于Activity的引用(Context)，所以直接使用Application的Context就可以。如果依赖于Activity，那就必须让每个Activity都实现一个Toast
                             */
                            ToastPlus.globalShowLongCatchNPE("登录成功")
//                            MainApplication.sp.authorization = Utils.tokenToAuthorization(bean.data?.token!!)
                            Repository.authorization =
                                Utils.tokenToAuthorization(bean.data?.token!!)
                            startActivity(PersonInfoActivity::class.java)
                        }
                        Constants.STATUS_ERROR -> {
                            ToastPlus.globalShowLongCatchNPE("账号或密码错误")
                        }
                    }
                } catch (e: Exception) {//还可能捕获到retrofit请求这行代码以外的其他代码执行报的异常
//                    ToastPlus.globalShowLongCatchNPE(e.info)
                    e.printStackTrace()
                    ToastUtils.showLong(e.info)
                }

                progressDialog.dismiss()
            }
        }
    }
}

class LoginActivityViewModel() : ViewModel() {
    //    private val account by lazy { MutableLiveData<String>().apply { value = "13545147094" } }
    val account by lazy { MutableLiveData<String>().apply { value = "13545147094" } }
    fun getAccount(): LiveData<String> = account
    fun setAccount(account: String) {
        this.account.value = account
    }

    //    val _account: LiveData<String> get() = account
//    val _account: MutableLiveData<String> get() = account
//    fun _getAccount(): LiveData<String> = account

    private val password by lazy { MutableLiveData<String>() }
    fun getPassword(): LiveData<String> = password
    fun setPassword(password: String) {
        this.password.value = password
    }
}