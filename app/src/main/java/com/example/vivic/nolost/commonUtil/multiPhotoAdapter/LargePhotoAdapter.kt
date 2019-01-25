package com.example.vivic.nolost.commonUtil.multiPhotoAdapter

import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup

class LargePhotoAdapter(viewList: MutableList<View>?) : PagerAdapter() {

    private var viewList: MutableList<View>? = null

    init {
        this.viewList = viewList
    }

    override fun getCount(): Int {
        return viewList?.size ?: 0
    }

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return o == view
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        container.addView(this.viewList?.get(position))
        return this.viewList?.get(position) as View
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(this.viewList?.get(position))
    }
}
