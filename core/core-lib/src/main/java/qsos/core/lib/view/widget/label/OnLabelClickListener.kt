package qsos.core.lib.view.widget.label

import android.view.View

interface OnLabelClickListener {
    fun onClick(index: Int, v: View, s: String)
}
