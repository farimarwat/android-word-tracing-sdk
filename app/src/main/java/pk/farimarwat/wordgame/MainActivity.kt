package pk.farimarwat.wordgame

import android.content.Context
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
        val drawable = ContextCompat.getDrawable(mContext,R.drawable.fill)
        binding.tlview.setSegmentFillImage(drawable)
    }
}