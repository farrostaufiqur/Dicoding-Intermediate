package com.belajar.storyapp.customView

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.belajar.storyapp.R.string

class PasswordEditText : AppCompatEditText {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        transformationMethod = PasswordTransformationMethod.getInstance()
    }

    private fun init() {
        inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD

        setHint(string.password)
        setAutofillHints(AUTOFILL_HINT_PASSWORD)
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = s.toString()
                when {
                    password.isBlank() -> {
                        requestFocus()
                        error = context.getString(string.empty_password)
                    }
                    password.length < 6 -> {
                        requestFocus()
                        error = context.getString(string.not_enough_password)
                    }
                }
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
    }
}