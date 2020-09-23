package com.meteoalgerie.autoscan.presentation.settings

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import com.meteoalgerie.autoscan.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.dialog_input_text.view.*

class InputTextDialogFragment : DialogFragment() {
    var inputTextDialogListener: InputTextDialogListener? = null
    private lateinit var input: TextInputEditText
    private lateinit var inputLayout: TextInputLayout
    private var inputText: String = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context!!)
        val view = View.inflate(context, R.layout.dialog_input_text, null)
        input = view.input
        inputLayout = view.inputLayout

        input.doAfterTextChanged {
            inputTextDialogListener?.doAfterTextChanged(
                view.input,
                view.inputLayout,
                it
            )
        }

        builder.setView(view)
            .setTitle(R.string.edit_server_dialog_title)
            .setPositiveButton(R.string.dialog_ok) { _, _ -> }
            .setNegativeButton(R.string.dialog_cancel) { _, _ ->
                inputTextDialogListener?.doOnNegativeButtonClick()
            }

        return builder.create()
    }

    override fun onResume() {
        super.onResume()
        input.setText(inputText)

        (dialog as AlertDialog).apply {
            getButton(Dialog.BUTTON_POSITIVE).setOnClickListener {
                inputTextDialogListener?.doOnPositiveButtonClick(
                    this,
                    input,
                    inputLayout,
                    input.text
                )
            }
        }
    }

    fun setInputListener(
        doAfterTextChanged: ((
            input: TextInputEditText,
            inputLayout: TextInputLayout,
            text: Editable?
        ) -> Unit)? = null,
        doOnPositiveButtonClick: ((
            dialog: Dialog,
            input: TextInputEditText,
            inputLayout: TextInputLayout,
            text: Editable?
        ) -> Unit)? = null,
        doOnNegativeButtonClick: (() -> Unit)? = null
    ) {
        inputTextDialogListener = object : InputTextDialogListener {
            override fun doAfterTextChanged(
                input: TextInputEditText,
                inputLayout: TextInputLayout,
                text: Editable?
            ) {
                doAfterTextChanged?.invoke(input, inputLayout, text)
            }

            override fun doOnPositiveButtonClick(
                dialog: Dialog,
                input: TextInputEditText,
                inputLayout: TextInputLayout,
                text: Editable?
            ) {
                doOnPositiveButtonClick?.invoke(dialog, input, inputLayout, text)
            }

            override fun doOnNegativeButtonClick() {
                doOnNegativeButtonClick?.invoke()
            }
        }
    }

    fun setInputText(text: String) {
        inputText = text
    }

    companion object {
        fun newInstance() = InputTextDialogFragment()
    }

    interface InputTextDialogListener {
        fun doAfterTextChanged(
            input: TextInputEditText,
            inputLayout: TextInputLayout,
            text: Editable?
        )

        fun doOnPositiveButtonClick(
            dialog: Dialog,
            input: TextInputEditText,
            inputLayout: TextInputLayout,
            text: Editable?
        )

        fun doOnNegativeButtonClick()
    }
}