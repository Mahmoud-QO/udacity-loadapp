package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
	context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr)
{
	//// Class Members ///////////////////////////////////////////////////////////////////////////

	companion object{
		sealed class ButtonState {
			object Clicked : ButtonState()
			object Loading : ButtonState()
			object Completed : ButtonState()
		}
	}

	//// Object Members //////////////////////////////////////////////////////////////////////////

	private val backgroundColor: Int = context.getColor(R.color.colorPrimary)
	private val progressBarColor: Int = context.getColor(R.color.colorPrimaryDark)
	private val progressCircleColor: Int = context.getColor(R.color.colorAccent)
	private val textColor: Int = context.getColor(R.color.white)

	private var widthSize = 0
	private var heightSize = 0
	private var text = context.getString(R.string.button_name)
	private var progress = 0f

	var state: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { _, _, new ->
		when (new) {
			ButtonState.Clicked -> {}
			ButtonState.Loading -> {
				valueAnimator.start()
				isClickable = false
				text = context.getString(R.string.button_loading)
			}
			ButtonState.Completed -> {
				valueAnimator.cancel()
				isClickable = true
				text = context.getString(R.string.button_name)
				progress = 0f
				invalidate()
			}
		}
	}

	private val paint = Paint().apply {
		isAntiAlias = true
		style = Paint.Style.FILL
		textSize = 55.0f
		textAlign = Paint.Align.CENTER
		typeface = Typeface.create("", Typeface.BOLD)
	}

	private val valueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
		duration = 2000
		repeatMode = ValueAnimator.RESTART
		repeatCount = ValueAnimator.INFINITE
		addUpdateListener {
			progress = it.animatedValue as Float
			invalidate()
		}
	}

	//// Init ////////////////////////////////////////////////////////////////////////////////////

	init {
		isClickable = true
	}

	//// Override ////////////////////////////////////////////////////////////////////////////////

	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)

		paint.color = backgroundColor
		canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)

		paint.color = progressBarColor
		canvas?.drawRect(0f, 0f, progress*widthSize, heightSize.toFloat(), paint)

		paint.color = textColor
		canvas?.drawText(text, 0.5f*widthSize, 0.5f*heightSize + 20, paint)

		paint.color = progressCircleColor
		canvas?.drawArc(widthSize - 200f, 50f, widthSize - 100f, 150f,
			0f, 360*progress, true, paint
		)
	}

	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		val minWidth: Int = paddingLeft + paddingRight + suggestedMinimumWidth
		val w: Int = resolveSizeAndState(minWidth, widthMeasureSpec, 1)
		val h: Int = resolveSizeAndState(MeasureSpec.getSize(w), heightMeasureSpec, 0)
		widthSize = w
		heightSize = h
		setMeasuredDimension(w, h)
	}

	//// Functions ///////////////////////////////////////////////////////////////////////////////

}