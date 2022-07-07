package com.belajar.storyapp.customView

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import com.belajar.storyapp.R.string

class EmailEditText : AppCompatEditText {

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
        inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        setHint(string.email)
        setAutofillHints(AUTOFILL_HINT_EMAIL_ADDRESS)
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = s.toString()
                when {
                    email.isBlank() -> {
                        requestFocus()
                        error = context.getString(string.empty_email)
                    }
                    !Patterns.EMAIL_ADDRESS.matcher(email).matches()-> {
                        requestFocus()
                        error = context.getString(string.invalid_email)
                    }
                }
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
    }
}