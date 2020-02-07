package com.newbiechen.nbreader.ui.page.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import dagger.android.support.DaggerFragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlin.reflect.KClass

abstract class BaseBindingFragment<T : ViewDataBinding> : DaggerFragment() {
    protected lateinit var mDataBinding: T

    internal abstract fun initContentView(): Int

    private var compositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mDataBinding = DataBindingUtil.inflate(inflater, initContentView(), container, false)
        initData(savedInstanceState)
        return mDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        processLogic()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    protected fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    protected fun addAllDisposable(vararg disposable: Disposable) {
        compositeDisposable.addAll(*disposable)
    }

    internal open fun initData(savedInstanceState: Bundle?) {
    }

    internal open fun initView() {
    }

    internal open fun processLogic() {
    }

    internal fun startActivity(cls: KClass<*>) {
        startActivity(Intent(context, cls.java))
    }
}