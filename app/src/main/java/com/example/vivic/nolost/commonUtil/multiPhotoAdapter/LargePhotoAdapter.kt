package com.example.vivic.nolost.commonUtil.multiPhotoAdapter

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.vivic.nolost.GlideApp

class LargePhotoAdapter(viewList: MutableList<View>?, photoList: MutableList<String>?) : PagerAdapter() {

    private var viewList: MutableList<View>? = null
    private var photoList: MutableList<String>? = null

    companion object {
        const val viewCount = 4
        val TAG = LargePhotoAdapter::class.simpleName
    }

    init {
        this.viewList = viewList
        this.photoList = photoList
    }

    override fun getCount(): Int {
        return photoList?.size ?: 0
    }

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return o == view
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val i = position % viewCount
        container.addView(this.viewList?.get(i))
        GlideApp.with(container.context).asDrawable().load(photoList?.get(position))
                .thumbnail(0.25f).into((viewList?.get(i) as ImageView))
        Log.d(TAG, "正在生成第${position}页，使用的view是第${i}个")
        return this.viewList?.get(i) as View
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val i = position % viewCount
        Log.d(TAG, "正在销毁第${position}页，销毁的view是第${i}个")
        container.removeView(this.viewList?.get(i))
    }
}
