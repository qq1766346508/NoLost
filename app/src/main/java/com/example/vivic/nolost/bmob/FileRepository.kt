package com.example.vivic.nolost.bmob

import android.util.Log
import cn.bmob.v3.datatype.BmobFile
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.UploadBatchListener
import cn.bmob.v3.listener.UploadFileListener
import io.reactivex.disposables.Disposable

object FileRepository {
    val TAG = FileRepository::class.java.simpleName

    interface IFileCallback {
        fun success(url: String?)
        fun error(throwable: Throwable?)
        fun progress(progress: Int?)

    }

    /**
     * 上传单个文件
     */
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

    //1、curIndex--表示当前第几个文件正在上传
    //2、curPercent--表示当前上传文件的进度值（百分比）
    //3、total--表示总的上传文件数
    //4、totalPercent--表示总的上传进度（百分比）
    interface IFileBatchCallback {
        fun success(files: MutableList<BmobFile>, urls: MutableList<String>?)
        fun error(statuscode: Int, errormsg: String)
        fun progress(curIndex: Int?, curPercent: Int, total: Int, totalPercent: Int)
    }

    /**
     * 批量上传文件
     */
    fun uploadBatchFile(photoList: MutableList<String>, iFileBatchCallback: IFileBatchCallback) {
        BmobFile.uploadBatch(photoList.toTypedArray(), object : UploadBatchListener {
            override fun onSuccess(files: MutableList<BmobFile>, urls: MutableList<String>) {
                Log.i(TAG, "uploadBatchFile success")
                iFileBatchCallback.success(files, urls)
            }

            override fun onProgress(curIndex: Int, curPercent: Int, total: Int, totalPercent: Int) {
                Log.i(TAG, "uploadBatchFile onProgress")
                iFileBatchCallback.progress(curIndex, curPercent, total, totalPercent)
            }

            override fun onError(statuscode: Int, errormsg: String) {
                Log.i(TAG, "uploadBatchFile onError")
                iFileBatchCallback.error(statuscode, errormsg)
            }

        })
    }

}