package pk.farimarwat.abckids.models

import android.graphics.Path
import android.graphics.RectF

class LetterF {

    companion object {
        fun getSegments(width:Int,height:Int):Path{
            val path = Path()
            val marginstart = width * 0.10f + 100f
            val marginEnd = width * 0.80f
            val marginTop = height * 0.1f
            val marginBottom = height * 0.9f

            path.moveTo(marginstart,marginTop)
            path.lineTo(marginstart ,marginBottom)

            path.moveTo(marginstart,marginTop)
            path.lineTo(marginEnd,marginTop)

            path.moveTo(marginstart,(marginBottom+marginTop)/2f)
            path.lineTo(marginEnd-60f,(marginBottom+marginTop)/2f)

            return path
        }
    }
}