package com.adazhdw.ktlib.list

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager


class ListRecyclerView : RecyclerView {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var mLoadMoreListener: LoadMoreListener? = null
    private var isLoadingMore: Boolean = false
    private var isLoadError: Boolean = false
    private var isDateEmpty: Boolean = false
    private var mHasMore: Boolean = true
    private var loadMoreEnabled = true
    private var mScrollState = -1
    private var mLoadMoreView: LoadMoreView = DefaultLoadMoreView(context)

    override fun onScrollStateChanged(state: Int) {
        this.mScrollState = state
    }

    override fun onScrolled(dx: Int, dy: Int) {
        val layoutManager = layoutManager
        val itemCount = layoutManager?.itemCount ?: 0
        if (itemCount <= 0) return
        val isPullUp =
            (mScrollState == SCROLL_STATE_DRAGGING || mScrollState == SCROLL_STATE_SETTLING) && dy > 0//dy>0表示手指向上滑动时，才触发loadmore
        val lastVisiblePosition: Int
        when (layoutManager) {
            is GridLayoutManager -> {
                lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
                if (itemCount == lastVisiblePosition + 1 && isPullUp) {
                    dispatchLoadMore()
                }
            }
            is StaggeredGridLayoutManager -> {
                val into = intArrayOf(layoutManager.spanCount)
                layoutManager.findFirstVisibleItemPositions(into)
                lastVisiblePosition = into.max() ?: 0
                if (itemCount == lastVisiblePosition + 1 && isPullUp) {
                    dispatchLoadMore()
                }
            }
            is LinearLayoutManager -> {
                lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
                if (itemCount == lastVisiblePosition + 1 && isPullUp) {
                    dispatchLoadMore()
                }
            }
        }
    }

    private fun dispatchLoadMore() {
        if (loadMoreEnabled && !isLoadingMore) {
            val adapter = adapter
            if (adapter != null && adapter is ListAdapter) {
                isLoadingMore = true
                adapter.loading(true)
                mLoadMoreView.onLoading()
                mLoadMoreListener?.onLoadMore()
            }
        }
    }

    fun onLoadFinish(dataEmpty: Boolean, hasMore: Boolean) {
        isLoadingMore = false
        isLoadError = false

        isDateEmpty = dataEmpty
        mHasMore = hasMore

        mLoadMoreView.onLoadFinish(dataEmpty, hasMore)

    }

    fun onLoadError(errorCode: Int, errorMsg: String?) {
        isLoadingMore = false
        isLoadError = true

        mLoadMoreView.onLoadError(errorCode, errorMsg)
    }

    fun loadMoreEnabled(isEnabled: Boolean) {
        loadMoreEnabled = isEnabled
    }

    fun isLoadingMore(): Boolean {
        return isLoadingMore
    }

    fun setLoadMoreListener(listener: LoadMoreListener) {
        this.mLoadMoreListener = listener
    }

    fun setLoadMoreView(loadMoreView: LoadMoreView) {
        mLoadMoreView = loadMoreView
    }

    fun getLoadMoreView(): LoadMoreView {
        return mLoadMoreView
    }

    interface LoadMoreListener {

        /**
         * More data should be requested.
         */
        fun onLoadMore()
    }
}