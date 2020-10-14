package com.meteoalgerie.autoscan.settings

import android.app.Dialog
import android.os.Bundle
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.snackbar.Snackbar
import com.meteoalgerie.autoscan.R

class NumberPickerFragment(
    private val value: Int,
    private val minValue: Int,
    private val maxValue: Int
) : DialogFragment() {
    var doOnPositiveButtonClick: ((number: Int) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val rootView = requireActivity().layoutInflater.inflate(R.layout.dialog_number_picker, null)
        val numberPicker = rootView.findViewById<NumberPicker>(R.id.numberPicker)
        numberPicker.minValue = this.minValue
        numberPicker.maxValue = this.maxValue
        numberPicker.value = this.value
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(rootView)
            .setTitle(R.string.edit_barcode_length_dialog_title)
            .setPositiveButton(R.string.dialog_ok)
            { dialog, id -> doOnPositiveButtonClick?.invoke(numberPicker.value) }
            .setNegativeButton(R.string.dialog_cancel)
            { dialog, id -> dialog.cancel() }

        return builder.create()
    }
}