package com.newbiechen.nbreader.ui.component.widget

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.IntDef
import com.newbiechen.nbreader.R

/**
 *  author : newbiechen
 *  date : 2019-08-08 15:15
 *  description：处理加载状态的 View
 */

typealias OnRetryClickListener = (view: View) -> Unit

class StatusView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    @IntDef(value = [STATUS_EMPTY, STATUS_LOADING, STATUS_FINISH, STATUS_ERROR])
    @Retention(AnnotationRetention.SOURCE)
    annotation class Status

    companion object {
        private val TAG = "StatusView"

        const val STATUS_LOADING = 0
        const val STATUS_EMPTY = 1
        const val STATUS_ERROR = 2
        const val STATUS_FINISH = 3
    }

    private var mEmptyViewId: Int = 0
    private var mErrorViewId: Int = 0
    private var mLoadingViewId: Int = 0
    private var mStatus = STATUS_LOADING

    private lateinit var mEmptyView: View
    private lateinit var mErrorView: View
    private lateinit var mLoadingView: View
    private var mContentView: View? = null

    private var mListener: OnRetryClickListener? = null

    init {
        initAttrs(attrs)
        initView()
    }

    private fun initAttrs(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.StatusView)
        mEmptyViewId = typedArray.getResourceId(
            R.styleable.StatusView_layout_empty,
            R.layout.layout_common_empty
        )
        mErrorViewId =
            typedArray.getResourceId(
                R.styleable.StatusView_layout_error,
                R.layout.layout_common_error
            )
        mLoadingViewId =
            typedArray.getResourceId(
                R.styleable.StatusView_layout_loading,
                R.layout.layout_common_loadding
            )

        typedArray.recycle()
    }

    private fun initView() {
        //添加在empty、error、loading 情况下的布局
        mEmptyView = inflateView(mEmptyViewId)
        mErrorView = inflateView(mErrorViewId)
        mLoadingView = inflateView(mLoadingViewId)

        // 添加状态 view
        addView(mEmptyView)
        addView(mErrorView)
        addView(mLoadingView)

        // 添加点击重试
        val retryBtn: View = mErrorView.findViewById(R.id.tv_retry)

        //设置监听器
        retryBtn.setOnClickListener {
            if (mListener != null) {
                toggleStatus(STATUS_LOADING)
                mListener?.invoke(it)
            }
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        toggleStatus(STATUS_LOADING)
    }

    override fun onViewAdded(child: View) {
        super.onViewAdded(child)
        // contentView 是最后一个加入的 View
        if (childCount == 4) {
            mContentView = child
        }
    }

    //除了自带的数据，保证子类只能够添加一个子View
    override fun addView(child: View?) {
        if (childCount > 4) {
            throw IllegalStateException("StatusView can host only one direct child")
        }
        super.addView(child)
    }

    override fun addView(child: View, index: Int) {
        if (childCount > 4) {
            throw IllegalStateException("StatusView can host only one direct child")
        }

        super.addView(child, index)
    }

    override fun addView(child: View, params: ViewGroup.LayoutParams) {
        if (childCount > 4) {
            throw IllegalStateException("StatusView can host only one direct child")
        }

        super.addView(child, params)
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        if (childCount > 4) {
            throw IllegalStateException("StatusView can host only one direct child")
        }

        super.addView(child, index, params)
    }

    //视图根据状态切换
    private fun toggleStatus(status: Int) {
        when (status) {
            STATUS_LOADING -> {
                mLoadingView.visibility = View.VISIBLE
                mEmptyView.visibility = View.GONE
                mErrorView.visibility = View.GONE
                mContentView?.visibility = View.GONE
            }
            STATUS_FINISH -> {
                mContentView?.visibility = View.VISIBLE
                mLoadingView.visibility = View.GONE
                mEmptyView.visibility = View.GONE
                mErrorView.visibility = View.GONE
            }
            STATUS_ERROR -> {
                mErrorView.visibility = View.VISIBLE
                mLoadingView.visibility = View.GONE
                mEmptyView.visibility = View.GONE
                mContentView?.visibility = View.GONE

            }
            STATUS_EMPTY -> {
                mEmptyView.visibility = View.VISIBLE
                mErrorView.visibility = View.GONE
                mLoadingView.visibility = View.GONE
                mContentView?.visibility = View.GONE
            }
        }
        mStatus = status

        // 进行刷新
        invalidate()
    }

    fun setOnReloadingListener(listener: OnRetryClickListener) {
        mListener = listener
    }

    // 设置当前展示的 view
    fun setStatus(@Status status: Int) {
        toggleStatus(status)
    }

    fun getStatus(): Int {
        return mStatus
    }

    private fun inflateView(id: Int): View {
        return LayoutInflater.from(context)
            .inflate(id, this, false)
    }

    //数据存储
    override fun onSaveInstanceState(): Parcelable? {
        val superParcel = super.onSaveInstanceState() ?: return null
        val savedState = SavedState(superParcel)
        savedState.status = mStatus
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        //刷新状态
        toggleStatus(savedState.status)
    }

    internal class SavedState : BaseSavedState {
        var status: Int = 0

        constructor(superState: Parcelable) : super(superState)

        private constructor(source: Parcel) : super(source) {
            status = source.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(status)
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(source: Parcel?): SavedState {
                return SavedState(source!!)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }
}


