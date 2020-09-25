package com.example.settlers

import android.widget.CompoundButton

enum class DialogMode { Inspect, Build }

class ModeController : CompoundButton.OnCheckedChangeListener {
    var mode = DialogMode.Inspect

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (isChecked) {
            mode = DialogMode.Build
        } else {
            mode = DialogMode.Inspect
        }
    }

}