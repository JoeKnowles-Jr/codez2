package com.jk.codez.ad

import com.jk.codez.ad.AestheticDialog

interface ButtonClickListener {
    fun onSave(dialog: AestheticDialog.Builder)
    fun onDelete(dialog: AestheticDialog.Builder)
}