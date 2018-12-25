package com.example.vivic.nolost.commonUtil.toastUtil

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import java.lang.reflect.Field
import java.lang.reflect.Modifier


open class ToastCompat(context: Context?) : Toast(context) {

    private val TAG = "ToastCompat"

    companion object {

        fun makeText(context: Context, text: CharSequence, duration: Int): Toast {
            return if(needHook()){
                val inflate = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val resources = context.resources
                val v = inflate.inflate(resources.getIdentifier("transient_notification", "layout", "android"), null)
                val tv = v.findViewById(resources.getIdentifier("message", "id", "android")) as TextView
                tv.text = text

                val toastCompat = ToastCompat(context)
                toastCompat.view = v
                toastCompat.duration = duration
                toastCompat
            }else{
                Toast.makeText(context, text, duration)
            }
        }

        fun makeText(context: Context, resId: Int, duration: Int): Toast {
            return makeText(context, context.resources.getText(resId), duration)
        }

        /**
         * 判断是否是android-25
         */
        private fun needHook(): Boolean {
            return Build.VERSION.SDK_INT == 25
        }
    }

    override fun show(){
        if(needHook()){
            tryToHook()
        }
        super.show()
    }

    /**
     * hook TN中的 mHandler，并重新赋值其中的mHandler中的mCallback
     * final Handler mHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
    IBinder token = (IBinder) msg.obj;
    handleShow(token);
    }
    };
     */
    private fun tryToHook() {
        try {
            val mTN = getFieldValue(this, "mTN")
            if (mTN != null) {
                var isSuccess = false

                val rawHandler = getFieldValue(mTN, "mHandler")
                if (rawHandler != null && rawHandler is Handler) {
                    isSuccess = setFieldValue(rawHandler, "mCallback", InternalHandlerCallback(rawHandler))
                }

                if (!isSuccess) {
                    Log.e(TAG, "tryToHook Toast error.")
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    /**
     * Callback用来处理真实handler的handleMessage方法
     */
    private inner class InternalHandlerCallback(private val mHandler: Handler) : Handler.Callback {

        override fun handleMessage(msg: Message): Boolean {
            try {
                mHandler.handleMessage(msg)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            return true
        }
    }

    /**
     * 反射设置object中fieldName中的值
     */
    private fun setFieldValue(`object`: Any, fieldName: String, newFieldValue: Any): Boolean {
        val field = getDeclaredField(`object`, fieldName)
        if (field != null) {
            try {
                val accessFlags = field.modifiers
                if (Modifier.isFinal(accessFlags)) {
                    val modifiersField = Field::class.java.getDeclaredField("accessFlags")
                    modifiersField.isAccessible = true
                    modifiersField.setInt(field, field.modifiers and Modifier.FINAL.inv())
                }
                if (!field.isAccessible) {
                    field.isAccessible = true
                }
                field.set(`object`, newFieldValue)
                return true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return false
    }

    /**
     * 反射获取object中属性fieldName中的value
     */
    private fun getFieldValue(obj: Any, fieldName: String): Any? {
        val field = getDeclaredField(obj, fieldName)
        return getFieldValue(obj, field)
    }

    /**
     * 反射获取object中field的value
     */
    private fun getFieldValue(obj: Any, field: Field?): Any? {
        if (field != null) {
            try {
                if (!field.isAccessible) {
                    field.isAccessible = true
                }
                return field.get(obj)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }

    /**
     * 我们这里只要获取Toast中的final TN mTN TN中的final Handler mHandler 以及Handler中的final Callback mCallback
     * 这两个都是在当前类的属性，所以只要用到getDeclaredField就好了，不需要遍历,只要直接拿父类就好了
     */
    private fun getDeclaredField(obj: Any, fieldName: String): Field? {
        var superClass = obj.javaClass
        while (superClass != Any::class.java) {
            try {
                return superClass.getDeclaredField(fieldName)
            } catch (e: NoSuchFieldException) {
                superClass = superClass.getSuperclass()
                continue
            }
        }
        return null
    }
}