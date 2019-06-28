package com.customview.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.Scroller
import kotlin.math.abs
import kotlin.math.max


/**
 *
 * 类描述：
 * 创建人：alan
 * 创建时间：2019-06-06 23:52
 * 修改备注：
 * @version
 *
 */
class FlowLayout(context: Context?) : ViewGroup(context) {


    private val allViews = mutableListOf<List<View>>()

    private var lineViews = mutableListOf<View>()

    private var lineHeights = mutableListOf<Int>()

    private lateinit var scroller: Scroller
    private var scaledDoubleTapSlop = 0


    constructor(context: Context?, attributeSet: AttributeSet) : this(context) {
        scroller = Scroller(context)
        val viewConfiguration = ViewConfiguration.get(getContext())
        scaledDoubleTapSlop = viewConfiguration.scaledDoubleTapSlop
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        allViews.clear()
        lineViews.clear()
        lineHeights.clear()

        val parentWidthMode = MeasureSpec.getMode(widthMeasureSpec)
        val parentWidthSize = MeasureSpec.getSize(widthMeasureSpec)
        val parentHeightMode = MeasureSpec.getMode(heightMeasureSpec)
        val parentHeightSize = MeasureSpec.getSize(heightMeasureSpec)


        var lineWidth = 0
        var lineHeight = 0

        var flowLayoutWidth = 0
        var flowLayoutHeight = 0

        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            measureChild(childView, widthMeasureSpec, heightMeasureSpec)

            val layoutParams = childView.layoutParams as MarginLayoutParams
            val measuredChileWidth = childView.measuredWidth + layoutParams.leftMargin + layoutParams.rightMargin
            val measuredChileHeight = childView.measuredHeight + layoutParams.topMargin + layoutParams.bottomMargin


            if (lineWidth + measuredChileWidth > parentWidthSize) {
                allViews.add(lineViews)
                lineHeights.add(lineHeight)

                flowLayoutWidth = max(flowLayoutWidth, lineWidth)
                flowLayoutHeight += lineHeight

                lineWidth = 0
                lineHeight = 0
                lineViews = mutableListOf()
            }

            lineWidth += measuredChileWidth
            lineHeight = max(lineHeight, measuredChileHeight)
            lineViews.add(childView)
        }

        allViews.add(lineViews)

        lineHeights.add(lineHeight)

        flowLayoutWidth = if (flowLayoutWidth > parentWidthSize) parentWidthSize else max(flowLayoutWidth, lineWidth)
        flowLayoutHeight += lineHeight


        val parentWidthMeasure =
            if (parentWidthMode == MeasureSpec.EXACTLY) parentWidthSize else flowLayoutWidth
        var parentHeightMeasure =
            if (parentHeightMode == MeasureSpec.EXACTLY) parentHeightSize else flowLayoutHeight
        setMeasuredDimension(parentWidthMeasure, parentHeightMeasure)

    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        var left = 0
        var top = 0
        var right = 0
        var bottom = 0

        var size = allViews.size
        var currentX = 0
        var currentY = 0

        for (i in 0 until size) {
            val lineHeight = lineHeights[i].toInt()
            allViews[i].forEach {
                val layoutParams = it.layoutParams as MarginLayoutParams
                left = currentX + layoutParams.leftMargin
                top = currentY + layoutParams.topMargin
                right = left + it.measuredWidth
                Log.e("XLog", "====================== measuredWidth : $measuredWidth")
                Log.e("XLog", "====================== right : $right")

                if (right > measuredWidth) {
                    right = measuredWidth - layoutParams.rightMargin
                }
                bottom = top + it.measuredHeight
                it.layout(left, top, right, bottom)
                currentX += it.measuredWidth + layoutParams.leftMargin + layoutParams.rightMargin
            }
            currentX = 0
            currentY += lineHeight
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return FlowLayoutParams(context, attrs)
    }

    override fun generateLayoutParams(layoutParams: LayoutParams?): LayoutParams {
        return FlowLayoutParams(layoutParams!!)
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return FlowLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    override fun checkLayoutParams(layoutParams: LayoutParams?): Boolean {
        return layoutParams is FlowLayoutParams
    }


    class FlowLayoutParams : MarginLayoutParams {

        constructor(c: Context?, attrs: AttributeSet?) : super(c, attrs)

//        val a = c.obtainStyledAttributes(attrs, R.styleable.CustomViewGroup)
//        gravity = a.getInt(R.styleable.CustomViewGroup_custom_gravity, UNSPECIFIED_GRAVITY)
//        a.recycle()

        constructor(layoutParams: LayoutParams) : super(layoutParams)

        constructor(width: Int, height: Int) : super(width, height)
    }


    private var mLastY: Int = 0

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        val y = ev.y.toInt()
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                if (scroller.isFinished) {
                    scroller.abortAnimation()
                }
                mLastY = y
            }
            MotionEvent.ACTION_MOVE -> {
                val dy = mLastY - y
                //当前手指位置的坐标
                val oldScrollY = scrollY
                //手指偏移后的坐标
                var preScrollY = oldScrollY + dy
//                if (preScrollX > (childCount - 1) * height) {
//                    preScrollX = (childCount - 1) * height
//                }
                if (preScrollY < 0) {
                    preScrollY = 0
                }

                //scrollTo 是从其实的按下位置开始偏移，每次移动都是相对按下的位置
//                scrollTo(0, preScrollY)
                //scrollBy 是从手指当前位置的坐标开始偏移，每次移动都是当前位置的位置
                // scrollBy(dx,dy)


                //开始滑动动画
                //第一步 finalY 滚动后的位置的坐标（即滚动后当前手指的坐标）滑动完成后的坐标，垂直方向，
                scroller.startScroll(0, scroller.finalY, 0, dy)
                //注意，一定要进行invalidate刷新界面，触发computeScroll()方法，因为单纯的startScroll()是属于Scroller的，只是一个辅助类，并不会触发界面的绘制
                invalidate()
                mLastY = y
            }
        }
        return true
    }

    override fun computeScroll() {
        super.computeScroll()
        //第二步 判断滚动偏移是否完成
        if (scroller.computeScrollOffset()) {
            //第三步 更新滚动偏移， 滑动过程中，根据消耗的时间计算出的当前的滑动偏移距离，垂直方向
            scrollTo(0, scroller.currY)
            invalidate()
        }
    }

    private var mLastX: Int = 0
    private var intercepted = false


    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        val currentX = event.x.toInt()
        val currentY = event.y.toInt()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                intercepted = false
                mLastX = currentX // 用于判断是否拦截的条件
                mLastY = currentY // 用于判断是否拦截的条件
            }
            MotionEvent.ACTION_MOVE -> {
                var dx = currentX - mLastX
                var dy = currentY - mLastY
                intercepted = abs(dy) > abs(dx)
                Log.e("XLog", "====================== dy : ${abs(dy)}")
                Log.e("XLog", "====================== scaledDoubleTapSlop : ${abs(dy) > scaledDoubleTapSlop}")
                Log.e("XLog", "======================  : ${abs(dy) > abs(dx)}")
                Log.e("XLog", "====================== intercepted : $intercepted")
            }
            MotionEvent.ACTION_UP -> {
                intercepted = false
            }
        }
        mLastX = currentX // 用于判断是否拦截的条件
        mLastY = currentY // 用于判断是否拦截的条件

        return intercepted
    }

}