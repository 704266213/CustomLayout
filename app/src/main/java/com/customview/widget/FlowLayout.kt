package com.customview.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
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

    private var lineHeights = mutableListOf<Integer>()


    constructor(context: Context?, attributeSet: AttributeSet) : this(context)


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
                lineHeights.add(Integer(lineHeight))

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
        lineHeights.add(Integer(lineHeight))
        flowLayoutWidth = max(flowLayoutWidth, lineWidth)
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
                bottom = top + it.measuredHeight
                it.layout(left, top, right, bottom)
                currentX = it.measuredWidth + layoutParams.leftMargin + layoutParams.rightMargin
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

        constructor(c: Context?, attrs: AttributeSet?) : super(c, attrs);

        constructor(layoutParams: LayoutParams) : super(layoutParams)

        constructor(width: Int, height: Int) : super(width, height)
    }

}