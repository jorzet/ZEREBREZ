/*
 * Copyright [2018] [Jorge Zepeda Tinoco]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zerebrez.zerebrez.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.zerebrez.zerebrez.R
import java.text.DecimalFormat
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import kotlinx.android.synthetic.main.custom_exam_average.view.*
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import com.zerebrez.zerebrez.models.AverageExams

/**
 * Created by Jorge Zepeda Tinoco on 13/05/18.
 * jorzet.94@gmail.com
 */

private const val TAG : String = "ExamsAverageListAdapter"

class ExamsAverageListAdapter(averageExams : List<AverageExams>, context : Context) : BaseAdapter() {

    private val mAverageExams : List<AverageExams> = averageExams
    private val mContext : Context = context

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val currentAverageExam = getItem(position) as AverageExams

        val inflator = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val examView = inflator.inflate(R.layout.custom_exam_average, null)

        examView.tv_exam_number.text = "EXAMEN " + currentAverageExam.getMyExamId()

        examView.bc_exams_average.setDrawBarShadow(false)
        examView.bc_exams_average.setDrawValueAboveBar(true);
        examView.bc_exams_average.getDescription().setEnabled(false)
        examView.bc_exams_average.setMaxVisibleValueCount(3)
        examView.bc_exams_average.setPinchZoom(false)
        examView.bc_exams_average.setDrawGridBackground(false)

        // hide right axis
        val rightAxis = examView.bc_exams_average.getAxisRight()
        rightAxis.setEnabled(false)
        // hide legend
        val legend = examView.bc_exams_average.getLegend()
        legend.setEnabled(false)

        // custom left axis
        val leftAxis = examView.bc_exams_average.getAxisLeft()
        leftAxis.setLabelCount(8, false)
        leftAxis.setValueFormatter(MyAxisValueFormatter())
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        leftAxis.setSpaceTop(35f)
        leftAxis.setAxisMinimum(0f)
        leftAxis.setAxisMaximum(128f)

        // custom bottom axis
        val xAxis = examView.bc_exams_average.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(3);
        xAxis.setValueFormatter(CharacterAxisValueFormatter());

        setData(currentAverageExam, examView.bc_exams_average)

        return examView
    }

    private fun setData(averageExams: AverageExams, mChart : BarChart) {

        val yVals1 = ArrayList<BarEntry>()

        yVals1.add(BarEntry(1.0F, averageExams.getMyAverage().toFloat()))
        yVals1.add(BarEntry(2.0F, averageExams.getBeastAverage().toFloat()))
        yVals1.add(BarEntry(3.0F, averageExams.getUsersAverage().toFloat()))

        val set1 : BarDataSet

        if (mChart.getData() != null && mChart.getData().getDataSetCount() > 0) {
            set1 = mChart.getData().getDataSetByIndex(0) as BarDataSet
            set1.values = yVals1
            mChart.getData().notifyDataChanged()
            mChart.notifyDataSetChanged()
        } else {
            set1 = BarDataSet(yVals1, "")
            set1.setDrawIcons(false)
            set1.setColors(mContext.resources.getColor(R.color.me_color),
                    mContext.resources.getColor(R.color.beast_color),
                    mContext.resources.getColor(R.color.average_color))
            set1.setValueFormatter(ValueFormatter());
            set1.setDrawValues(true)

            val dataSets = ArrayList<IBarDataSet>()
            dataSets.add(set1)

            val data = BarData(dataSets)
            data.setValueTextSize(10f)
            data.barWidth = 0.2f

            mChart.setData(data)
        }
    }

    override fun getItem(position: Int): Any {
        return this.mAverageExams.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return this.mAverageExams.size
    }

    private class  MyAxisValueFormatter() : IAxisValueFormatter {

        private var mFormatter : DecimalFormat = DecimalFormat("###,###,###,##0.0")

        override fun getFormattedValue(value: Float, axis: AxisBase?): String {
            return mFormatter.format(value);
        }
    }

    private class CharacterAxisValueFormatter() : IAxisValueFormatter {
        val mCharacter = arrayListOf("YO","MEJOR", "PROMEDIO")

        override fun getFormattedValue(value: Float, axis: AxisBase?): String {
            Log.d(TAG, "value: " + (value - 1))
            return mCharacter.get(value.toInt() - 1)
        }
    }

    private class ValueFormatter : IValueFormatter {
        override fun getFormattedValue(value: Float, entry: Entry?, dataSetIndex: Int, viewPortHandler: ViewPortHandler?): String {
            Log.d(TAG, "value: " + value)
            return value.toInt().toString();
        }
    }
}