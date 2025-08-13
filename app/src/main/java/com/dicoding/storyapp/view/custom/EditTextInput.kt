package com.dicoding.storyapp.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.widget.addTextChangedListener
import com.dicoding.storyapp.R

open class EditTextInput @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    protected val editText: EditText
    protected val ivLeftIcon: ImageView
    protected val ivRightIcon: ImageView

    init {
        LayoutInflater.from(context).inflate(R.layout.view_edittext, this, true)
        orientation = HORIZONTAL
        setBackgroundResource(R.drawable.bg_edittext)

        ivLeftIcon = findViewById(R.id.ivLeftIcon)
        editText = findViewById(R.id.etInput)
        ivRightIcon = findViewById(R.id.ivRightIcon)

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.EditTextInput)
            val hint = typedArray.getString(R.styleable.EditTextInput_hintText)
            editText.hint = hint
            typedArray.recycle()
        }
    }

    fun getText(): String = editText.text.toString()

    fun setText(text: String) {
        editText.setText(text)
    }

    fun setError(errorMessage: String?) {
        editText.error = errorMessage
    }

    fun addTextChangedListener(listener: (String) -> Unit) {
        editText.addTextChangedListener { listener(it.toString()) }
    }
}
