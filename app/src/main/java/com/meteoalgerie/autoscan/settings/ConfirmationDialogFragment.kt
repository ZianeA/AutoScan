package com.meteoalgerie.autoscan.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.os.bundleOf
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.radiobutton.MaterialRadioButton
import com.meteoalgerie.autoscan.R
import kotlinx.android.synthetic.main.dialog_confirmation.view.*

class ConfirmationDialogFragment() : BottomSheetDialogFragment() {
    private var onCheckedChangeListener: ((index: Int) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.dialog_confirmation, container, false)
        rootView.title.text = requireArguments().getString(ARG_TITLE)

        val items = requireArguments().getStringArrayList(ARG_ITEMS)!!
        val checkedItemIndex = requireArguments().getInt(ARG_CHECKED_ITEM_INDEX)
        val radioGroup = rootView.findViewById<RadioGroup>(R.id.radioGroup)
        items.forEachIndexed { index, item ->
            val radioButton = MaterialRadioButton(ContextThemeWrapper(context, R.style.RadioButton))
            radioButton.text = item
            radioGroup.addView(radioButton)
            if (checkedItemIndex == index) radioGroup.check(radioButton.id)
        }
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButton = group.findViewById<MaterialRadioButton>(checkedId)
            onCheckedChangeListener?.invoke(group.indexOfChild(checkedRadioButton))
            dismiss()
        }
        return rootView
    }

    override fun getTheme() = R.style.BottomSheetDialog

    fun setOnCheckedChangeListener(l: (index: Int) -> Unit) {
        onCheckedChangeListener = l
    }

    companion object {
        private const val ARG_TITLE = "arg_tile"
        private const val ARG_ITEMS = "arg_items"
        private const val ARG_CHECKED_ITEM_INDEX = "arg_checked_item_index"

        fun newInstance(
            title: String,
            items: List<String>,
            checkedItemIndex: Int
        ): ConfirmationDialogFragment {
            return ConfirmationDialogFragment().apply {
                arguments = bundleOf(
                    ARG_TITLE to title,
                    ARG_ITEMS to items,
                    ARG_CHECKED_ITEM_INDEX to checkedItemIndex
                )
            }
        }
    }
}