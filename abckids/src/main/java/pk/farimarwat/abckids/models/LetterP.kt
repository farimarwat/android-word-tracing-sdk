package pk.farimarwat.abckids.models

import android.graphics.Path
import android.graphics.RectF

class LetterP {

    companion object {
        fun getSegments(width:Int,height:Int):Path{
            val path = Path()
            val marginstart = width * 0.25f
            val marginEnd = width * 0.80f
            val marginTop = height * 0.1f
            val marginBottom = height * 0.9f
            path.moveTo(marginstart,marginTop)
            path.lineTo(marginstart,marginBottom)

            path.moveTo(marginstart,marginTop)
            val oval = RectF(marginstart+100f,marginTop,marginEnd,marginBottom/2f+40f)
            path.arcTo(oval,280f,175f)
            path.lineTo(marginstart,marginBottom/2f+40f)


            return path
        }
    }
}