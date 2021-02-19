package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    enum class ButtonState {
        NORMAL, LOADING
    }

    private var progress = 0.0f

    // To add animation we use value animator to change property overtime
    // when started animation updates periodically and provides new progress
    // https://medium.com/@dbottillo/animate-android-custom-view-a94473412054
    // https://developer.android.com/guide/topics/graphics/prop-animation
    private val valueAnimator = ValueAnimator.ofInt(0, 100).apply {
        // we would like animation to repeat until button status changes
        repeatCount = ValueAnimator.INFINITE
        repeatMode = ValueAnimator.RESTART

        // on each update we would like to update "progress" property that is used
        // to calculate "width" of progress rectangle and "degree" for arc
        addUpdateListener {
            // update progress in range from 0 to 1
            progress = it.animatedFraction

            // request animation update
            invalidate()
        }
    }

    //make sure button is not clickable if it is loading
    var buttonState: ButtonState by Delegates.observable(ButtonState.NORMAL) { p, old, new ->
        // when button state changes, start or stop animator depending on the button state
        // animator will change internal "progress" value from range to 0.0 to 1.0
        isClickable = when (buttonState) {
            ButtonState.LOADING -> {
                valueAnimator.start()
                false
            }
            else -> {
                valueAnimator.end()
                true
            }
        }
        // if button state changes - invalidate UI
        // this will force onDraw method to be called
        invalidate()
    }

    // Paint styles used for rendering are initialized here. This
    // is a performance optimization, since onDraw() is called
    // for every screen refresh.
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 56.0f
    }

    private var normalText = ""
    private var normalColor = 0

    private var loadingText = ""
    private var loadingColor = 0

    private var circleColor = 0

    // Making the view clickable
    init {
        isClickable = true

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            normalText = getText(R.styleable.LoadingButton_normalText).toString()
            normalColor = getColor(R.styleable.LoadingButton_normalBackground, 0)
            loadingText = getText(R.styleable.LoadingButton_loadingText).toString()
            loadingColor = getColor(R.styleable.LoadingButton_loadingBackground, 0)
            circleColor = getColor(R.styleable.LoadingButton_circleColor, 0)
            valueAnimator.duration =
                getInt((R.styleable.LoadingButton_animationDuration), 1000).toLong()
        }
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val textPositionX = width / 2f
        val textPositionY = (height / 2 - (paint.descent() + paint.ascent()) / 2)

        when (buttonState) {
            ButtonState.LOADING -> {
                paint.color = normalColor
                canvas?.drawRect(0.0f, 0.0f, width.toFloat(), height.toFloat(), paint)

                paint.color = loadingColor
                canvas?.drawRect(0.0f, 0.0f, width.toFloat() * progress, height.toFloat(), paint)

                val textWidth = paint.measureText(loadingText)

                val arcPositionX = textPositionX + textWidth / 2 + 16
                val arcPositionY = height / 2 - paint.textSize / 2

                // Drawing progress circle
                paint.color = circleColor
                canvas?.drawArc(
                    arcPositionX,
                    arcPositionY,
                    arcPositionX + paint.textSize,
                    arcPositionY + paint.textSize,
                    0f,
                    360f * progress,
                    true,
                    paint
                )

                paint.color = Color.WHITE
                canvas?.drawText(loadingText, textPositionX, textPositionY, paint)
            }
            else -> {
                paint.color = normalColor
                canvas?.drawRect(0.0f, 0.0f, width.toFloat(), height.toFloat(), paint)

                paint.color = Color.WHITE
                canvas?.drawText(normalText, textPositionX, textPositionY, paint)
            }
        }
    }


    // https://stackoverflow.com/a/12267248
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minWidth: Int = paddingLeft + paddingRight + suggestedMinimumWidth

        val w: Int = resolveSizeAndState(minWidth, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )

        setMeasuredDimension(w, h)
    }

}