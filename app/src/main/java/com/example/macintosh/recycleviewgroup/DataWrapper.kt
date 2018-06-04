package com.example.macintosh.recycleviewgroup

import java.util.ArrayList

data class DataItem(val data: String)

data class GroupItem(override val childList: List<DataItem>): Parent<DataItem>


class DataWrapper<P: Parent<C>, C> {

    private lateinit var parent: P
    private var child: C? = null
    private var wrapper: Boolean = false
    private var wrapperChildList: List<DataWrapper<P, C>> = ArrayList()

    constructor(prent: P){
        parent = prent
        wrapper = true
        wrapperChildList = generateChildItemList(parent)
    }

    constructor(chld: C){
        child = chld
        wrapper = false
    }

    private fun generateChildItemList(parent: P): List<DataWrapper<P, C>> {
        val childItemList = ArrayList<DataWrapper<P, C>>()
        parent.childList.forEach {
            childItemList.add(DataWrapper(it))
        }
        return childItemList
    }

    fun getWrappedChildList(): List<DataWrapper<P, C>> {
        if (!wrapper) {
            throw IllegalStateException("Parent not wrapped")
        }

        return wrapperChildList
    }

    //abstract fun getChildList(): List<C>

    fun isParent(): Boolean {
        return wrapper
    }

    fun getChild(): C? {
        return child
    }

    fun getParent(): P{
        return parent
    }

    fun setParent(prent: P) {
        parent = prent
        wrapperChildList = generateChildItemList(parent)
    }


}
