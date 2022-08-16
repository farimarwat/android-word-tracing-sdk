package pk.farimarwat.abckids.models

import android.graphics.Path

data class KSegment(
    var isaccessed: Boolean?,
    var points: MutableList<KPointF>?
) {

    fun getPrevious(point: KPointF): KPointF? {
        var kpointf: KPointF? = null
        points?.let {
            val index = getIndex(point)
            if(index > 0){
                kpointf = it[index -1]
            }
        }
        return kpointf
    }

    fun getNext(point: KPointF): KPointF? {
        var kpointf: KPointF? = null
        points?.let {
            for (i in 0 until it.size - 1) {
                val item = it[i]
                if (item.point.x == point.point.x
                    && item.point.y == point.point.y
                    && i < it.size
                ) {
                    kpointf = it[i + 1]
                    break
                }
            }
        }
        return kpointf
    }

    fun setAccess(point: KPointF) {
        points?.let {
            for (p in it) {
                if (p.point.x == point.point.x
                    && p.point.y == p.point.y
                ) {
                    p.isaccessed = true
                }
            }
        }
    }

    fun getIndex(point: KPointF): Int {
        var index = 0
        points?.let {
            for (p in it) {
                if (p.point.x == point.point.x && p.point.y == point.point.y) {
                    break
                }
                index++
            }
        }
        return index
    }

    fun getFirst():KPointF?{
        var pointf:KPointF? = null
        points?.let {
            pointf = it.first()
        }
        return pointf
    }
    fun getLast():KPointF?{
        var pointf:KPointF? = null
        points?.let {
            pointf = it.last()
        }
        return pointf
    }
    fun getTotal():Int{
        points?.let {
            return it.size
        }
        return 0
    }

    fun isSegmentAccessed(): Boolean {
        points?.let {
            for (p in it) {
                if (!p.isaccessed) {
                    return false
                }
            }
        }
        return true
    }

}