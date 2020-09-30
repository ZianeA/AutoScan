package com.meteoalgerie.autoscan.presentation.util

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.meteoalgerie.autoscan.R
import kotlinx.android.synthetic.main.my_snackbar.view.*

class MySnackbar : CardView {
    private var isAnimating = false
    private var nextMessage: (() -> Unit)? = null

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs, defStyleAttr)
    }

    private fun init(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int = 0
    ) {
        inflate(getContext(), R.layout.my_snackbar, this) as CardView
        setCardBackgroundColor(
            ContextCompat.getColor(getContext(), R.color.snackbar_background_color)
        )
        radius = resources.getDimension(R.dimen.snackbar_corner_radius)
        cardElevation = resources.getDimension(R.dimen.snackbar_elevation)
    }

    fun showMessage(message: String, duration: Long = LENGTH_LONG) {
        if (isAnimating) {
            nextMessage = { showMessage(message, duration) }
            return
        }

        isAnimating = true
        alpha = 0f
        visibility = View.VISIBLE
        mySnackbarMessage.text = message
        val (initialScaleX, initialScaleY) = Pair(scaleX, scaleY)

        scaleX = initialScaleX / 2
        scaleX = initialScaleY / 2

        animate()
            .alpha(1f)
            .scaleX(initialScaleX)
            .scaleY(initialScaleY)
            .withEndAction {
                animate()
                    .alpha(0f)
                    .setStartDelay(duration)
                    .withEndAction {
                        visibility = View.GONE
                        isAnimating = false
                        nextMessage?.invoke()
                        nextMessage = null
                    }
                    .start()
            }
            .setStartDelay(0)
            .start()
    }

    fun showMessage(@StringRes resId: Int, duration: Long = LENGTH_LONG) {
        showMessage(resources.getString(resId), duration)
    }

    companion object {
        const val LENGTH_LONG = 7000L
        const val LENGTH_SHORT = 4000L
    }
}