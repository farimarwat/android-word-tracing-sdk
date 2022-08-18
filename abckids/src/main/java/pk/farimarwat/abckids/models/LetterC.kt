package pk.farimarwat.abckids.models

import android.graphics.Path
import android.graphics.RectF

class LetterC {

    companion object {
        fun getSegments(width:Int,height:Int):Path{

            val path = Path()
            val marginstart = width * 0.10f
            val marginEnd = width * 0.90f
            val marginTop = height * 0.1f
            val marginBottom = height * 0.9f

            val oval = RectF(marginstart,marginTop,marginEnd,marginBottom)
            path.addArc(oval,290f, -210f)

            return path
        }
    }
}