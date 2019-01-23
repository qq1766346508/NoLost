package com.example.vivic.nolost.bmob

import android.util.Log
import cn.bmob.v3.datatype.BmobFile
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.UploadFileListener
import io.reactivex.disposables.Disposable

object FileRepository {
    val TAG = FileRepository::class.java.simpleName

    interface IFileCallback {
        fun success(result: String?)
        fun error(throwable: Throwable?)
        fun progress(int: Int?)

    }

    fun uploadFile(bmobFile: BmobFile, iFileCallback: IFileCallback): Disposable {
        return bmobFile.upload(object : UploadFileListener() {
            override fun done(p0: BmobException?) {
                if (p0 == null) {
                    Log.i(TAG, "uploadFile success,url:${bmobFile.fileUrl}")
                    iFileCallback.success(bmobFile.fileUrl)
                } else {
                    Log.i(TAG, "uploadFile error,$p0")
                    iFileCallback.error(p0)
                }
            }

            override fun onProgress(value: Int?) {
                Log.i(TAG, "uploadFile progress is $value")
                iFileCallback.progress(value)
            }
        })
    }

}