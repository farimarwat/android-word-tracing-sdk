package pk.farimarwat.abckids.models

import android.graphics.Path
import android.graphics.RectF

class LetterD {

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
            val oval = RectF(width/4f,marginTop,marginEnd,marginBottom)
            path.arcTo(oval,270f,180f)
            path.lineTo(marginstart,marginBottom)

            return path
        }
    }
}