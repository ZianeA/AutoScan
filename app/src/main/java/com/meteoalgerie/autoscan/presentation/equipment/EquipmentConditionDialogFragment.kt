package com.meteoalgerie.autoscan.presentation.equipment

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.meteoalgerie.autoscan.R

/**
 * A simple [DialogFragment] subclass.
 * Use the [EquipmentConditionDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EquipmentConditionDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val listener = parentFragment as? EquipmentConditionDialogListener
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.equipment_state_dialog_title))
            .setSingleChoiceItems(
                R.array.equipment_condition,
                arguments?.getInt(ARG_CHECKED_CONDITION) ?: DEFAULT_CHECKED_CONDITION
            ) { dialog, index -> listener?.onEquipmentConditionPicked(index); dialog.dismiss() }
        return builder.create()
    }

    interface EquipmentConditionDialogListener {
        fun onEquipmentConditionPicked(index: Int)
    }

    companion object {
        private const val ARG_CHECKED_CONDITION = "checked_condition"
        private const val DEFAULT_CHECKED_CONDITION = 0
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment EquipmentConditionDialogFragment.
         */
        @JvmStatic
        fun newInstance(checkedCondition: Int) =
            EquipmentConditionDialogFragment().apply {
                arguments = Bundle().apply { putInt(ARG_CHECKED_CONDITION, checkedCondition) }
            }
    }
}