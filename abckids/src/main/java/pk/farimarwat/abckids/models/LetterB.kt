package pk.farimarwat.abckids.models

import android.graphics.Path
import android.graphics.RectF

class LetterB {

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
            var oval = RectF(marginstart+100f,marginTop,marginEnd,marginBottom/2f+20f)
            path.arcTo(oval,280f,175f)
            path.lineTo(marginstart,marginBottom/2f+20f)

            path.moveTo(marginstart,marginBottom/2f+20f)
            oval = RectF(marginstart+100f,marginBottom/2f+20f,marginEnd,marginBottom)
            path.arcTo(oval,280f,175f)
            path.lineTo(marginstart,marginBottom)

            return path
        }
    }
}