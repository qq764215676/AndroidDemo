package com.yuran.androiddemo.activity

import android.app.ProgressDialog
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.gudsen.library.extension.info
import com.gudsen.library.extension.setTextNoSame
import com.gudsen.library.util.ToastPlus
import com.yuran.androiddemo.R
import com.yuran.androiddemo.api.Constants
import com.yuran.androiddemo.api.RetrofitUtils
import com.yuran.androiddemo.api.bean.UserInfoBean
import com.yuran.androiddemo.application.MainApplication
import com.yuran.androiddemo.biz.Utils
import com.yuran.androiddemo.storage.SP
import kotlinx.android.synthetic.main.login_activity.*
import kotlinx.coroutines.launch

interface ILoginActivityView {
    fun showProgressDialog(title: String, msg: String)
    fun hideProgressDialog()
}

interface ILoginActivityPresenter {
    fun login()
}

// TODO: 2/1/21 EditText怎么配合ViewModel使用；优化SP的用法
class LoginActivity : BaseActivity(R.layout.login_activity) {
    val viewModel: LoginActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        et_password.doOnTextChanged { text, start, before, count -> viewModel.setPassword(text.toString()) }
        viewModel.getPassword().observe(this, Observer { et_password.setTextNoSame(it) })

        //这行代码要加上，因为可能有xml中赋值的情况。这个时候需要初始化给ViewModel
        viewModel.setAccount(et_account.text.toString())
        viewModel.setPassword(et_password.text.toString())

        btn_login.setOnClickListener {
            lifecycleScope.launch {
                val progressDialog = ProgressDialog(this@LoginActivity)
                progressDialog.setTitle("提示")
                progressDialog.setMessage("正在登录...")
                progressDialog.show()

                try {
                    val bean = RetrofitUtils.webService.api_login(viewModel.getAccount().value!!, viewModel.getPassword().value!!)
                    when (bean.status) {
                        Constants.STATUS_OK -> {
                            ToastPlus.globalShowLongCatchNPE("登录成功")
//                            MainApplication.sp.authorization = Utils.tokenToAuthorization(bean.data?.token!!)
                            SP.authorization = Utils.tokenToAuthorization(bean.data?.token!!)
                            startActivity(PersonInfoActivity::class.java)
                        }
                        Constants.STATUS_ERROR -> {
                            ToastPlus.globalShowLongCatchNPE("账号或密码错误")
                        }
                    }
                } catch (e: Exception) {//还可能捕获到retrofit请求这行代码以外的其他代码执行报的异常
                    ToastPlus.globalShowLongCatchNPE(e.info)
                }

                progressDialog.dismiss()
            }
        }
    }
}

class LoginActivityViewModel() : ViewModel() {
    private val account by lazy { MutableLiveData<String>() }
    fun getAccount(): LiveData<String> = account
    fun setAccount(account: String) {
        this.account.value = account
    }

    private val password by lazy { MutableLiveData<String>() }
    fun getPassword(): LiveData<String> = password
    fun setPassword(password: String) {
        this.password.value = password
    }
}