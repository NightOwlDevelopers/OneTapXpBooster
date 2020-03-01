package com.smartappstudio.quickboost2

import android.animation.Animator
import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.BaseInputConnection
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.FrameLayout
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory
import java.util.*

class LottieFontViewGroup @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val views = ArrayList<View>()

    private val cursorView: LottieAnimationView by lazy { LottieAnimationView(context) }

    val sampleText = "QUICK BOOST"

    var pos = 0


    init {
        /*isFocusableInTouchMode = true
        LottieCompositionFactory.fromAsset(context, "Mobilo/BlinkingCursor.json")
            .addListener {
                cursorView.layoutParams = FrameLayout.LayoutParams(
                    *//*ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT*//*
                measuredWidth/4,measuredWidth/4
                )
                cursorView.setComposition(it)
                cursorView.repeatCount = LottieDrawable.INFINITE
                cursorView.playAnimation()
                addView(cursorView)

            }*/
        /*val sampleText="Q"
        val sampleTextIterator = sampleText.iterator()
        while (sampleTextIterator.hasNext()) {
            val letter=sampleTextIterator.next()
            Log.d("XXX",letter.toString())
            if (letter.toString() == " ") {
                addSpace()
            }
            else{
                val fileName = "Mobilo/$letter.json"
                LottieCompositionFactory.fromAsset(context, fileName)
                    .addListener {
                        addComposition(it)
                    }
            }
            Thread.sleep(200,10)
        }*/

        val letter = sampleText[0]
        val fileName = "Mobilo/$letter.json"
        LottieCompositionFactory.fromAsset(context, fileName)
            .addListener {
                addComposition(it, pos)
            }
    }

    private fun printX(pos: Int) {
        var letter = sampleText[pos]
        if (letter.equals(' ')) {
            addSpace()
            printX(pos + 1)
        } else {
            val fileName = "Mobilo/$letter.json"
            LottieCompositionFactory.fromAsset(context, fileName)
                .addListener {
                    addComposition(it, pos)
                }
        }
    }

    private fun addSpace() {
        val index = indexOfChild(cursorView)
        addView(createSpaceView(), index)
    }

    override fun addView(child: View, index: Int) {
        super.addView(child, index)
        if (index == -1) {
            views.add(child)
        } else {
            views.add(index, child)
        }
    }

    private fun removeLastView() {
        if (views.size > 1) {
            val position = views.size - 2
            removeView(views[position])
            views.removeAt(position)
        }
    }

    /*override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (views.isEmpty()) {
            return
        }
        var currentX = paddingTop
        var currentY = paddingLeft

        for (i in views.indices) {
            val view = views[i]
            if (!fitsOnCurrentLine(currentX, view)) {
                if (view.tag != null && view.tag == "Space") {
                    continue
                }
                currentX = paddingLeft
                currentY += view.measuredHeight
            }
            currentX += view.width
        }

        setMeasuredDimension(measuredWidth, currentY + views[views.size - 1].measuredHeight * 2)
    }*/

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (views.isEmpty()) {
            return
        }
        var currentX = paddingTop
        var currentY = paddingLeft

        for (i in views.indices) {
            val view = views[i]
            if (!fitsOnCurrentLine(currentX, view)) {
                if (view.tag != null && view.tag == "Space") {
                    continue
                }
                /*if(view.tag == "Space"){
                    currentX = paddingLeft+100
                    currentY += view.measuredHeight
                    continue
                }*/
                else {
                    currentX = paddingLeft
                    currentY += view.measuredHeight
                }
            } else {
                if (view.tag == "Space") {
                    System.err.println("SPACEX")
                    currentX = paddingLeft + 250
                }
            }
            view.layout(
                currentX, currentY, currentX + 50 + view.measuredWidth,
                currentY + view.measuredHeight
            )
            currentX += view.measuredWidth - 50
        }
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
        val fic = BaseInputConnection(this, false)
        outAttrs.actionLabel = null
        outAttrs.inputType = InputType.TYPE_NULL
        outAttrs.imeOptions = EditorInfo.IME_ACTION_NEXT
        return fic
    }

    override fun onCheckIsTextEditor(): Boolean {
        return true
    }


    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_SPACE) {
            addSpace()
            return true
        }

        if (keyCode == KeyEvent.KEYCODE_DEL) {
            removeLastView()
            return true
        }

        if (!isValidKey(event)) {
            return super.onKeyUp(keyCode, event)
        }


        val letter = "" + Character.toUpperCase(event.unicodeChar.toChar())
        // switch (letter) {
        //     case ",":
        //         letter = "Comma";
        //         break;
        //     case "'":
        //         letter = "Apostrophe";
        //         break;
        //     case ";":
        //     case ":":
        //         letter = "Colon";
        //         break;
        // }
        val fileName = "Mobilo/$letter.json"
        /*LottieCompositionFactory.fromAsset(context, fileName)
            .addListener { addComposition(it) }*/

        return true
    }

    private fun isValidKey(event: KeyEvent): Boolean {
        if (!event.hasNoModifiers()) {
            return false
        }
        if (event.keyCode >= KeyEvent.KEYCODE_A && event.keyCode <= KeyEvent.KEYCODE_Z) {
            return true
        }

        // switch (keyCode) {
        //     case KeyEvent.KEYCODE_COMMA:
        //     case KeyEvent.KEYCODE_APOSTROPHE:
        //     case KeyEvent.KEYCODE_SEMICOLON:
        //         return true;
        // }
        return false
    }

    private fun addComposition(composition: LottieComposition, pos: Int) {
        val lottieAnimationView = LottieAnimationView(context)
        lottieAnimationView.layoutParams = FrameLayout.LayoutParams(
            measuredWidth / 4,
            measuredWidth / 4

        )
        lottieAnimationView.setComposition(composition)
        lottieAnimationView.speed = 3.0F
        //lottieAnimationView.repeatCount=LottieDrawable.RESTART
        lottieAnimationView.playAnimation()
        lottieAnimationView.addAnimatorListener(object :
            Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                Log.e("Animation:", "start")
            }

            override fun onAnimationEnd(animation: Animator) {
                Log.e("Animation:", "end")
                if (pos < sampleText.length - 1) printX(pos + 1)
            }

            override fun onAnimationCancel(animation: Animator) {
                Log.e("Animation:", "cancel")
            }

            override fun onAnimationRepeat(animation: Animator) {
                Log.e("Animation:", "repeat")
            }
        })
        val index = indexOfChild(cursorView)
        addView(lottieAnimationView, index)
    }

    private fun fitsOnCurrentLine(currentX: Int, view: View): Boolean {
        return currentX + view.measuredWidth < width - paddingRight
    }

    private fun createSpaceView(): View {
        val spaceView = View(context)
        spaceView.layoutParams = FrameLayout.LayoutParams(
            /*resources.getDimensionPixelSize(R.dimen.font_space_width),*/
            measuredWidth / 2,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            1
        )
        spaceView.tag = "Space"
        return spaceView
    }

}
