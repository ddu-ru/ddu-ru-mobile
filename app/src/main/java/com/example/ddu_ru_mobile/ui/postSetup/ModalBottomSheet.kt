package com.example.ddu_ru_mobile.ui.postSetup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.ddu_ru_mobile.R
import java.time.LocalDate


class ModalBottomSheet : BottomSheetDialogFragment() {

    var onDatePicked: ((LocalDate) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.date_picker_bottom, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val datePicker = view.findViewById<DatePicker>(R.id.datePicker)
        val btnConfirm = view.findViewById<Button>(R.id.btnConfirm)
        datePicker.minDate = System.currentTimeMillis()

        btnConfirm.setOnClickListener {
            val date = LocalDate.of(
                datePicker.year, datePicker.month + 1, datePicker.dayOfMonth
            )
            onDatePicked?.invoke(date)
            dismiss()
        }
    }

    companion object {
        const val TAG = "BasicBottomModalSheet"
    }
}