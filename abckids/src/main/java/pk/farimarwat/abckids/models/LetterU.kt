package pk.farimarwat.abckids.models

import android.graphics.Path

class LetterU {
    companion object {
        fun getSegments(width:Int,height:Int):Path{
            val path = Path()
            val marginstart = width * 0.25f
            val marginEnd = width * 0.80f
            val marginTop = height * 0.1f
            val marginBottom = height * 0.9f
            path.moveTo(marginstart,marginTop)
            path.arcTo(marginstart,height*0.3f,marginEnd,height*0.9f,-180f,-180f,false)
            path.lineTo(marginEnd,marginTop)

            return  path
        }
    }
}