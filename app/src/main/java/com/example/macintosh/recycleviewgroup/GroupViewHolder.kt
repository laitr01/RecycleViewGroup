package com.example.macintosh.recycleviewgroup

import android.support.annotation.UiThread
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView

/**
 * Provide a reference to the type of views that you are using (custom ViewHolder)
 */
class GroupViewHolder<P:Parent<C>, C>(v: View) : RecyclerView.ViewHolder(v) {
    val textView: TextView
    internal var mParent: P? = null
    var adapter: Adapter<GroupItem, DataItem>? = null
    init {
        // Define click listener for the ViewHolder's View.
        textView = v.findViewById(R.id.textView)
    }

    @UiThread
    fun getParent(): P? {
        return mParent
    }

    /**
     * Returns the adapter position of the Parent associated with this ParentViewHolder
     *
     * @return The adapter position of the Parent if it still exists in the adapter.
     * RecyclerView.NO_POSITION if item has been removed from the adapter,
     * RecyclerView.Adapter.notifyDataSetChanged() has been called after the last
     * layout pass or the ViewHolder has already been recycled.
     */
    @UiThread
    fun getParentAdapterPosition(): Int {
        val flatPosition = adapterPosition
        return if (flatPosition == RecyclerView.NO_POSITION) {
            flatPosition
        } else adapter?.getNearestParentPosition(flatPosition)?:-1

    }
}
