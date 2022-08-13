package pk.farimarwat.abckids

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import pk.farimarwat.abckids.models.KSegment
import pk.farimarwat.abckids.models.LetterA
import pk.farimarwat.abckids.models.LetterU


const val TAG = "abckids"
class TracingLetterView (context:Context,attrs:AttributeSet):View(context,attrs) {
    private var mTa:TypedArray
    private var mCanvasSize = 400
    private var mSizeSegment = 60f
    private val mPaintSegBorder = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 60f
    }
    private var mPathSegBorder = Path()

    private val mPaintSegBackground = Paint().apply {
        isAntiAlias = true

        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND

    }
    private var mPathSegBackground = Path()

    private val mPaintSegFill = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private var mPathSegFill= Path()
    private val mPaintSegDot = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    private val mSegDotRadius = 10f
    private val mSegDotDetectRadius = 50f
    private val mListSegments = mutableListOf<KSegment>()
    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        mTa = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.TracingLetterView,0,0
        )
        initSegment(context,mTa)
    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mCanvasSize = Math.min(measuredWidth,measuredHeight)
        setMeasuredDimension(mCanvasSize,mCanvasSize)
    }
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawColor(Color.GRAY)
        setLetter(LetterA.getSegments(width,height),canvas)
        canvas?.drawBitmap(createSegBorder(width,height),0f,0f,null)
        canvas?.drawBitmap(createSegBackground(width,height),0f,0f,null)
    }
    private fun initSegment(context:Context,ta:TypedArray){
        val colorSegBorder = ta.getColor(R.styleable.TracingLetterView_tlv_segmentbordercolor,0)
        if(colorSegBorder != 0){
            mPaintSegBorder.color = colorSegBorder
        } else {
            mPaintSegBorder.color = ContextCompat.getColor(context,R.color.segmentborder)
        }

        val colorSegBackground = ta.getColor(R.styleable.TracingLetterView_tlv_segmentbackgroundcolor,0)
        if(colorSegBackground != 0){
            mPaintSegBackground.color = colorSegBackground
        } else {
            mPaintSegBackground.color = ContextCompat.getColor(context,R.color.segmentbackground)
        }

        val colorSegFill = ta.getColor(R.styleable.TracingLetterView_tlv_segmentfillcolor,0)
        if(colorSegFill != 0){
            mPaintSegFill.color = colorSegFill
        } else {
            mPaintSegFill.color = ContextCompat.getColor(context,R.color.segmentfill)
        }

        val colorSegDot = ta.getColor(R.styleable.TracingLetterView_tlv_segmentdot,0)
        if(colorSegDot != 0){
            mPaintSegDot.color = colorSegDot
        } else {
            mPaintSegDot.color = ContextCompat.getColor(context,R.color.segmentdot)
        }

        mSizeSegment = ta.getFloat(R.styleable.TracingLetterView_tlv_segmentsize, SEG_SIZE_DEFAULT)
        mPaintSegBorder.strokeWidth = mSizeSegment
        mPaintSegBackground.strokeWidth = mSizeSegment - 20f
        mPaintSegFill.strokeWidth = mSizeSegment - 20f

        ta.recycle()
    }

    fun createSegBorder(width:Int,height:Int):Bitmap {
        val bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawPath(mPathSegBorder, mPaintSegBorder)
        return bitmap
    }

    fun createSegBackground(width:Int,height: Int):Bitmap {
        val bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawPath(mPathSegBackground, mPaintSegBackground)
        return bitmap
    }

    fun drawSegFill(canvas: Canvas?) {
        canvas?.drawPath(mPathSegFill, mPaintSegFill)
    }
    fun drawLetter(canvas: Canvas?){
        val rectbitmap = RectF(0f,0f,width.toFloat(),height.toFloat())
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val bitmap = BitmapFactory.decodeResource(resources,R.drawable.letter)
        canvas?.drawBitmap(bitmap,null,rectbitmap,paint)


    }
    fun setLetter(path:Path,canvas: Canvas?){
        canvas?.drawPath(path,mPaintSegBorder)
        canvas?.drawPath(path,mPaintSegBackground)
        val paths = pathsFromComplexPath(path)
        paths?.let { list ->
            for(p in list){
                val dots = getPoints(p)
                for(d in dots){
                    canvas?.drawCircle(d.x,d.y,mSegDotRadius,mPaintSegDot)
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when(it.action){
                MotionEvent.ACTION_DOWN ->{
                    Log.e(TAG,"X: ${it.x} Y: ${it.y}")
                }
                MotionEvent.ACTION_MOVE ->{
                    Log.e(TAG,"X: ${it.x} Y: ${it.y}")
                }
                else -> {}
            }
        }
        return true
    }

    private fun isCircleTouched(
        touchX: Float,
        touchY: Float,
        centerX: Float,
        centerY: Float,
        r: Float
    ): Boolean {
        val x = touchX - centerX
        val y = touchY - centerY
        return touchX > centerX && Math.sqrt((x * x + y * y).toDouble()) < r
    }
    fun pathsFromComplexPath(p: Path?): List<Path>? {
        val pathList: MutableList<Path> = ArrayList()
        val pm = PathMeasure(p, false)
        var fin = false
        while (!fin) {
            val len = pm.length
            if (len > 0) {
                val np = Path()
                pm.getSegment(0f, len, np, true)
                pathList.add(np)
            }
            fin = !pm.nextContour()
        }
        return pathList
    }
    private fun getPoints(path: Path): MutableList<PointF> {
        val list = mutableListOf<PointF>()
        val pm = PathMeasure(path, false)
        val amoutofpoints = pm.length/70f
        val aCoordinates = floatArrayOf(0f,0f)
        var i = 0.0f
        while (i < 1.0) {
            pm.getPosTan(pm.length * i, aCoordinates, null)
            i += 1f/amoutofpoints
            val point = PointF()
            point.x = aCoordinates.get(0)
            point.y = aCoordinates.get(1)
            list.add(point)
        }
        return list
    }
    companion object {
        val SEG_SIZE_DEFAULT = 80f


    }
}