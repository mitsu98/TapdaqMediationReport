package id.axlyody.tapdaqmediationreport.utils

import android.content.Context
import android.util.TypedValue
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import smartadapter.Position
import smartadapter.SmartRecyclerAdapter
import kotlin.math.max

private class GridAutoLayoutManager : GridLayoutManager {
    private var mColumnWidth: Int = 0
    private var mColumnWidthChanged = true
    private var mWidthChanged = true
    private var mWidth: Int = 0

    constructor(context: Context, columnWidth: Int) : super(context, 1) {
        setColumnWidth(checkedColumnWidth(context, columnWidth))
    }/* Initially set spanCount to 1, will be changed automatically later. */

    constructor(
        context: Context, columnWidth: Int, orientation: Int, reverseLayout: Boolean
    ) : super(context, 1, orientation, reverseLayout) {
        setColumnWidth(checkedColumnWidth(context, columnWidth))
    }/* Initially set spanCount to 1, will be changed automatically later. */

    private fun checkedColumnWidth(context: Context, colWidth: Int): Int {
        var columnWidth = colWidth
        columnWidth = if (columnWidth <= 0) {
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                sColumnWidth.toFloat(),
                context.resources.displayMetrics
            ).toInt()
        } else {
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, columnWidth.toFloat(), context.resources.displayMetrics
            ).toInt()
        }
        return columnWidth
    }

    private fun setColumnWidth(newColumnWidth: Int) {
        if (newColumnWidth > 0 && newColumnWidth != mColumnWidth) {
            mColumnWidth = newColumnWidth
            mColumnWidthChanged = true
        }
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
        val width = width
        val height = height

        if (width != mWidth) {
            mWidthChanged = true
            mWidth = width
        }

        if (mColumnWidthChanged && mColumnWidth > 0 && width > 0 && height > 0 || mWidthChanged) {
            val totalSpace: Int = if (orientation == LinearLayoutManager.VERTICAL) {
                width - paddingRight - paddingLeft
            } else {
                height - paddingTop - paddingBottom
            }
            val spanCount = max(1, totalSpace / mColumnWidth)
            setSpanCount(spanCount)
            mColumnWidthChanged = false
            mWidthChanged = false
        }
        super.onLayoutChildren(recycler, state)
    }

    companion object {
        private const val sColumnWidth = 180 // assume cell width of 200dp
    }
}

fun gridAutoLayout(recyclerView: RecyclerView, context: Context): RecyclerView.LayoutManager {
    return GridAutoLayoutManager(context, 160).apply {
        spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Position): Int {
                return if ((recyclerView.adapter as SmartRecyclerAdapter).getItem(position) is String) spanCount else 1
            }
        }
    }
}