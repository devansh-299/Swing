package me.devansh.swing.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.RelativeLayout
import kotlin.math.roundToInt


class SwipeActionView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var swipeViewContainer: RelativeLayout
    private var swipeEndViewContainer: RelativeLayout
    private var swipeIndicatorContainer: RelativeLayout

    private var anchorBackOnRelease = false

    enum class Orientation {
        HORIZONTAL, VERTICAL
    }

    private var orientation: Orientation = Orientation.VERTICAL
    private var targetPoint = 0

    interface SwipeActionCallback {
        fun onSwingComplete()
    }

    private var swipeActionCallback: SwipeActionCallback? = null

    init {
        swipeViewContainer = RelativeLayout(context).apply {
            this.setBackgroundColor(Color.parseColor("#FF0000"))
        }
        swipeEndViewContainer = RelativeLayout(context).apply {
            // TODO: this is temporary
            this.setBackgroundColor(Color.parseColor("#0000FF"))
        }
        swipeIndicatorContainer = RelativeLayout(context)

        setupSwipeEndViewContainer()
        setupSwipeViewContainer()
    }

    private fun setupSwipeEndViewContainer() {
        val layoutParams = LayoutParams(200, 300).apply {
            if (orientation == Orientation.VERTICAL) {
                this.addRule(ALIGN_PARENT_BOTTOM)
                this.addRule(CENTER_HORIZONTAL)
            } else {
                this.addRule(ALIGN_PARENT_RIGHT)
                this.addRule(CENTER_VERTICAL)
            }
        }
        swipeEndViewContainer.layoutParams = layoutParams
        swipeEndViewContainer.elevation = 5f
        addView(swipeEndViewContainer)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        targetPoint = if (orientation == Orientation.VERTICAL) {
            height - swipeEndViewContainer.height
        } else {
            width - swipeEndViewContainer.width
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupSwipeViewContainer() {
        val layoutParams =
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                if (orientation == Orientation.VERTICAL) {
                    this.addRule(CENTER_HORIZONTAL)
                } else {
                    this.addRule(CENTER_VERTICAL)
                }
            }
        swipeViewContainer.layoutParams = layoutParams

        swipeViewContainer.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    val params = v.layoutParams as LayoutParams
                    val pointOfTouch = if (orientation == Orientation.VERTICAL) {
                        v.top
                    } else {
                        v.left
                    }
                    Log.e("library", "pointOfTouch:$pointOfTouch | target:$targetPoint")
                    if (pointOfTouch >= targetPoint) {
                        swipeActionCallback?.onSwingComplete()
                        if (orientation == Orientation.VERTICAL) {
                            params.topMargin = 0
                        } else {
                            params.marginStart = 0
                        }
                        swipeIndicatorContainer.visibility = VISIBLE
                        invalidate()

                    } else if (anchorBackOnRelease) {
                        val valueAnimator = if (orientation == Orientation.VERTICAL) {
                            ValueAnimator.ofInt(v.top, 0)
                        } else {
                            ValueAnimator.ofInt(v.left, 0)
                        }
                        valueAnimator.interpolator = LinearInterpolator()
                        valueAnimator.duration = 500
                        valueAnimator.addUpdateListener { animator: ValueAnimator ->
                            if (orientation == Orientation.VERTICAL) {
                                params.topMargin = animator.animatedValue as Int
                            } else {
                                params.marginStart = animator.animatedValue as Int
                            }
                            v.layoutParams = params
                            invalidate()
                        }
                        valueAnimator.addListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                super.onAnimationEnd(animation)
                                swipeIndicatorContainer.visibility = VISIBLE
                            }
                        })
                        valueAnimator.start()
                    }
                }

                MotionEvent.ACTION_DOWN -> {
                    swipeIndicatorContainer.visibility = GONE
                }

                MotionEvent.ACTION_MOVE -> {
                    val delta = if (orientation == Orientation.VERTICAL) {
                        (event.y + v.top).roundToInt()
                    } else {
                        (event.x + v.left).roundToInt()
                    }
                    val layoutParams = v.layoutParams as LayoutParams
                    if (orientation == Orientation.VERTICAL) {
                        layoutParams.topMargin = delta.coerceAtLeast(0)
                    } else {
                        layoutParams.marginStart = delta.coerceAtLeast(0)
                    }
                    v.layoutParams = layoutParams
                    invalidate()
                }
            }
            true
        }
        addView(swipeViewContainer)
    }

    fun anchorBackOnRelease(anchorBack: Boolean) {
        anchorBackOnRelease = anchorBack
    }

    fun setSwipeViewContainer(view: View) {
        swipeViewContainer.addView(view)
    }

    fun swipeActionCallback(callback: SwipeActionCallback) {
        swipeActionCallback = callback
    }

}
