package pk.farimarwat.abckids

import android.graphics.PointF

interface AbcdkidsListener {
    fun onPrepared(points:List<PointF>)
    fun onDotTouched(progress:Float)
    fun onSegmentFinished()
    fun onTraceFinished()
}