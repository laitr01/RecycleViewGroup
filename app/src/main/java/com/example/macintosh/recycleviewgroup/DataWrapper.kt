package com.example.macintosh.recycleviewgroup

data class DataItem(val data: String)

data class GroupItem(override val childList: List<DataItem>): Parent<DataItem>


class DataWrapper<P: Parent<C>, C> {

    private lateinit var parent: P
    private var child: C? = null
    private var wrapper: Boolean = false
    var loadmore: Boolean = false
    var firstLoaded: Boolean = true
    var firstDisplayed: Boolean = false
    var remainWrapperChildList: ArrayList<DataWrapper<P, C>> = ArrayList()
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

    constructor(prent: P, fLoaded: Boolean, fDisplayed: Boolean, ldmore: Boolean){
        firstLoaded = fLoaded
        firstDisplayed = fDisplayed
        loadmore = ldmore
        parent = prent
        wrapper = false
    }

    private fun generateChildItemList(parent: P): List<DataWrapper<P, C>> {
        val childItemList = ArrayList<DataWrapper<P, C>>()

        remainWrapperChildList.clear()
        if(firstLoaded){
            parent.childList
                    .take(3)
                    .forEach {
                childItemList.add(DataWrapper(it))
            }
            remainWrapperChildList = parent.childList.drop(3) as ArrayList<DataWrapper<P, C>>
            childItemList.add(DataWrapper(parent, false, true, remainWrapperChildList.size>0))
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
