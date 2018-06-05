package com.example.macintosh.recycleviewgroup

import android.support.annotation.UiThread
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import java.util.*

open class Adapter<P:Parent<C>, C>(dataSet: List<P>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TYPE_PARENT = 0
    val TYPE_CHILD = 1
    val TYPE_LOADMORE = 2
    private val INVALID_FLAT_POSITION = -1
    internal var flatItemList: MutableList<DataWrapper<P, C>>
    private lateinit var parentList: List<P>

    init {
        flatItemList = generateFlattenedParentChildList(dataSet) as MutableList<DataWrapper<P, C>>
    }


    @UiThread
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (isParentViewType(viewType)) {
            // Create a new view.
            val v = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.text_row_item, viewGroup, false)

            GroupViewHolder<P, C>(v)
        } else {
            if(viewType == TYPE_LOADMORE){
                val v = LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.loadmore, viewGroup, false)

                LoadmoreViewholder<C>(v)
            }else{
                val v = LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.text_row_item, viewGroup, false)

                ItemViewHolder<C>(v)
            }
        }
    }
    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, flatPosition: Int) {
        Log.d(TAG, "Element $flatPosition set.")

        if (flatPosition > flatItemList.size) {
            throw IllegalStateException("Trying to bind item out of bounds, size " + flatItemList.size
                    + " flatPosition " + flatPosition + ". Was the data changed without a call to notify...()?")
        }

        val listItem = flatItemList[flatPosition]
        if (listItem.isParent()) {
            var parentViewHolder = viewHolder as GroupViewHolder<P, C>

            val parent = listItem.getParent() as GroupItem
            //onBindParentViewHolder(parentViewHolder, getNearestParentPosition(flatPosition), listItem.getParent())

            parentViewHolder.textView.text = parent.javaClass.canonicalName

        } else {
            if(listItem.loadmore){

            }else{
                val childViewHolder = viewHolder as ItemViewHolder<C>
                childViewHolder.mChild = listItem.getChild()
                val child = listItem.getChild() as DataItem
                childViewHolder.textView.text = child.data
            }

            //onBindChildViewHolder(childViewHolder, getNearestParentPosition(flatPosition), getChildPosition(flatPosition), listItem.getChild())
        }
    }


    /**
     * For multiple view type support look at overriding [.getParentViewType] and
     * [.getChildViewType]. Almost all cases should override those instead
     * of this method.
     *
     * @param flatPosition The index in the merged list of children and parents to get the view type of
     * @return Gets the view type of the item at the given flatPosition.
     */
    @UiThread
    override fun getItemViewType(flatPosition: Int): Int {
        val listItem = flatItemList[flatPosition]
        return if (listItem.isParent()) {
            getParentViewType(getNearestParentPosition(flatPosition))
        } else {
            getChildViewType(listItem.loadmore, getNearestParentPosition(flatPosition), getChildPosition(flatPosition))
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = flatItemList.size


    /**
     * Return the view type of the parent at `parentPosition` for the purposes of view recycling.
     *
     *
     * The default implementation of this method returns [.TYPE_PARENT], making the assumption of
     * a single view type for the parents in this adapter. Unlike ListView adapters, types need not
     * be contiguous. Consider using id resources to uniquely identify item view types.
     *
     *
     * If you are overriding this method make sure to override [.isParentViewType] as well.
     *
     *
     * Start your defined viewtypes at [.TYPE_FIRST_USER]
     *
     * @param parentPosition The index of the parent to query
     * @return integer value identifying the type of the view needed to represent the parent at
     * `parentPosition`. Type codes need not be contiguous.
     */
    fun getParentViewType(parentPosition: Int): Int {
        return TYPE_PARENT
    }

    /**
     * Return the view type of the child `parentPosition` contained within the parent
     * at `parentPosition` for the purposes of view recycling.
     *
     *
     * The default implementation of this method returns [.TYPE_CHILD], making the assumption of
     * a single view type for the children in this adapter. Unlike ListView adapters, types need not
     * be contiguous. Consider using id resources to uniquely identify item view types.
     *
     *
     * Start your defined viewtypes at [.TYPE_FIRST_USER]
     *
     * @param parentPosition The index of the parent continaing the child to query
     * @param childPosition The index of the child within the parent to query
     * @return integer value identifying the type of the view needed to represent the child at
     * `parentPosition`. Type codes need not be contiguous.
     */
    fun getChildViewType(loadmore:Boolean, parentPosition: Int, childPosition: Int): Int {
        return if (loadmore)
            TYPE_LOADMORE
        else
            TYPE_CHILD
    }

    /**
     * Used to determine whether a viewType is that of a parent or not, for ViewHolder creation purposes.
     *
     *
     * Only override if [.getParentViewType] is being overriden
     *
     * @param viewType the viewType identifier in question
     * @return whether the given viewType belongs to a parent view
     */
    fun isParentViewType(viewType: Int): Boolean {
        return viewType == TYPE_PARENT
    }

    fun getParentList(): List<P> {
        return parentList
    }

    /**
     * Given the index relative to the entire RecyclerView, returns the nearest
     * ParentPosition without going past the given index.
     *
     *
     * If it is the index of a parent, will return the corresponding parent position.
     * If it is the index of a child within the RV, will return the position of that child's parent.
     */
    @UiThread
    internal fun getNearestParentPosition(flatPosition: Int): Int {
        if (flatPosition == 0) {
            return 0
        }

        var parentCount = -1
        for (i in 0..flatPosition) {
            val listItem = flatItemList[i]
            if (listItem.isParent()) {
                parentCount++
            }
        }
        return parentCount
    }
    /**
     * Given the index relative to the entire RecyclerView for a child item,
     * returns the child position within the child list of the parent.
     */
    @UiThread
    internal fun getChildPosition(flatPosition: Int): Int {
        if (flatPosition == 0) {
            return 0
        }

        var childCount = 0
        for (i in 0 until flatPosition) {
            val listItem = flatItemList[i]
            if (listItem.isParent()) {
                childCount = 0
            } else {
                childCount++
            }
        }
        return childCount
    }

    @UiThread
    fun notifyParentDataSetChanged() {
        flatItemList = generateFlattenedParentChildList(parentList) as MutableList<DataWrapper<P, C>>
        notifyDataSetChanged()
    }

    /**
     * Generates a full list of all parents and their children, in order. Uses Map to preserve
     * last expanded state.
     *
     * @param parentList A list of the parents from
     *                   the {@link ExpandableRecyclerAdapter}
     * @param savedLastExpansionState A map of the last expanded state for a given parent key.
     * @return A list of all parents and their children, expanded accordingly
     */
    private fun generateFlattenedParentChildList(parentList: List<P>): List<DataWrapper<P, C>> {
        val flatItemList = ArrayList<DataWrapper<P, C>>()

        val parentCount = parentList.size
        for (i in 0 until  parentCount) {
            val parent = parentList[i]
            generateParentWrapper(flatItemList, parent)
        }

        return flatItemList
    }

    private fun generateParentWrapper(flatItemList: MutableList<DataWrapper<P, C>>, parent: P) {
        val parentWrapper = DataWrapper(parent)
        flatItemList.add(parentWrapper)
        //if (shouldExpand) {
            generateExpandedChildren(flatItemList, parentWrapper)
        //}
    }

    private fun generateExpandedChildren(flatItemList: MutableList<DataWrapper<P, C>>, parentWrapper:
    DataWrapper<P, C>) {
        val wrappedChildList = parentWrapper.getWrappedChildList()
        val childCount = wrappedChildList.size
        for (j in 0 until childCount) {
            val childWrapper = wrappedChildList[j]
            flatItemList.add(childWrapper)
        }
    }

    /**
     * Gets the index of a ExpandableWrapper within the helper item list based on
     * the index of the ExpandableWrapper.
     *
     * @param parentPosition The index of the parent in the list of parents
     * @return The index of the parent in the merged list of children and parents
     */
    @UiThread
    private fun getFlatParentPosition(parentPosition: Int): Int {
        var parentCount = 0
        val listItemCount = flatItemList.size
        for (i in 0 .. listItemCount) {
            if (flatItemList[i].isParent()) {
                parentCount++

                if (parentCount > parentPosition) {
                    return i
                }
            }
        }

        return INVALID_FLAT_POSITION
    }

    companion object {
        private val TAG = "CustomAdapter"
    }
}