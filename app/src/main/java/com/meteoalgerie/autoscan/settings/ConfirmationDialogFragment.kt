package com.meteoalgerie.autoscan.settings

import android.content.ContextWrapper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.appcompat.view.ContextThemeWrapper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.radiobutton.MaterialRadioButton
import com.meteoalgerie.autoscan.R
import kotlinx.android.synthetic.main.dialog_confirmation.view.*

class ConfirmationDialogFragment(
    private val title: String,
    private val items: List<String>,
    private val checkedItem: String,
    private val onCheckedChangeListener: (checkedItem: Int) -> Unit
) : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.dialog_confirmation, container, false)
        rootView.title.text = title
        val radioGroup = rootView.findViewById<RadioGroup>(R.id.radioGroup)
        items.forEach {
            val radioButton = MaterialRadioButton(ContextThemeWrapper(context, R.style.RadioButton))
            radioButton.text = it
            radioGroup.addView(radioButton)
            if (it == checkedItem) {
                radioGroup.check(radioButton.id)
            }
        }
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val checkedRadioButton = group.findViewById<MaterialRadioButton>(checkedId)
            onCheckedChangeListener(group.indexOfChild(checkedRadioButton))
            dismiss()
        }
        return rootView
    }

    override fun getTheme() = R.style.BottomSheetDialog
}