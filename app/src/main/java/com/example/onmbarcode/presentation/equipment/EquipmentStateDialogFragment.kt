package com.example.onmbarcode.presentation.equipment

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.onmbarcode.R

/**
 * A simple [DialogFragment] subclass.
 * Use the [EquipmentStateDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EquipmentStateDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val listener = parentFragment as? EquipmentStateDialogListener
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(getString(R.string.equipment_state_dialog_title))
            .setSingleChoiceItems(
                R.array.equipment_state,
                arguments?.getInt(ARG_CHECKED_STATE) ?: DEFAULT_CHECKED_STATE
            ) { dialog, index -> listener?.onEquipmentStatePicked(index); dialog.dismiss() }
        return builder.create()
    }

    interface EquipmentStateDialogListener {
        fun onEquipmentStatePicked(index: Int)
    }

    companion object {
        private const val ARG_CHECKED_STATE = "checked_state"
        private const val DEFAULT_CHECKED_STATE = 0
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment EquipmentStateDialogFragment.
         */
        @JvmStatic
        fun newInstance(checkedState: Int) =
            EquipmentStateDialogFragment().apply {
                arguments = Bundle().apply { putInt(ARG_CHECKED_STATE, checkedState) }
            }
    }
}