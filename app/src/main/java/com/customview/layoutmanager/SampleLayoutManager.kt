package com.customview.layoutmanager

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView



class SampleLayoutManager(private val context: Context) : RecyclerView.LayoutManager() {


    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        //如果没有item，直接返回
        if (itemCount <= 0) return
        // 跳过preLayout，preLayout主要用于支持动画
        if (state!!.isPreLayout()) {
            return
        }
        //在布局之前，将所有的子View先Detach掉，放入到Scrap缓存中
        detachAndScrapAttachedViews(recycler!!)

        //定义竖直方向的偏移量
        var offsetY = 0
        for (i in 0 until itemCount) {
            //这里就是从缓存里面取出
            val itemView = recycler.getViewForPosition(i)
            //将View加入到RecyclerView中
            addView(itemView)

            measureChildWithMargins(itemView, 0, 0)
            val width = getDecoratedMeasuredWidth(itemView)
            val height = getDecoratedMeasuredHeight(itemView)

            //最后，将View布局
            val left = (getWidth() - width) / 2

            layoutDecorated(itemView, left, offsetY, getWidth() - left, offsetY + height)
            //将竖直方向偏移量增大height
            offsetY += height - 200

            totalHeight += height
        }
    }

    override fun canScrollVertically(): Boolean {
        return true
    }

    private var totalHeight = 0
    private var verticalScrollOffset = 0

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        //实际要滑动的距离
        var travel = dy

        //如果滑动到最顶部
        if (verticalScrollOffset + dy < 0) {
            travel = -verticalScrollOffset
        } else if (verticalScrollOffset + dy > totalHeight - getVerticalSpace()) {//如果滑动到最底部
            travel = totalHeight - getVerticalSpace() - verticalScrollOffset
        }

        //将竖直方向的偏移量+travel
        verticalScrollOffset += travel

        // 平移容器内的item
        offsetChildrenVertical(-travel)
        return travel
    }

    /**
     * 获取RecyclerView在垂直方向上的可用空间，即去除了padding后的高度
     *
     * @return
     */
    private fun getVerticalSpace(): Int {
        return height - paddingBottom - paddingTop
    }
}