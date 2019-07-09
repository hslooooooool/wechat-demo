package qsos.core.lib.view.widget.chart

import android.graphics.Color
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate

/**@author : 华清松@description : 柱状图管理器
 */
class PieChartManager(private val mPieChart: PieChart) {

    private val mColors = arrayListOf<Int>()

    init {
        mColors.clear()
        for (c in ColorTemplate.MATERIAL_COLORS) {
            mColors.add(c)
        }
        for (c in ColorTemplate.COLORFUL_COLORS) {
            mColors.add(c)
        }
        for (c in ColorTemplate.PASTEL_COLORS) {
            mColors.add(c)
        }
    }

    private fun getColors(size: Int): List<Int> {
        val colors = arrayListOf<Int>()
        while (colors.size < size) {
            colors.addAll(mColors)
        }
        return colors
    }

    /**初始化Chart*/
    fun setPieChart(data: ArrayList<PieEntry>, centerText: String, size: Int) {
        mPieChart.setUsePercentValues(true)
        mPieChart.description.isEnabled = false
        mPieChart.setExtraOffsets(5f, 10f, 5f, 5f)

        mPieChart.dragDecelerationFrictionCoef = 0.95f
        // 设置中间文字
        mPieChart.setDrawCenterText(true)
        mPieChart.centerText = centerText
        mPieChart.isDrawHoleEnabled = true
        mPieChart.setHoleColor(Color.WHITE)

        mPieChart.setTransparentCircleColor(Color.WHITE)
        mPieChart.setTransparentCircleAlpha(110)

        // 内圈半径
        mPieChart.holeRadius = 30f
        // 内圈遮罩半径
        mPieChart.transparentCircleRadius = 40f
        //
        mPieChart.rotationAngle = 0f
        // 触摸旋转
        mPieChart.isRotationEnabled = false
        mPieChart.isHighlightPerTapEnabled = false

        val dataSet = PieDataSet(data, null)
        // 子项间隔
        dataSet.sliceSpace = 3f
        // 按下拓展距离
        dataSet.selectionShift = 5f
        dataSet.setDrawValues(false)
        dataSet.colors = getColors(size).subList(0, size)

        val mPieData = PieData(dataSet)
        mPieData.setValueFormatter(PercentFormatter())
        mPieData.setValueTextSize(10f)
        mPieData.setValueTextColor(Color.WHITE)

        mPieChart.legend.verticalAlignment = Legend.LegendVerticalAlignment.CENTER
        mPieChart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        mPieChart.legend.orientation = Legend.LegendOrientation.VERTICAL
        mPieChart.legend.setDrawInside(false)
        mPieChart.legend.xEntrySpace = 7f
        mPieChart.legend.yEntrySpace = 0f
        mPieChart.legend.textSize = 12f
        mPieChart.legend.yOffset = 0f

        mPieChart.setEntryLabelColor(Color.WHITE)
        mPieChart.setEntryLabelTextSize(10f)

        mPieChart.data = mPieData
    }

}
