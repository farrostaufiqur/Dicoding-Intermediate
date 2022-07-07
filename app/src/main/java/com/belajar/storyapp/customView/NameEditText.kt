package com.belajar.storyapp.customView

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.belajar.storyapp.R.string

class NameEditText : AppCompatEditText {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME

        setHint(string.name)
        setAutofillHints(AUTOFILL_HINT_NAME)
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val name = s.toString()
                when {
                    name.length < 6 -> {
                        requestFocus()
                        error = context.getString(string.not_enough_name)
                    }
                    name.isBlank() -> {
                        requestFocus()
                        error = context.getString(string.empty_name)
                    }
                }
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
    }
}