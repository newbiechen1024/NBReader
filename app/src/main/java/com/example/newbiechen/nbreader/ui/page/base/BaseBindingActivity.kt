package com.youtubedl.ui.main.base

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.newbiechen.nbreader.ui.component.extension.overlayStatusBar
import com.example.newbiechen.nbreader.uilts.SystemBarUtil
import dagger.android.support.DaggerAppCompatActivity
import kotlin.reflect.KClass

/**
 * 由于使用到了 DaggerAppCompatActivity，创建的 Activity 必须在 ActivityBindingModule 进行注册
 */
abstract class BaseBindingActivity<T : ViewDataBinding> : DaggerAppCompatActivity() {

    protected lateinit var mDataBinding: T

    internal abstract fun initContentView(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, initContentView())
        initData(savedInstanceState)
        initView()
        processLogic()
    }


    internal open fun initData(savedInstanceState: Bundle?) {
    }

    internal open fun initView() {
    }

    internal open fun processLogic() {
    }

    protected fun supportActionBar(toolbar: Toolbar): ActionBar? {
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowHomeEnabled(true)
            actionBar.title = ""
        }

        toolbar.setNavigationOnClickListener { finish() }
        return actionBar
    }

    /**
     * 显示到 statusbar 上
     */
    protected fun overStatusBar(toolbar: Toolbar? = null) {
        SystemBarUtil.transparentStatusBar(this)
        if (Build.VERSION.SDK_INT >= 19) {
            toolbar?.overlayStatusBar()
        }
    }

    internal fun startActivity(kCls: KClass<*>) {
        startActivity(Intent(this, kCls.java))
    }
}