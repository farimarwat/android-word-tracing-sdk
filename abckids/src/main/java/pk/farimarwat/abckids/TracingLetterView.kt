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
import pk.farimarwat.abckids.models.KPointF
import pk.farimarwat.abckids.models.KSegment
import pk.farimarwat.abckids.models.LetterA
import pk.farimarwat.abckids.models.LetterU


const val TAG = "abckids"
class TracingLetterView (context:Context,attrs:AttributeSet):View(context,attrs) {
    private var mTa:TypedArray
    private var mCanvasSize = 400
    private var mSizeSegment = 60f
    private val mPaint:Paint
    private val mPaintSegBorder = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
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
    private var mActiveSegment:KSegment? = null
    private var mActiveSegmentIndex:Int = 0
    private var mCanMove = false
    private var mPorterDuff_SRC_ATOP = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)
    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        mPaint = Paint().apply {
            style = Paint.Style.FILL_AND_STROKE
        }
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
        mPaint.xfermode = null
        setLetter(LetterU.getSegments(width,height),canvas)
        canvas?.drawBitmap(createSegBorder(width,height),0f,0f,mPaint)
        canvas?.drawBitmap(createSegBackground(width,height),0f,0f,mPaint)
        mPaint.xfermode = mPorterDuff_SRC_ATOP
        canvas?.drawBitmap(createSegFill(width,height),0f,0f,mPaint)
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

    fun createSegFill(width: Int,height: Int):Bitmap {
        val bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        canvas.drawPath(mPathSegFill, mPaintSegFill)
        return bitmap
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
        paths?.let { list_paths ->
            for(p in list_paths){
                val points = getPoints(p)
                val kpoints = mutableListOf<KPointF>()
                for(point in points){
                    kpoints.add(
                        KPointF(false,point)
                    )
                }
                mListSegments.add(
                    KSegment(false,kpoints)
                )
            }
        }
        drawUnaccessedSegment(canvas,mListSegments)
    }
    fun drawUnaccessedSegment(canvas:Canvas?,segments:MutableList<KSegment>){
        if(segments.isNotEmpty()){
            var counter = 0
            for(seg in segments){
               if(!seg.isaccessed!!) {
                   mActiveSegment = seg
                   mActiveSegmentIndex = counter
                   break
               }
                counter++
            }
            mActiveSegment?.points?.let {
                for(d in it){
                    canvas?.drawCircle(d.point.x,d.point.y,mSegDotRadius,mPaintSegDot)
                }
            }
        }
    }
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let { event

            when(event.action){
                MotionEvent.ACTION_DOWN ->{
                    mActiveSegment?.points?.let { list ->
                        for(point in list){
                            val istouched = isCircleTouched(
                                event.x,event.y,
                                point.point.x,point.point.y,
                                mSegDotDetectRadius
                            )
                            if(istouched){
                                if(mActiveSegment?.getPrevious(point)?.isaccessed == true
                                    || mActiveSegment?.getPrevious(point) == null
                                ){
                                    mCanMove = true
                                    mPathSegFill.moveTo(point.point.x,point.point.y)
                                    val next = mActiveSegment?.getNext(point)
                                    next?.let {
                                        mActiveSegment?.setAccess(it)
                                        mPathSegFill.lineTo(it.point.x,it.point.y)
                                    }
                                    mActiveSegment?.setAccess(point)
                                    invalidate()
                                }
                                break
                            }

                        }
                    }
                }
                MotionEvent.ACTION_MOVE ->{
                    mActiveSegment?.points?.let { list ->
                        for(point in list){
                            val istouched = isCircleTouched(
                                event.x,event.y,point.point.x,point.point.y,mSegDotDetectRadius
                            )
                            if(istouched && mCanMove){
                                Log.e(TAG,"Index: ${mActiveSegment?.getIndex(point)}")
                                mPathSegFill.lineTo(point.point.x,point.point.y)
                                mActiveSegment?.setAccess(point)

                                if(mActiveSegment?.isSegmentAccessed() == true){
                                    mListSegments[mActiveSegmentIndex].isaccessed = true
                                }
                                invalidate()
                                break
                            }

                        }
                    }
                }
                MotionEvent.ACTION_UP -> {
                    mCanMove = false
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
        while (i < 1.1) {
            pm.getPosTan(pm.length * i, aCoordinates, null)
            i += 1f/amoutofpoints
            val point = PointF()
            point.x = aCoordinates[0]
            point.y = aCoordinates[1]
            list.add(point)
        }
        return list
    }
    companion object {
        val SEG_SIZE_DEFAULT = 100f
    }
}