package com.youtubedl.ui.main.base

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import dagger.android.support.DaggerAppCompatActivity

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

    internal fun startActivity(cls: Class<in AppCompatActivity>) {
        startActivity(Intent(this, cls))
    }
}