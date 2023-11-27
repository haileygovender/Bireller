package com.example.birellerapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class WaveFormView (context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var paint= Paint()
    private var amplitude=ArrayList<Float>()
    private var spikes=ArrayList<RectF>()

    private var radius=6f
    private var w =9f
    private var d=6f
    private var screenWidth=0f
    private var screenHeight=400f
    private var maxSpikes=0


    init{
        paint.color= Color.rgb(244,81,40)
        screenWidth=resources.displayMetrics.widthPixels.toFloat()
        maxSpikes=(screenWidth/(w+d)).toInt()
    }
    fun addAmplitude(amp:Float)
    {
        var norm =Math.min(amp.toInt()/7,400).toFloat()
        amplitude.add(norm)  //displays on the screen


        spikes.clear()
        var amps : List<Float> = amplitude.takeLast(maxSpikes)
        for (i:Int in amps.indices){
            var left =screenWidth-i*(w+d)
            var top=screenHeight/2 - amps[i]/2
            var right:Float =left + w
            var bottom :Float =top+amps[i]
            spikes.add(RectF(left,top,right,bottom))

        }

        invalidate()
    }

    fun clear(): ArrayList<Float>{
        var amps =amplitude.clone() as ArrayList<Float>
        amplitude.clear()
        spikes.clear()
        invalidate()

        return amps
    }

    override fun draw(canvas: Canvas)
    {
        super.draw(canvas)
        spikes.forEach {
            canvas?.drawRoundRect(it, radius, radius, paint)
        }
        //canvas?.drawRoundRect(RectF(60f,60f,60+80f,60F+360f),6f,6f,paint) change dimensions of shape
    }



}