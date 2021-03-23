package com.yuran.androiddemo.activity

import android.app.ProgressDialog
import android.os.Bundle
import androidx.activity.viewModels
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
import com.yuran.androiddemo.biz.Utils
import com.yuran.androiddemo.storage.Repository
import com.yuran.androiddemo.storage.SP
import kotlinx.android.synthetic.main.login_activity.*
import kotlinx.coroutines.launch

/*
View和Presenter两个接口应该用个契约类来放
 */
interface ILoginActivityView {
    fun showProgressDialog(title: String, msg: String)
    fun hideProgressDialog()
}

interface ILoginActivityPresenter {
    fun login()
}

// TODO: 2/1/21 EditText怎么配合ViewModel使用；优化SP的用法
class LoginActivity : BaseActivity(R.layout.login_activity), ILoginActivityView {
    val viewModel: LoginActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //这行代码要加上，因为可能有xml中赋值的情况。这个时候需要初始化给ViewModel
        //在observer之前调用。考虑到旋转屏幕Activity销毁重建，observer刚把数据赋给UI，又被这里的代码重置了(这里的代码应该只在第一次进入Activity时设置给ViewModel)。虽然没测试过
        viewModel.setAccount(et_account.text.toString())
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

        et_password.doOnTextChanged { text, start, before, count -> viewModel.setPassword(text.toString()) }
        viewModel.getPassword().observe(this, Observer { et_password.setTextNoSame(it) })

        btn_login.setOnClickListener {
            lifecycleScope.launch {

                try {
                    val bean = RetrofitUtils.webService.api_login(viewModel.getAccount().value!!, viewModel.getPassword().value!!)
                    when (bean.status) {
                        Constants.STATUS_OK -> {
                            /*
                            Toast可以直接在P层调用，不需让Activity实现，因为Toast不依赖于Activity的引用(Context)，所以直接使用Application的Context就可以。如果依赖于Activity，那就必须让每个Activity都实现一个Toast
                             */
                            ToastPlus.globalShowLongCatchNPE("登录成功")
//                            MainApplication.sp.authorization = Utils.tokenToAuthorization(bean.data?.token!!)
                            Repository.authorization = Utils.tokenToAuthorization(bean.data?.token!!)
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

    /*
    这种方式有点啰嗦。而且本来不需要定义一个全局变量的，现在弄出一个全局变量出来
     */
    val progressDialog by lazy { ProgressDialog(this@LoginActivity) }
    override fun showProgressDialog(title: String, msg: String) {
        progressDialog.setTitle("提示")
        progressDialog.setMessage("正在登录...")
        progressDialog.show()
    }

    override fun hideProgressDialog() {
        progressDialog.dismiss()
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

class LoginActivityPresenter(val view: ILoginActivityView) : ILoginActivityPresenter {

    override fun login() {

    }

}