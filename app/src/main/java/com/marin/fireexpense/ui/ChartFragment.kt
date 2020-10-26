package com.marin.fireexpense.ui

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.marin.fireexpense.R
import com.marin.fireexpense.data.model.Expense
import com.marin.fireexpense.databinding.FragmentChartBinding
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

/**
 * Created by Enrique Marín on 13-10-2020.
 */

class ChartFragment : Fragment(R.layout.fragment_chart) {

    private val args: ChartFragmentArgs by navArgs()
    private val expenseList: Array<Expense> by lazy { args.expense }
    private lateinit var lineChart: LineChart
    private val months = listOf("ENE", "FEB", "MAR", "ABR", "MAY", "JUN", "JUL", "AGO", "SEP",
    "OCT", "NOV", "DEC")
    private var tote = 0.0
    private var expenses = mutableListOf<Float>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        val binding = FragmentChartBinding.bind(view)
        lineChart = binding.lineChart

        calculateMonthlySum()

        createLineChart()
    }

    private fun getSameChart (chart: LineChart, description: String): LineChart {
        chart.description.text = description
        chart.description.textSize = 13f
        chart.description.setPosition(900f, 28f)
        chart.setExtraOffsets(10f, 10f, 10f, 10f)
        chart.setBackgroundColor(Color.CYAN)
        chart.animateY(3000)
        //legend(chart)

        return chart
    }

    private fun setEntries(): ArrayList<Entry> {
        val entries = arrayListOf<Entry>()
        for (i in months.indices) {
            entries.add(Entry(i.toFloat(), expenses[i]))
        }

        return entries
    }

    private fun axisX(axis: XAxis) {
        axis.isGranularityEnabled = true
        axis.position = XAxis.XAxisPosition.BOTTOM
        axis.textSize = 12f
        lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(months)
    }

    private fun axisLeft(axis: YAxis) {
        //axis.spaceTop = 50f // Espacio por arriba
        axis.axisMinimum = 0f
    }

    private fun axisRight(axis: YAxis) {
        axis.isEnabled = false
    }

    private fun calculateMonthlySum() {
        val matchMonth = listOf("01", "02", "03", "04", "05", "06", "07",
            "08", "09", "10", "11", "12")

        for (i in matchMonth.indices) {
            var monthlyTote = 0.0
            val list = expenseList.filter { it.month == matchMonth[i] }
            list.forEach { monthlyTote += it.amount }
            expenses.add(monthlyTote.toFloat())
        }

        expenses.forEach { tote += it }
    }

    private fun createLineChart() {
        val res = DecimalFormat("#.##") // Formateo con 2 decimales.
        res.roundingMode = RoundingMode.HALF_UP // Redondeo hacia valor más cercano/arriba.

        val customLineChart = if (distinguishSeveralTypes()) {
            getSameChart(lineChart, "Gastos de ${expenseList[0].type} " +
                    "en el año ${expenseList[0].year}: ${res.format(tote)}€")
        } else {
            getSameChart(lineChart, "Gastos generales " +
                    "del año ${expenseList[0].year}: ${res.format(tote)}€")
        }

        customLineChart.setDrawGridBackground(true)

        axisX(customLineChart.xAxis)
        axisLeft(customLineChart.axisLeft)
        axisRight(customLineChart.axisRight)
        customLineChart.legend.isEnabled = false // Oculta la leyenda, si la hay.

        val data = LineDataSet(setEntries(), "")
        data.fillAlpha = 100
        data.fillDrawable = ResourcesCompat.getDrawable(activity?.resources!!, R.drawable.gradient_bg, null)
        data.setDrawFilled(true)
        data.valueTextSize = 10f
        //data.fillColor = Color.YELLOW

        lineChart.data = LineData(data)
        lineChart.invalidate()
    }

    private fun distinguishSeveralTypes(): Boolean {
        val typeList = mutableListOf<String>()
        expenseList.forEach { typeList.add(it.type) }
        return typeList.toHashSet().size <= 1
    }
}