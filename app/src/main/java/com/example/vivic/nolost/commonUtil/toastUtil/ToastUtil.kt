package com.example.vivic.nolost.commonUtil.toastUtil

import android.content.Context
import android.view.Gravity
import android.widget.Toast
import com.example.vivic.nolost.NoLostApplication


object ToastUtil {

    /**
     * 返回app context
     */
    private val sApplication: Context
        get() {
            return NoLostApplication.getContext()
        }

    /**
     * 返回一个ToastCompat对象， 默认显示的Gravity是center
     * @param resId Int
     * @return Toast
     */
    private fun getDefaultToast(resId: Int): Toast {
        val message = sApplication.getString(resId)
        return getDefaultToast(message)
    }

    /**
     * 返回一个ToastCompat对象， 默认显示的Gravity是center
     * @param message String
     * @return Toast
     */
    private fun getDefaultToast(message: String): Toast {
        val toast = ToastCompat.makeText(sApplication, message, Toast.LENGTH_LONG)
        toast.setGravity(Gravity.CENTER, 0, 0)
        return toast
    }

    /**
     * 根据resource id 显示toast， 默认显示时长为Toast.Long, 默认位置为Center
     * @see Toast.LENGTH_LONG
     * @param resId Int 资源id
     */
    @JvmStatic
    fun showToast(resId: Int) {
        val toast = getDefaultToast(resId)
        toast.show()
    }

    /**
     * 根据资源id和duration显示toast, 默认位置为Center
     * @param resId Int    文字资源id
     * @param duration Int 显示时长
     */
    @JvmStatic
    fun showToast(resId: Int, duration: Int) {
        val toast = getDefaultToast(resId)
        toast.duration = duration
        toast.show()
    }

    /**
     * show toast, 默认时长为Long
     * @param resId Int    文字资源id
     * @param gravity Int  显示屏幕位置
     * @param xOffset Int  横坐标offset
     * @param yOffset Int  纵坐标offset
     */
    @JvmStatic
    fun showToast(resId: Int, gravity: Int, xOffset: Int, yOffset: Int) {
        val toast = ToastCompat.makeText(sApplication, resId, Toast.LENGTH_LONG)
        toast.setGravity(gravity, xOffset, yOffset)
        toast.show()
    }

    /**
     * 定制显示toast
     * @param resId Int    文字资源id
     * @param gravity Int  显示屏幕位置
     * @param xOffset Int  横坐标offset
     * @param yOffset Int  纵坐标offset
     * @param duration Int 显示时长
     */
    @JvmStatic
    fun showToast(resId: Int, gravity: Int, xOffset: Int, yOffset: Int, duration: Int) {
        val toast = ToastCompat.makeText(sApplication, resId, duration)
        toast.setGravity(gravity, xOffset, yOffset)
        toast.show()
    }

    /**
     * 根据文字显示toast， 默认显示时长为Toast.Long, 默认位置为Center
     * @see Toast.LENGTH_LONG
     * @param message String  需要显示的文字
     */
    @JvmStatic
    fun showToast(message: String) {
        val toast = getDefaultToast(message)
        toast.show()
    }

    /**
     * 根据文字显示toast, 默认位置为Center
     * @param message String 需要显示的文字
     * @param duration Int  显示时长
     */
    @JvmStatic
    fun showToast(message: String, duration: Int) {
        val toast = getDefaultToast(message)
        toast.duration = duration
        toast.show()
    }

    /**
     * show toast, 默认时长为Long
     * @param message String 需要显示的文字
     * @param gravity Int  显示屏幕位置
     * @param xOffset Int  横坐标offset
     * @param yOffset Int  纵坐标offset
     */
    @JvmStatic
    fun showToast(message: String, gravity: Int, xOffset: Int, yOffset: Int) {
        val toast = ToastCompat.makeText(sApplication, message, Toast.LENGTH_LONG)
        toast.setGravity(gravity, xOffset, yOffset)
        toast.show()
    }

    /**
     * 定制显示toast
     * @param message String 需要显示的文字
     * @param gravity Int  显示屏幕位置
     * @param xOffset Int  横坐标offset
     * @param yOffset Int  纵坐标offset
     * @param duration Int 显示时长
     */
    @JvmStatic
    fun showToast(message: String, gravity: Int, xOffset: Int, yOffset: Int, duration: Int) {
        val toast = ToastCompat.makeText(sApplication, message, duration)
        toast.setGravity(gravity, xOffset, yOffset)
        toast.show()
    }
}