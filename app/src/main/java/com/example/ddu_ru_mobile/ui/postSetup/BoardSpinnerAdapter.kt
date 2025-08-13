package com.example.ddu_ru_mobile.ui.postSetup

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.ddu_ru_mobile.R

class BoardSpinnerAdapter (context: Context, private val resId:Int, private val textId:Int, private val categoryList : Array<String>):
    ArrayAdapter<String>(context,resId,textId,categoryList) {

    private var selectedPosition: Int = -1

    fun setSelectedPosition(position: Int) {
        selectedPosition = position
        notifyDataSetChanged()
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val textView = view.findViewById<TextView>(textId)

        textView.setTextColor(ContextCompat.getColor(context, R.color.sub_gray))

        if (position == count) {
            textView.text = ""
            textView.hint = getItem(super.getCount() - 1) // 마지막 항목을 hint로
            textView.setTextColor(ContextCompat.getColor(context, R.color.sub_gray))
        } else {
            textView.text = getItem(position)
            if (position == selectedPosition) {
                textView.setTextColor(ContextCompat.getColor(context, R.color.main_color))
            }
        }
        return view
    }

    override fun getCount(): Int {
        return super.getCount() - 1
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val textView = view.findViewById<TextView>(textId)

        // 선택된 항목인 경우에만 디자인 변경
        if (position == selectedPosition) {
            textView.setTextColor(context.getColor(com.example.ddu_ru_mobile.R.color.main_color))
            view.setBackgroundResource(com.example.ddu_ru_mobile.R.drawable.bg_border_selected)
        } else {
        // 선택되지 않은 항목은 기본 스타일
        textView.setTextColor(context.getColor(com.example.ddu_ru_mobile.R.color.calendar_text_black))
        view.setBackgroundResource(com.example.ddu_ru_mobile.R.drawable.bg_dropdown)
        }

        
        return view
    }
}