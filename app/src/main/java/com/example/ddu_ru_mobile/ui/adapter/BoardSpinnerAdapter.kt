package com.example.ddu_ru_mobile.ui.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class BoardSpinnerAdapter (context: Context, private val resId:Int, private val textId:Int, private val categoryList : Array<String>):
    ArrayAdapter<String>(context,resId,textId,categoryList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val textView = view.findViewById<TextView>(textId)


        if (position == count) {
            textView.text = ""
            textView.hint = getItem(super.getCount() - 1) // 마지막 항목을 hint로
        } else {
            textView.text = getItem(position)
        }

        return view
    }

    override fun getCount(): Int {
        return super.getCount() - 1
    }
    }