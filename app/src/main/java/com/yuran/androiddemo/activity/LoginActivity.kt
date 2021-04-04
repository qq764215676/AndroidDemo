package com.yuran.androiddemo.activity

import android.app.ProgressDialog
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.BounceInterpolator
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
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
import com.gudsen.library.util.ViewUtils
import com.gudsen.library.util.isTouchPointInView
import com.gudsen.library.util.point
import com.orhanobut.logger.Logger
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

//        et_account.doOnTextChanged { text, start, before, count ->
//            viewModel.setAccount(text.toString())
//            /*if (text != viewModel.getAccount().value) {
//                viewModel.setAccount(text.toString())
//            }*/
//        }
//        viewModel.getAccount().observe(this, Observer {
//            if (it != et_account.text.toString()) {
//                et_account.setText(it)
//            }
//            et_account.setTextNoSame(it)
//        })

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
//                        viewModel.getAccount().value!!,
                        viewModel.account.value!!,
//                        viewModel.account2,
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

        viewDataBinding.btnDebug.setOnClickListener {
            viewModel.account.value = "15897770916"
            viewModel.account2 = "15897770916"
            viewModel.account3.set("15897770916")
        }

        //把这段给按钮添加缩放效果的代码封装一个方法
//        viewDataBinding.btnLogin.isLongClickable = false
        viewDataBinding.btnLogin.setOnTouchListener(object : View.OnTouchListener {
            var isTouchPointInView: Boolean = true
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                Logger.d("event.action=${event.action}")

                fun zoomOn() {
                    v.animate().scaleX(1.2F).scaleY(1.2F)/*.setInterpolator(BounceInterpolator())*/
                }

                fun zoomIn() {
                    v.animate().scaleX(1.0F).scaleY(1.0F)
                }

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        zoomOn()
//                    v.parent.requestDisallowInterceptTouchEvent(true)
                    }
                    /*
                    只要按下，就会不断产生ACTION_MOVE事件(即使按下后没有移动手指)，直到松开
                     */
                    MotionEvent.ACTION_MOVE -> {
                        if (isTouchPointInView != v.isTouchPointInView(event)) {
                            isTouchPointInView = v.isTouchPointInView(event)
                            if (isTouchPointInView) {
                                zoomOn()
                            } else {
                                zoomIn()
                            }
                        }
                    }
                    /*
                    按下不移动长按松开时，会产生ACTION_CANCEL事件
                    按下移动长按松开时，产生ACTION_UP事件
                     */
                    // TODO: 3/23/21 可以判断，如果抬起时在View里面，就触发点击事件。不过看系统默认的，按下有悬浮效果，移开就没了，系统是这样的，不过也只有默认的Button背景有悬浮，一般自定义背景的没有。
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        zoomIn()
                    }
                }

                return false
            }
        })
        /*viewDataBinding.btnLogin.setOnTouchListener { v, event ->

            return@setOnTouchListener false
        }*/
    }
}

class LoginActivityViewModel() : ViewModel() {
    //    private val account by lazy { MutableLiveData<String>().apply { value = "13545147094" } }
    /*
    还是不能直接给公开的account可变LiveData变量给DataBinding xml双向绑定。get没问题，可以直接用，但是set不一定是直接赋一个值，有可能需要传多个参，set很多情况下都可能需要自己重新封装提供一个。
    不过一般，双向绑定的，都是简单的提供默认的get、set就行了。因为这个变量对应的就是控件比如EditText的text的值、ProgressBar的progress等等。
     */
    val account by lazy { MutableLiveData<String>().apply { value = "13545147094" } }
    /*fun getAccount(): LiveData<String> = account
    fun setAccount(account: String) {
        this.account.value = account
    }*/

    //    val _account: LiveData<String> get() = account
//    val _account: MutableLiveData<String> get() = account
//    fun _getAccount(): LiveData<String> = account

    var account2 = ""
    val account3 by lazy { ObservableField("") }

    private val password by lazy { MutableLiveData<String>() }
    fun getPassword(): LiveData<String> = password
    fun setPassword(password: String) {
        this.password.value = password
    }
}