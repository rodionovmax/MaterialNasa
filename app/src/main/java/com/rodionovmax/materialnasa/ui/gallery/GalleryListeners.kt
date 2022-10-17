package com.rodionovmax.materialnasa.ui.gallery

import androidx.recyclerview.widget.RecyclerView
import com.rodionovmax.materialnasa.data.model.Pod

interface GalleryListeners {

    interface OnDeleteButtonClickedListener {
        fun removeFromDatabase(pod: Pod)
    }

    interface OnStartDragListener {
        fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
    }

    interface OnPositionsChangedListener {
        fun updatePositionsInDb(posFrom: Int, posTo: Int, pod: Pod)
    }
}