package com.example.vivic.nolost.commonUtil.multiPhotoAdapter

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.example.vivic.nolost.GlideApp
import com.example.vivic.nolost.R
import com.example.vivic.nolost.activity.BaseActivity
import kotlinx.android.synthetic.main.layout_large_photo_dialog.*

class LargePhotoDialog() : DialogFragment() {


    private var largePhotoAdapter: LargePhotoAdapter? = null
    private var viewList: MutableList<View>? = null
    private var photoList: ArrayList<String>? = null
    private var currentIndex: Int? = null
    private var inflater: LayoutInflater? = null
    private var mIndicatorLayout: LinearLayout? = null
    private val mIndicatorViews = ArrayList<ImageView>()

    companion object {
        private val TAG = LargePhotoDialog::class.java.simpleName
        private const val PHOTO_LIST = "photoList"
        private const val CURRENT_INDEX = "currentIndex"

        fun getInstance(photoList: ArrayList<String>, currentIndex: Int): LargePhotoDialog {
            val largePhotoDialog = LargePhotoDialog()
            val bundle = Bundle()
            bundle.putStringArrayList(PHOTO_LIST, photoList)
            bundle.putInt(CURRENT_INDEX, currentIndex)
            largePhotoDialog.arguments = bundle
            return largePhotoDialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Dialog_Fullscreen)
        this.photoList = arguments?.getStringArrayList(PHOTO_LIST)
        currentIndex = arguments?.getInt(CURRENT_INDEX)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (dialog != null && dialog.window != null) {
            val flags = WindowManager.LayoutParams.FLAG_FULLSCREEN
            dialog.window!!.setFlags(flags, flags)
            dialog.window!!.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
        this.inflater = inflater
        return inflater.inflate(R.layout.layout_large_photo_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        photoList?.let {
            viewList = mutableListOf()
            for (i in 0 until it.size) {
                val view = inflater?.inflate(R.layout.item_large_photo, null, false) ?: return
                view.setOnClickListener { dismissAllowingStateLoss() }
                viewList?.add(view)
            }
            largePhotoAdapter = LargePhotoAdapter(viewList)
            vp_large_photo.adapter = largePhotoAdapter
            vp_large_photo.currentItem = currentIndex!!
            GlideApp.with(this@LargePhotoDialog).asDrawable().load(photoList?.get(currentIndex!!)).thumbnail(0.25f).into((viewList?.get(currentIndex!!) as ImageView))
            vp_large_photo.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(p0: Int) {

                }

                override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
                }

                override fun onPageSelected(index: Int) {
                    Log.d(TAG, "current page = $index")
                    GlideApp.with(this@LargePhotoDialog).asDrawable().load(photoList?.get(index)).thumbnail(0.25f).into((viewList?.get(index) as ImageView))
                }
            })
        }
    }

    fun updateIndicator() {
        photoList?.let {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Glide.get(activity as Context).clearMemory()
    }

    fun show(context: Context) {
        try {
            val fm = (context as BaseActivity).supportFragmentManager
            val ft = fm.beginTransaction()
            val fragment = fm.findFragmentByTag(TAG)
            if (fragment != null) {
                ft.remove(fragment)
            }
            if (this.isAdded) {
                return
            }
            ft.add(this, TAG)
            ft.commitAllowingStateLoss()
            Log.d(TAG, "show $TAG")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
