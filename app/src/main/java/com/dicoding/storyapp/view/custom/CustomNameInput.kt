package com.dicoding.storyapp.custom

import android.content.Context
import android.util.AttributeSet
import com.dicoding.storyapp.R


class CustomNameInput @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0

) : EditTextInput(context, attrs, defStyleAttr){

    init{
        ivLeftIcon.setImageResource(R.drawable.user_ic)
    }

}