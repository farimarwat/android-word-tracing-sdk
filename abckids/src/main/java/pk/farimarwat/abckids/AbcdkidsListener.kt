package pk.farimarwat.abckids

import android.graphics.PointF

interface AbcdkidsListener {

    fun onDotTouched(progress:Float)
    fun onSegmentFinished()
    fun onTraceFinished()
}