package pk.farimarwat.abckids.models

import android.graphics.Path

class LetterU {
    companion object {
        fun getSegments(width:Int,height:Int):Path{
            val list = mutableListOf<KSegment>()
            val path = Path()
            val startx = 200f
            val startY = 50f
            val lineH = 400f
            val width = 350f
            path.moveTo(startx,startY)
            path.lineTo(startx,lineH)
            path.cubicTo(
                startx,lineH+200,
                startx+width,lineH+200,
                startx+width,lineH
            )
            path.lineTo(startx+width,startY)

            return  path
        }
    }
}