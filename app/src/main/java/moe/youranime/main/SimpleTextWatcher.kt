package moe.youranime.main

import android.text.Editable
import android.text.TextWatcher

abstract class SimpleTextWatcher: TextWatcher {

    override fun afterTextChanged(s: Editable?) {}
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (s == null) {
            onTextChange("")
        } else {
            onTextChange(s.trim().toString())
        }
    }

    abstract fun onTextChange(s: String)
}