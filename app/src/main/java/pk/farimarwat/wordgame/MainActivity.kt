package pk.farimarwat.wordgame

import android.content.Context
import android.graphics.Path
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import pk.farimarwat.abckids.AbcdkidsListener
import pk.farimarwat.abckids.TAG
import pk.farimarwat.wordgame.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var mContext:Context
    private val binding:ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        mContext = this
        val path = Path()
        val width = 420
        val height = 420
        path.moveTo(width*0.19f,height*0.9f)
        path.lineTo(width*0.45f,height*0.08f)

        path.moveTo(width*0.46f,height*0.08f)
        path.lineTo(width*0.76f,height*0.9f)

        path.moveTo(width*0.32f,height*0.62f)
        path.lineTo(width*0.62f,height*0.62f)
        binding.tlview.setLetter(path)

//        binding.tlview.setLetter("A",width,height)
        binding.tlview.addListener(object :AbcdkidsListener{
            override fun onDotTouched(progress: Float) {
                Log.e(TAG,"Progress: ${progress}")
            }

            override fun onSegmentFinished() {
                Log.e(TAG,"Segment Finished")
            }

            override fun onTraceFinished() {
                Log.e(TAG,"Tracing completed")
            }

        })

    }
}