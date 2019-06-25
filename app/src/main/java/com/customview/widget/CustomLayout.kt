package com.customview.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import com.customview.R
import kotlin.math.max

/**
 *
 * 类描述：
 * 创建人：alan
 * 创建时间：2019-06-06 23:15
 * 修改备注：
 * @version
 *
 */
class CustomLayout : ViewGroup {

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attributeSet: AttributeSet) : super(context, attributeSet)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        //遍历子控件，并对子控件进行测量
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            val layoutParams = childView.layoutParams

            //根据父View的widthMeasureSpec获取子View的MeasureSpec，
            // layoutParams.width为子View的Xml里面的值match_parent，wrap_content，或者精确值
            val childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, 0, layoutParams.width)
            val childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, 0, layoutParams.height)
            //测量子view
            childView.measure(childWidthMeasureSpec, childHeightMeasureSpec)
        }

        //获取测量的模式
        val parentWidthMode = MeasureSpec.getMode(widthMeasureSpec)
        //获取测量的大小
        val parentWidthSize = MeasureSpec.getSize(widthMeasureSpec)

        val parentHeightMode = MeasureSpec.getMode(heightMeasureSpec)
        val parentHeightSize = MeasureSpec.getSize(heightMeasureSpec)

        var parentWidth = 0
        var parentHeight = 0

        when (parentWidthMode) {
            //如果是精确值
            MeasureSpec.EXACTLY -> {
                parentWidth = parentWidthSize
            }
            //如果父View尽可能大的话，取子View中最大的值
            MeasureSpec.AT_MOST -> {
                for (i in 0 until childCount) {
                    val childView = getChildAt(i)
                    val customLayoutParams = childView.layoutParams as CustomLayoutParams
                    parentWidth = max(parentWidth, childView.measuredWidth + customLayoutParams.layoutOffset.toInt())
                }
            }
            //
            MeasureSpec.UNSPECIFIED -> {
                for (i in 0 until childCount) {
                    val childView = getChildAt(i)
                    val customLayoutParams = childView.layoutParams as CustomLayoutParams
                    parentWidth = max(parentWidth, childView.measuredWidth + customLayoutParams.layoutOffset.toInt())
                }
            }
        }


        when (parentHeightMode) {
            MeasureSpec.EXACTLY -> {
                parentHeight = parentHeightSize
            }
            MeasureSpec.AT_MOST -> {
                for (i in 0 until childCount) {
                    val childView = getChildAt(i)
                    parentHeight += childView.measuredHeight
                }
            }
            MeasureSpec.UNSPECIFIED -> {
                for (i in 0 until childCount) {
                    val childView = getChildAt(i)
                    parentHeight += childView.measuredHeight
                }
            }
        }

        //保存测量的结果
        setMeasuredDimension(parentWidth, parentHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var left = 0
        var right = 0
        var top = 0
        var bottom = 0
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            val customLayoutParams = childView.layoutParams as CustomLayoutParams
            left = customLayoutParams.layoutOffset
            right = left + childView.measuredWidth
            bottom = top + childView.measuredHeight
            childView.layout(left, top, right, bottom)
            top += childView.measuredHeight
        }
    }


    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return CustomLayoutParams(context, attrs)
    }

    override fun generateLayoutParams(layoutParams: LayoutParams?): LayoutParams {
        return CustomLayoutParams(layoutParams!!)
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return CustomLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    override fun checkLayoutParams(layoutParams: LayoutParams?): Boolean {
        return layoutParams is CustomLayoutParams
    }


    class CustomLayoutParams : MarginLayoutParams {

        val POSITION_MIDDLE = 0 // 中间
        val POSITION_LEFT = 1 // 左上方
        val POSITION_RIGHT = 2 // 右上方
        val POSITION_BOTTOM = 3 // 左下角
        val POSITION_RIGHTANDBOTTOM = 4 // 右下角

        var position = POSITION_LEFT  // 默认我们的位置就是左上角

        var layoutOffset = 0

        constructor(c: Context?, attrs: AttributeSet?) : super(c, attrs) {

            val attrs = c!!.obtainStyledAttributes(attrs, R.styleable.CustomLayout)
            //获取设置在子控件上的位置属性
            position = attrs.getInt(R.styleable.CustomLayout_layout_position, position)
            layoutOffset = attrs.getDimension(R.styleable.CustomLayout_layout_offset, 0f).toInt()
            attrs.recycle()
        }

        constructor(layoutParams: LayoutParams) : super(layoutParams)

        constructor(width: Int, height: Int) : super(width, height)

    }
}