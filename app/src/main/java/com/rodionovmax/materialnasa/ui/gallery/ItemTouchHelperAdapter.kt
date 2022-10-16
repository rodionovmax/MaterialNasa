package com.rodionovmax.materialnasa.ui.gallery

interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int)
    // fun onItemDismiss(position: Int) // dismissed because we don't remove item by swipe
}