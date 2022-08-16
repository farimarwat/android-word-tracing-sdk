package pk.farimarwat.abckids

interface AbcdkidsListener {
    fun onDotTouched(progress:Float)
    fun onSegmentFinished()
    fun onTraceFinished()
}