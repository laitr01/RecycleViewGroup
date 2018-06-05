package com.example.macintosh.recycleviewgroup

import android.support.annotation.UiThread
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button

class LoadmoreViewholder<P> (v: View): RecyclerView.ViewHolder(v) {
    internal var parent: P? = null
    var adapter: Adapter<GroupItem, DataItem>? = null

    init {
        // Define click listener for the ViewHolder's View.
        val button = v.findViewById<Button>(R.id.loadmore)
    }

    /**
     * @return the childListItem associated with this view holder
     */
    @UiThread
    fun getParent(): P? {
        return parent
    }

    /**
     * Returns the adapter position of the Parent associated with this ChildViewHolder
     *
     * @return The adapter position of the Parent if it still exists in the adapter.
     * RecyclerView.NO_POSITION if item has been removed from the adapter,
     * RecyclerView.Adapter.notifyDataSetChanged() has been called after the last
     * layout pass or the ViewHolder has already been recycled.
     */
    @UiThread
    fun getParentAdapterPosition(): Int {
        val flatPosition = adapterPosition
        return if (adapter == null || flatPosition == RecyclerView.NO_POSITION) {
            RecyclerView.NO_POSITION
        } else adapter?.getNearestParentPosition(flatPosition)?: -1

    }
}