package com.practice.eyepetizer.mvp.ui.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import cn.yt.demo_kotlin.R

import com.practice.eyepetizer.base.BaseActivity
import com.practice.eyepetizer.glide.GlideApp
import com.practice.eyepetizer.globle.Constants
import com.practice.eyepetizer.mvp.contract.CategoryDetailContract
import com.practice.eyepetizer.mvp.model.bean.CategoryBean
import com.practice.eyepetizer.mvp.model.bean.HomeBean
import com.practice.eyepetizer.mvp.presenter.CategoryDetailPresenter
import com.practice.eyepetizer.mvp.ui.adapter.CategoryDetailAdapter
import com.practice.eyepetizer.utils.StatusBarUtil
import kotlinx.android.synthetic.main.activity_category_detail.*


/**
 * desc: 分类详情
 */
class CategoryDetailActivity : BaseActivity(), CategoryDetailContract.View {

    private val mPresenter by lazy { CategoryDetailPresenter() }

    private val mAdapter by lazy { CategoryDetailAdapter(this, itemList, R.layout.item_category_detail) }

    private var categoryData: CategoryBean? = null

    private var itemList = ArrayList<HomeBean.Issue.Item>()

    init {
        mPresenter.attachView(this)
    }

    /**
     * 是否加载更多
     */
    private var loadingMore = false

    override fun initData() {
        categoryData = intent.getSerializableExtra(Constants.BUNDLE_CATEGORY_DATA) as CategoryBean?

    }
    override fun getLayoutId(): Int  =  R.layout.activity_category_detail

    @SuppressLint("SetTextI18n")
    override fun initView() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // 加载headerImage
        GlideApp.with(this)
                .load(categoryData?.headerImage)
                .placeholder(R.color.color_darker_gray)
                .into(imageView)

        tv_category_desc.text ="#${categoryData?.description}#"

        collapsing_toolbar_layout.title = categoryData?.name
        collapsing_toolbar_layout.setExpandedTitleColor(Color.WHITE) //设置还没收缩时状态下字体颜色
        collapsing_toolbar_layout.setCollapsedTitleTextColor(Color.BLACK) //设置收缩后Toolbar上字体的颜色

        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = mAdapter
        //实现自动加载
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val itemCount = mRecyclerView.layoutManager.itemCount
                val lastVisibleItem = (mRecyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                if (!loadingMore && lastVisibleItem == (itemCount - 1)) {
                    loadingMore = true
                    mPresenter.loadMoreData()
                }
            }
        })

        //状态栏透明和间距处理
        StatusBarUtil.darkMode(this)
        StatusBarUtil.setPaddingSmart(this, toolbar)

    }

    override fun start() {
        //获取当前分类列表
        categoryData?.id?.let { mPresenter.getCategoryDetailList(it) }
    }

    override fun showLoading() {

    }

    override fun dismissLoading() {

    }

    override fun setCateDetailList(itemList: ArrayList<HomeBean.Issue.Item>) {
        loadingMore = false
        mAdapter.addData(itemList)
    }

    override fun showError(errorMsg: String) {
        multipleStatusView.showError()
    }


    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
    }



}