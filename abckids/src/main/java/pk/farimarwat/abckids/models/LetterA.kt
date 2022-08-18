package pk.farimarwat.abckids.models

import android.graphics.Path

class LetterA {

    companion object {
        fun getSegments(width:Int,height:Int):Path{

            val path = Path()
            path.moveTo(width*0.19f,height*0.9f)
            path.lineTo(width*0.45f,height*0.08f)

            path.moveTo(width*0.46f,height*0.08f)
            path.lineTo(width*0.76f,height*0.88f)

            path.moveTo(width*0.32f,height*0.62f)
            path.lineTo(width*0.62f,height*0.62f)

            return path
        }
    }
}