package qsos.core.lib.view.widget.chart

import android.graphics.Color
import android.graphics.Typeface
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import kotlin.math.roundToInt

/**@author : 华清松@description : 柱状图管理器
 */
class BarChartManager(private val mBarChart: BarChart) {

    inner class YAxisValueFormatter : ValueFormatter()

    inner class XAxisValueFormatter(
            private val mTags: ArrayList<String>
    ) : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            val index = value.roundToInt()
            return if (index < 0 || index >= mTags.size) "" else mTags[index]
        }
    }

    /**初始化LineChart
     */
    private fun initLineChart() {
        // 背景颜色
        mBarChart.setBackgroundColor(Color.WHITE)
        // 是否显示网格背景
        mBarChart.setDrawGridBackground(false)
        // 显示每条背景阴影
        mBarChart.setDrawBarShadow(false)
        // 设置图表边框的颜色
        mBarChart.setBorderColor(Color.LTGRAY)
        // mBarChart.setHighlightFullBarEnabled(false);
        // 所有触摸事件,默认true
        mBarChart.setTouchEnabled(false)
        // 可拖动,默认true
        mBarChart.isDragEnabled = true
        // 两个轴上的缩放,X,Y分别默认为true
        mBarChart.setScaleEnabled(false)
        // X轴上的缩放,默认true
        mBarChart.isScaleXEnabled = false
        // Y轴上的缩放,默认true
        mBarChart.isScaleYEnabled = false
        // X,Y轴同时缩放，false则X,Y轴单独缩放,默认false
        mBarChart.setPinchZoom(false)
        // 双击缩放,默认true
        mBarChart.isDoubleTapToZoomEnabled = false
        // 抬起手指，继续滑动,默认true
        mBarChart.isDragDecelerationEnabled = false
        // 显示图表边界线
        mBarChart.setDrawBorders(false)
        // 设置XY动画效果
//        mBarChart.animateY(1000, Easing.Linear)
//        mBarChart.animateX(1000, Easing.Linear)
        // 显示描述信息
        mBarChart.description.isEnabled = false
        // 图例设置
        val legend = mBarChart.legend
        // 显示图例
        legend.form = Legend.LegendForm.NONE
        // 图例文字的大小
        legend.textSize = 9f
        // 显示位置
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        // 排列方向
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        // 设置绘制在内部
        legend.setDrawInside(false)
        /**XY轴的设置*/
        // X轴设置显示位置
        mBarChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        // X轴最小间距
        mBarChart.xAxis.granularity = 1f
        // 绘制网格线
        mBarChart.xAxis.setDrawGridLines(false)
        // X轴字体样式
        mBarChart.xAxis.typeface = Typeface.DEFAULT_BOLD
        mBarChart.xAxis.textColor = Color.GRAY
        mBarChart.xAxis.axisLineColor = Color.GRAY
        // 设置X轴文字居中对齐
        mBarChart.xAxis.setCenterAxisLabels(true)

        // 保证Y轴从0开始，不然会上移一点
        mBarChart.axisLeft.setDrawGridLines(false)
        mBarChart.axisRight.axisMinimum = 0f
        mBarChart.axisLeft.axisMinimum = 0f
        // Y轴字体样式
        mBarChart.axisLeft.typeface = Typeface.DEFAULT_BOLD
        mBarChart.axisLeft.textColor = Color.GRAY
        mBarChart.axisLeft.axisLineColor = Color.GRAY
        // 右侧Y轴不显示
        mBarChart.axisRight.isEnabled = false
    }

    /**展示竖向柱状图(一条)*/
    fun showBarChart(mValues: ArrayList<BarEntry>, xTags: ArrayList<String>, color: Int) {
        initLineChart()
        val dataSets = ArrayList<IBarDataSet>()
        // 每一个BarDataSet代表一类柱状图
        mValues.forEach { barEntry ->
            val barDataSet = BarDataSet(listOf(barEntry), "")
            barDataSet.color = color
            // 是否显示顶部的值
            barDataSet.setDrawValues(true)
            // 文字的大小
            barDataSet.valueTextSize = 11f
            barDataSet.formLineWidth = 1f
            barDataSet.formSize = 10.0f
            dataSets.add(barDataSet)
        }
        val data = BarData(dataSets)
        // 设置宽度
        data.barWidth = 0.3f
        // 是否显示X轴标签
        mBarChart.xAxis.setDrawLabels(true)
        // 设置X轴标签数
        mBarChart.xAxis.setLabelCount(mValues.size + 1, true)
        val xAxisFormatter = XAxisValueFormatter(xTags)
        mBarChart.xAxis.valueFormatter = xAxisFormatter

        // 设置左边Y轴刻度线
        val custom = YAxisValueFormatter()
        mBarChart.axisLeft.valueFormatter = custom
        // mBarChart.axisLeft .setLabelCount(yValues.length + 1, false);
        // 设置Y轴的最小值和最大值
        mBarChart.axisLeft.axisMaximum = 100f
        mBarChart.axisLeft.axisMinimum = 10f
        mBarChart.data = data
    }

    /**展示水平柱状图(一条)*/
    fun showHorizontalBarChart(mValues: ArrayList<BarEntry>, xTags: ArrayList<String>, color: Int) {
        /**初始化样式*/
        mBarChart.setDrawValueAboveBar(true)
        mBarChart.setMaxVisibleValueCount(60)
        // 背景颜色
        mBarChart.setBackgroundColor(Color.WHITE)
        // 是否显示网格背景
        mBarChart.setDrawGridBackground(false)
        // 显示每条背景阴影
        mBarChart.setDrawBarShadow(false)
        // 设置图表边框的颜色
        mBarChart.setBorderColor(Color.LTGRAY)
        // mBarChart.setHighlightFullBarEnabled(false);
        // 所有触摸事件,默认true
        mBarChart.setTouchEnabled(false)
        // 可拖动,默认true
        mBarChart.isDragEnabled = true
        // 两个轴上的缩放,X,Y分别默认为true
        mBarChart.setScaleEnabled(false)
        // X轴上的缩放,默认true
        mBarChart.isScaleXEnabled = false
        // Y轴上的缩放,默认true
        mBarChart.isScaleYEnabled = false
        // X,Y轴同时缩放，false则X,Y轴单独缩放,默认false
        mBarChart.setPinchZoom(false)
        // 双击缩放,默认true
        mBarChart.isDoubleTapToZoomEnabled = false
        // 抬起手指，继续滑动,默认true
        mBarChart.isDragDecelerationEnabled = false
        // 显示图表边界线
        mBarChart.setDrawBorders(false)
        // 设置XY动画效果
        // mBarChart.animateY(1000, Easing.Linear)
        // mBarChart.animateX(1000, Easing.Linear)
        // 显示描述信息
        mBarChart.description.isEnabled = false
        // 图例设置
        val legend = mBarChart.legend
        // 显示图例
        legend.form = Legend.LegendForm.NONE
        // 图例文字的大小
        legend.textSize = 9f
        // 图例显示位置
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        // 排列方向
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        // 设置绘制在内部
        legend.setDrawInside(false)

        /**XY轴的设置*/
        // X轴设置显示位置
        mBarChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        // X轴最小间距
        mBarChart.xAxis.granularity = 1f
        // 绘制网格线
        mBarChart.xAxis.setDrawGridLines(false)
        // X轴字体样式
        mBarChart.xAxis.typeface = Typeface.DEFAULT_BOLD
        mBarChart.xAxis.textColor = Color.GRAY
        mBarChart.xAxis.axisLineColor = Color.GRAY
        // 设置X轴文字居中对齐
        mBarChart.xAxis.setCenterAxisLabels(true)

        // 绘制Y轴网格线
        mBarChart.axisRight.setDrawGridLines(true)
        mBarChart.axisRight.axisMinimum = 0f
        mBarChart.axisLeft.axisMinimum = 0f
        // Y轴字体样式
        mBarChart.axisRight.typeface = Typeface.DEFAULT_BOLD
        mBarChart.axisRight.textColor = Color.GRAY
        mBarChart.axisRight.axisLineColor = Color.GRAY
        // Y轴显示
        mBarChart.axisRight.isEnabled = true
        mBarChart.axisLeft.isEnabled = false

        /**设置数据*/
        val dataSets = ArrayList<IBarDataSet>()
        // 每一个BarDataSet代表一类柱状图
        mValues.forEach { barEntry ->
            val barDataSet = BarDataSet(listOf(barEntry), "")
            barDataSet.color = color
            // 是否显示顶部的值
             barDataSet.setDrawValues(true)
            // 文字的大小
            barDataSet.valueTextSize = 11f
            barDataSet.formLineWidth = 1f
            barDataSet.formSize = 10.0f
            dataSets.add(barDataSet)
        }
        val data = BarData(dataSets)
        // 设置宽度
        data.barWidth = 0.3f

        // 是否显示X轴标签
        mBarChart.xAxis.setDrawLabels(true)
        // 设置X轴标签数
        mBarChart.xAxis.setLabelCount(mValues.size + 1, true)
        val xAxisFormatter = XAxisValueFormatter(xTags)
        mBarChart.xAxis.valueFormatter = xAxisFormatter

        // 是否显示Y轴标签
        mBarChart.axisRight.setDrawLabels(true)
        // 设置Y轴标签数
        mBarChart.axisRight.setLabelCount(mValues.size + 1, true)
        // 设置Y轴刻度线
        val custom = YAxisValueFormatter()
        mBarChart.axisRight.valueFormatter = custom

        mBarChart.data = data
    }

    /**展示柱状图(多条)
     */
    fun showMoreBarChart(xAxisValues: List<Float>, yAxisValues: List<List<Float>>, labels: List<String>, colours: List<Int>) {
        initLineChart()
        val data = BarData()
        for (i in yAxisValues.indices) {
            val entries = ArrayList<BarEntry>()
            for (j in 0 until yAxisValues[i].size) {
                entries.add(BarEntry(xAxisValues[j], yAxisValues[i][j]))
            }
            val barDataSet = BarDataSet(entries, labels[i])

            barDataSet.color = colours[i]
            barDataSet.valueTextColor = colours[i]
            barDataSet.valueTextSize = 10f
            barDataSet.axisDependency = YAxis.AxisDependency.LEFT
            data.addDataSet(barDataSet)
        }
        val amount = yAxisValues.size
        // 柱状图组之间的间距
        val groupSpace = 0.3f
        val barSpace = ((1 - 0.12) / amount.toDouble() / 10.0).toFloat()
        val barWidth = ((1 - 0.3) / amount.toDouble() / 10.0 * 9).toFloat()

        mBarChart.xAxis.setLabelCount(xAxisValues.size - 1, false)
        data.barWidth = barWidth
        val xValues = arrayOf("小学", "初中", "高中", "专科", "本科")
        mBarChart.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                for (i in xAxisValues.indices) {
                    if (value == xAxisValues[i] - 1F) {
                        return xValues[i]
                    }
                }
                return ""
            }
        }
        data.groupBars(0f, groupSpace, barSpace)
        mBarChart.data = data
    }

    /**设置Y轴值
     */
    fun setYAxis(max: Float, min: Float, labelCount: Int) {
        if (max < min) {
            return
        }
        mBarChart.axisLeft.axisMaximum = max
        mBarChart.axisLeft.axisMinimum = min
        mBarChart.axisLeft.setLabelCount(labelCount, false)

        mBarChart.axisRight.axisMaximum = max
        mBarChart.axisRight.axisMinimum = min
        mBarChart.axisRight.setLabelCount(labelCount, false)
        mBarChart.invalidate()
    }

    /**设置X轴的值
     */
    fun setXAxis(max: Float, min: Float, labelCount: Int) {
        mBarChart.xAxis.axisMaximum = max
        mBarChart.xAxis.axisMinimum = min
        mBarChart.xAxis.setLabelCount(labelCount, false)
        mBarChart.invalidate()
    }

    /**设置高限制线*/
    fun setHightLimitLine(high: Float, name: String?, color: Int) {
        var mName = name
        if (mName == null) {
            mName = "高限制线"
        }
        val heightLimit = LimitLine(high, mName)
        heightLimit.lineWidth = 4f
        heightLimit.textSize = 10f
        heightLimit.lineColor = color
        heightLimit.textColor = color
        mBarChart.axisLeft.addLimitLine(heightLimit)
        mBarChart.invalidate()
    }

    /**设置低限制线
     */
    fun setLowLimitLine(low: Int, name: String?) {
        var mName = name
        if (mName == null) {
            mName = "低限制线"
        }
        val heightLimit = LimitLine(low.toFloat(), mName)
        heightLimit.lineWidth = 4f
        heightLimit.textSize = 10f
        mBarChart.axisLeft.addLimitLine(heightLimit)
        mBarChart.invalidate()
    }

    /**设置描述信息*/
    fun setDescription(str: String) {
        val description = Description()
        description.text = str
        mBarChart.description = description
        mBarChart.invalidate()
    }
}
