package com.rodionovmax.materialnasa.ui.gallery

import com.rodionovmax.materialnasa.data.model.Pod

interface OnDeleteButtonClickedListener {
    fun removeFromDatabase(pod: Pod)
}