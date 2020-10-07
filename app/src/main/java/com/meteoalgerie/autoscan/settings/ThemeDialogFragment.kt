package com.meteoalgerie.autoscan.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.meteoalgerie.autoscan.R
import kotlinx.android.synthetic.main.dialog_theme.*

class ThemeDialogFragment(
    private val checkedTheme: String,
    private val onCheckedChangeListener: (theme: Int) -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_theme, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val radioButtonId = when (checkedTheme) {
            getString(R.string.theme_dark) -> R.id.radioDark
            getString(R.string.theme_light) -> R.id.radioLight
            getString(R.string.theme_follow_os_setting) -> R.id.radioSystem
            else -> throw IllegalArgumentException("Unknown theme")
        }
        radioGroup.check(radioButtonId)

        radioGroup.setOnCheckedChangeListener { group, checkedRadioButtonId ->
            val checkedThemeName = when(checkedRadioButtonId) {
                R.id.radioDark -> R.string.theme_dark
                R.id.radioLight -> R.string.theme_light
                R.id.radioSystem -> R.string.theme_follow_os_setting
                else -> throw IllegalArgumentException("Unknown radio button id")
            }

            onCheckedChangeListener(checkedThemeName)
            dismiss()
        }
    }
}