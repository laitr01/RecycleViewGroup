package com.example.macintosh.recycleviewgroup

import android.support.annotation.UiThread
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.TextView

/**
 * Provide a reference to the type of views that you are using (custom ViewHolder)
 */
class ItemViewHolder<C> (v: View) : RecyclerView.ViewHolder(v) {
    val textView: TextView
    internal var mChild: C? = null
    var adapter: Adapter<GroupItem, DataItem>? = null
    init {
        // Define click listener for the ViewHolder's View.
        textView = v.findViewById(R.id.textView)
    }

    /**
     * @return the childListItem associated with this view holder
     */
    @UiThread
    fun getChild(): C? {
        return mChild
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

    /**
     * Returns the adapter position of the Child associated with this ChildViewHolder
     *
     * @return The adapter position of the Child if it still exists in the adapter.
     * RecyclerView.NO_POSITION if item has been removed from the adapter,
     * RecyclerView.Adapter.notifyDataSetChanged() has been called after the last
     * layout pass or the ViewHolder has already been recycled.
     */
    @UiThread
    fun getChildAdapterPosition(): Int {
        val flatPosition = adapterPosition
        return if (adapter == null || flatPosition == RecyclerView.NO_POSITION) {
            RecyclerView.NO_POSITION
        } else adapter?.getChildPosition(flatPosition)?:-1

    }

}