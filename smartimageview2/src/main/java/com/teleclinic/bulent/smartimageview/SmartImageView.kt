package com.teleclinic.bulent.smartimageview

import android.content.Context
import android.graphics.BitmapFactory
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageView
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import java.io.File


class SmartImageView : androidx.appcompat.widget.AppCompatImageView {

    private var mContext: Context? = null
    private var attrs: AttributeSet? = null
    private var styleAttr: Int? = null
    private var transferUtility: TransferUtility? = null

    var COGNITO_POOL_ID = "us-west-1:2013ea11-1aef-4e74-88a3-7317a9d9c4c0"
    var BUCKET_NAME = "lnq-server-files"


    constructor(context: Context) : super(context) {
        init(context, null, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs, null)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int?) {
        this.mContext = context
        this.attrs = attrs
        this.styleAttr = defStyleAttr
        createTransferUtility()
        //readAttributes()
    }

    private fun createTransferUtility() {
        val credentialsProvider = CognitoCachingCredentialsProvider(
                context.getApplicationContext(),
                COGNITO_POOL_ID,
                Regions.US_WEST_1
        )
        val s3Client = AmazonS3Client(credentialsProvider)
        transferUtility = TransferUtility(s3Client, context.getApplicationContext())
    }

    fun download(objectKey: String?, imageView: ImageView?) {
        val fileDownload: File = File(context.getCacheDir(), objectKey)
        val transferObserver = transferUtility!!.download(
                BUCKET_NAME,
                objectKey,
                fileDownload
        )
        transferObserver.setTransferListener(object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState) {
                if (TransferState.COMPLETED == state) {
                    val glideThumbnailOptions = RequestOptions()
                            .centerCrop()
                            .placeholder(R.color.gray_minus_4)
                            .error(R.color.gray_minus_4)
                            .priority(Priority.NORMAL)

                    if (imageView != null) {
                        imageView?.let { it1 ->
                            Glide.with(imageView.context)
                                    .load(fileDownload.absoluteFile)
                                    .apply(glideThumbnailOptions)
                                    .into(it1)
                        }
                    }
                }

                //Glide.with(context).load(BitmapFactory.decodeFile(fileDownload.absolutePath)).apply(RequestOptions().centerCrop()).apply(RequestOptions().circleCrop()).apply(RequestOptions().placeholder(R.drawable.avatar)).into(imageView)
                //                    imageView.setImageBitmap(BitmapFactory.decodeFile(fileDownload.getAbsolutePath()));
            }

            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {}
            override fun onError(id: Int, ex: Exception) {
            }
        })
    }

    fun putImage(url: String) {
//        val senderImageAsBytes: ByteArray = Base64.decode(url, Base64.DEFAULT)
//        BitmapFactory.decodeByteArray(senderImageAsBytes, 0, senderImageAsBytes.size)
        download(url, this)

    }
}
