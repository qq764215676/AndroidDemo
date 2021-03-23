package com.yuran.androiddemo.activity

import android.app.ProgressDialog
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.gudsen.library.extension.info
import com.gudsen.library.util.ToastPlus
import com.yuran.androiddemo.R
import com.yuran.androiddemo.api.Constants
import com.yuran.androiddemo.api.RetrofitUtils
import com.yuran.androiddemo.application.MainApplication
import com.yuran.androiddemo.biz.Utils
import com.yuran.androiddemo.storage.SP
import kotlinx.android.synthetic.main.login_activity.*
import kotlinx.coroutines.launch

// TODO: 2/1/21 EditText怎么配合ViewModel使用；优化SP的用法
class LoginActivity1 : BaseActivity(R.layout.login_activity) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        btn_login.setOnClickListener {
            lifecycleScope.launch {
                val progressDialog = ProgressDialog(this@LoginActivity1)
                progressDialog.setTitle("提示")
                progressDialog.setMessage("正在登录...")
                progressDialog.show()

                try {
                    val bean = RetrofitUtils.webService.api_login(et_account.text.toString(), et_password.text.toString())
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
                } catch (e: Exception) {
                    ToastPlus.globalShowLongCatchNPE(e.info)
                }

                progressDialog.dismiss()
            }
        }
    }
}