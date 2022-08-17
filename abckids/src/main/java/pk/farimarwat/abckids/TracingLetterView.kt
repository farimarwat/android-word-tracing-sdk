package pk.farimarwat.abckids

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import pk.farimarwat.abckids.models.KPointF
import pk.farimarwat.abckids.models.KSegment
import pk.farimarwat.abckids.models.LetterA
import pk.farimarwat.abckids.models.LetterU
import kotlin.math.log


const val TAG = "abckids"

class TracingLetterView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var mListener: AbcdkidsListener? = null
    private var mTa: TypedArray
    private var mCanvasSize = 400
    private var mSizeSegment = 60f
    private val mPaint = Paint().apply {
        isAntiAlias = true
    }
    private val mPaintSpecific = Paint().apply {
        isAntiAlias = true
    }

    private var mSegBorderStrokeSize = 0f
    private var mSegBorderColor = 0
    private var mPathSegBorder = Path()

    private var mSegBackgroundStrokeSize = 0f
    private var mSegBackgroundColor = 0
    private var mPathSegBackground = Path()

    private var mSegFillStrokeSize = 0f
    private var mSegFillColor = 0
    private var mFillBitmapShader: BitmapShader? = null
    private var mPathSegFill = Path()
    private val mPaintSegDot = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    private val mSegDotRadius = 10f
    private val mSegDotDetectRadius = 50f
    private val mListSegments = mutableListOf<KSegment>()
    private var mTracingCompleted = false
    private var mActiveSegment: KSegment? = null
    private var mActiveSegmentIndex: Int = 0
    private var mCanMove = false
    private var mPorterDuff_SRC_ATOP = PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP)
    private var mPorterDuff_DST_ATOP = PorterDuffXfermode(PorterDuff.Mode.DST_ATOP)
    private var mIndicatorBitmap: Bitmap? = null
    private var mShowIndicator = true
    private var mIndicatorPoint:PointF? = null
    private var mIndicatorValueAnimator:ValueAnimator? = null

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        mTa = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.TracingLetterView, 0, 0
        )
        initSegment(context, mTa)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mCanvasSize = Math.min(measuredWidth, measuredHeight)
        setMeasuredDimension(mCanvasSize, mCanvasSize)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mPaint.xfermode = null
        setLetter(LetterA.getSegments(width, height))
        canvas?.drawBitmap(createSegBackground(width, height), 0f, 0f, mPaint)
        mPaint.xfermode = mPorterDuff_SRC_ATOP
        canvas?.drawBitmap(createSegFill(width, height), 0f, 0f, mPaint)
        mPaint.xfermode = mPorterDuff_DST_ATOP
        canvas?.drawBitmap(createSegBorder(width, height), 0f, 0f, mPaint)

        if(mShowIndicator){
            mIndicatorBitmap?.let { ibitmap ->
                mIndicatorPoint?.let { ipoint ->
                    canvas?.drawBitmap(ibitmap,ipoint.x,ipoint.y,null)
                }
            }
        }
        drawUnaccessedSegment(canvas, mListSegments)
    }

    private fun initSegment(context: Context, ta: TypedArray) {
        val colorSegBorder = ta.getColor(R.styleable.TracingLetterView_tlv_segmentbordercolor, 0)
        if (colorSegBorder != 0) {
            mSegBorderColor = colorSegBorder
        } else {
            mSegBorderColor = ContextCompat.getColor(context, R.color.segmentborder)
        }

        val colorSegBackground =
            ta.getColor(R.styleable.TracingLetterView_tlv_segmentbackgroundcolor, 0)
        if (colorSegBackground != 0) {
            mSegBackgroundColor = colorSegBackground
        } else {
            mSegBackgroundColor = ContextCompat.getColor(context, R.color.segmentbackground)
        }

        val colorSegFill = ta.getColor(R.styleable.TracingLetterView_tlv_segmentfillcolor, 0)
        if (colorSegFill != 0) {
            mSegFillColor = colorSegFill
        } else {
            mSegFillColor = ContextCompat.getColor(context, R.color.segmentfill)
        }

        val fillimagedrawable = ta.getDrawable(R.styleable.TracingLetterView_tlv_segmentfillimage)
        fillimagedrawable?.let {
            var bitmap = it.toBitmap()
            bitmap = Bitmap.createScaledBitmap(bitmap, 60, 60, false)
            mFillBitmapShader = BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
        }
        val colorSegDot = ta.getColor(R.styleable.TracingLetterView_tlv_segmentdot, 0)
        if (colorSegDot != 0) {
            mPaintSegDot.color = colorSegDot
        } else {
            mPaintSegDot.color = ContextCompat.getColor(context, R.color.segmentdot)
        }

        mSizeSegment = ta.getFloat(R.styleable.TracingLetterView_tlv_segmentsize, SEG_SIZE_DEFAULT)
        mSegBorderStrokeSize = mSizeSegment
        mSegBackgroundStrokeSize = mSizeSegment - 20f
        mSegFillStrokeSize = mSizeSegment

        val indicator = ta.getDrawable(R.styleable.TracingLetterView_tlv_indicator)
        if(indicator != null){
            val ind_bitmapt = indicator.toBitmap()
            mIndicatorBitmap = Bitmap.createScaledBitmap(ind_bitmapt,60,60,false)
        } else {
            mIndicatorBitmap = BitmapFactory.decodeResource(resources,R.drawable.hand)
        }
        ta.recycle()
    }

    fun createSegBorder(width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            color = mSegBorderColor
            strokeWidth = mSegBorderStrokeSize
        }
        canvas.drawPath(mPathSegBorder, paint)
        return bitmap
    }

    fun createSegBackground(width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            color = mSegBackgroundColor
            strokeWidth = mSegBackgroundStrokeSize
        }
        canvas.drawPath(mPathSegBackground, paint)
        return bitmap
    }

    fun createSegFill(width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeWidth = mSegFillStrokeSize
        }
        if (mFillBitmapShader != null) {
            paint.shader = mFillBitmapShader
        } else {
            paint.color = mSegFillColor
        }
        canvas.drawPath(mPathSegFill, paint)
        return bitmap
    }

    fun setLetter(path: Path) {
        if (mListSegments.isEmpty()) {
            mPathSegBackground = path
            mPathSegBorder = path
            Log.e(TAG,"Setting Letter")
            val paths = pathsFromComplexPath(path)
            paths?.let { list_paths ->
                for (p in list_paths) {
                    val pm = PathMeasure(path, false)
                    val startpoint = getPoint(pm, 0f)
                    val endPoint = getPoint(pm, pm.length)
                    val points = getPoints(p)
                    val kpoints = mutableListOf<KPointF>()
                    for (point in points) {
                        kpoints.add(
                            KPointF(false, point)
                        )
                    }
                    mListSegments.add(
                        KSegment(false, kpoints, startpoint, endPoint)
                    )
                }
            }
        }
    }

    fun drawUnaccessedSegment(canvas: Canvas?, segments: MutableList<KSegment>) {
        if (segments.isNotEmpty()
            && (!mTracingCompleted)
        ) {
            if(mActiveSegment == null){
                var counter = 0
                for (seg in segments) {
                    if (!seg.isaccessed!!) {
                        mActiveSegment = seg
                        mActiveSegmentIndex = counter
                        mShowIndicator = true
                        showIndicator()
                        break
                    }
                    counter++
                }
            }
            mActiveSegment?.points?.let {
                for (d in it) {
                    canvas?.drawCircle(d.point.x, d.point.y, mSegDotRadius, mPaintSegDot)
                }
            }

        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            event
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    mShowIndicator = false
                    mIndicatorValueAnimator?.cancel()
                    mActiveSegment?.points?.let { list ->
                        for (point in list) {
                            val istouched = isCircleTouched(
                                event.x, event.y,
                                point.point.x, point.point.y,
                                mSegDotDetectRadius
                            )
                            if (istouched) {
                                mShowIndicator = false
                                //setting progress
                                mActiveSegment?.let { seg ->
                                    val total = seg.getTotal()
                                    val current = seg.getIndex(point).plus(1)
                                    mListener?.onDotTouched(
                                        (current * total) / 100f
                                    )
                                }

                                if (mActiveSegment?.getPrevious(point)?.isaccessed == true
                                    || mActiveSegment?.getPrevious(point) == null
                                ) {
                                    mCanMove = true
                                    mActiveSegment?.let {
                                        val first = it.getFirst()
                                        first?.let { kpoint ->
                                            mPathSegFill.moveTo(kpoint.point.x, kpoint.point.y)
                                        }
                                    }
//
                                    mActiveSegment?.setAccess(point)
                                    invalidate()
                                }
                                break
                            }
                        }
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    mActiveSegment?.points?.let { list ->
                        for (point in list) {
                            val istouched = isCircleTouched(
                                event.x, event.y, point.point.x, point.point.y, mSegDotDetectRadius
                            )
                            val previous = mActiveSegment?.getPrevious(point)
                            if (istouched
                                && mCanMove
                                && previous?.isaccessed == true
                            ) {
                                //setting progress
                                mActiveSegment?.let { seg ->
                                    val total = seg.getTotal().toFloat()
                                    val current = seg.getIndex(point).plus(1).toFloat()
                                    val progress = ((current / total) * 100f)
                                    mListener?.onDotTouched(
                                        progress
                                    )
                                    if (progress >= 100.0f) {
                                        mListSegments[mActiveSegmentIndex].isaccessed = true
                                        mListener?.onSegmentFinished()
                                        mCanMove = false
                                        mActiveSegment = null

                                        mPathSegFill.lineTo(point.point.x, point.point.y)
                                        if (mActiveSegmentIndex == mListSegments.size - 1) {
                                            mTracingCompleted = true
                                            mListener?.onTraceFinished()
                                            mActiveSegment = null
                                        }
                                    }

                                }

                                mPathSegFill.lineTo(point.point.x, point.point.y)
                                mActiveSegment?.setAccess(point)
//
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

    //attributes
    fun setSegmentFillImage(image: Drawable?) {
        image?.let {
            var bitmap = it.toBitmap()
            bitmap = Bitmap.createScaledBitmap(bitmap, 60, 60, false)
            mFillBitmapShader = BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
        }
    }

    //
    fun addListener(listener: AbcdkidsListener) {
        mListener = listener
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
        val amoutofpoints = pm.length / 60f
        val aCoordinates = floatArrayOf(0f, 0f)
        var i = 0.0f
        while (i < 1.0) {
            pm.getPosTan(pm.length * i, aCoordinates, null)
            i += 1.0f / amoutofpoints
            val point = PointF()
            point.x = aCoordinates[0]
            point.y = aCoordinates[1]
            list.add(point)
        }

        return list
    }

    fun getPoint(pm: PathMeasure, length: Float): PointF {
        val pointf = PointF(0f, 0f)
        val coordinates = floatArrayOf(0f, 0f)

        pm.getPosTan(length, coordinates, null)
        pointf.x = coordinates[0]
        pointf.y = coordinates[1]
        return pointf
    }

    fun showIndicator() {
       if(mShowIndicator){
           mActiveSegment?.let { kSegment ->
               kSegment.points?.let { points ->
                   mIndicatorValueAnimator = ValueAnimator.ofInt(0, points.size -1)
                   mIndicatorValueAnimator?.apply {
                       duration = 2000
                       repeatCount = 3
                   }
                   mIndicatorValueAnimator?.addUpdateListener {
                      mIndicatorPoint = points[mIndicatorValueAnimator?.animatedValue as Int]
                          .point

                       invalidate()
                   }
                   mIndicatorValueAnimator?.start()
               }
           }
       } else {
           mIndicatorValueAnimator?.cancel()
       }
    }

    companion object {
        val SEG_SIZE_DEFAULT = 120f
    }
}