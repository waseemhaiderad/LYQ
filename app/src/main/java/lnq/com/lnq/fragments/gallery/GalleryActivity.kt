package lnq.com.lnq.fragments.gallery


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lnq.com.lnq.R
import lnq.com.lnq.custom.views.gallery.GalleryModel
import lnq.com.lnq.databinding.ActivityGalleryBinding
import java.io.File

import java.util.ArrayList

class GalleryActivity : AppCompatActivity(), View.OnClickListener, GalleryAdopter.onClickAdapterInterface {
    lateinit var binding: ActivityGalleryBinding
    lateinit var context: Context
    lateinit var listGallery: MutableList<GalleryModelNew>
    lateinit var adopterGallery: GalleryAdopter
    lateinit var listSelection: MutableList<String>
    lateinit var arrAlbums: Array<String?>
    private var posOld: Int = 0
    lateinit var flag: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gallery)
        context = this
        init()
    }

    private fun init() {
        flag = intent.getStringExtra("mFlag")
        binding.mImgDone.setOnClickListener(this)
        binding.mTvFilter.setOnClickListener(this)
        GlobalScope.launch(Dispatchers.Main) {
//            delay(5000)
            binding.mPb.visibility = View.VISIBLE
            val result = loadGalleryData()
            if (result) {
                binding.mRv.layoutManager = GridLayoutManager(context, 4)
                adopterGallery = GalleryAdopter(listGallery, this@GalleryActivity)
                binding.mRv.adapter = adopterGallery
                if (flag.contentEquals("single")) {
                    listSelection.add("")
                }
            } else {
                Toast.makeText(context, "No Pictures Found", Toast.LENGTH_SHORT).show()
            }
            binding.mPb.visibility = View.INVISIBLE
        }
    }

    public override fun onStart() {
        super.onStart()
    }

    public override fun onStop() {
        super.onStop()
    }

    override fun onClickDealsItem(objGallery: GalleryModelNew) {
        val pos = listGallery.indexOf(objGallery)
        when (flag) {
            "multiple" -> if (listSelection.contains(objGallery.path)) {
                objGallery.isSelected = false
                listSelection.remove(objGallery.path)
                adopterGallery.notifyItemChanged(pos)
            } else {
                listGallery[pos].isSelected = true
                listSelection.add(listGallery[pos].path.toString())
                adopterGallery.notifyItemChanged(pos)
            }
            "single" -> {
                if (listSelection.contains(listGallery[pos].path)) {
                    return
                }
                listGallery[posOld].isSelected = false
                adopterGallery.notifyItemChanged(posOld)
                listSelection.set(0, listGallery[pos].path.toString())
                listGallery[pos].isSelected = true
                adopterGallery.notifyItemChanged(pos)
                posOld = pos
            }
        }
        binding.mTvTitle.text = "" + listSelection!!.size
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.mImgDone -> when (flag) {
                "multiple" -> if (listSelection.size == 0) {
                    Toast.makeText(context, "Please Select Atleast One Picture", Toast.LENGTH_SHORT).show()
                } else {
                    val mGson = Gson()
                    val returnIntent = Intent()
                    returnIntent.putExtra("result", mGson.toJson(listSelection))
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                }
                "single" -> if (listSelection.size == 0) {
                    Toast.makeText(context, "Please Select Atleast One Picture", Toast.LENGTH_SHORT).show()
                } else {
                    val mGson = Gson()
                    val returnIntent = Intent()
                    returnIntent.putExtra("result", mGson.toJson(listSelection))
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                }
            }
            R.id.mTvFilter -> {
                val mAlertBuilder: AlertDialog.Builder
                mAlertBuilder = AlertDialog.Builder(context, R.style.MyAlertDialogStyle)
                mAlertBuilder.setItems(arrAlbums) { dialogInterface, i ->
                    if (!binding.mTvFilter.text.toString().contentEquals(arrAlbums[i].toString())) {
                        listSelection.clear()
                        if (flag.contentEquals("single")) {
                            listSelection.add("")
                        }
                        binding.mTvTitle.text = "0"
                        val mSizeLst = listGallery.size
                        listGallery.clear()
                        adopterGallery.notifyItemRangeRemoved(0, mSizeLst)
                        val projection = arrayOf(MediaStore.Images.Media.DATA)
                        when (arrAlbums[i]) {
                            "All Images" -> {
                                val curAll = this@GalleryActivity.contentResolver.query(
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                        projection, // Selection arguments (none)
                                        null        // Ordering
                                        , null, null// Ordering
                                )// Which columns to return
                                // Which rows to return (all rows)
                                // Selection arguments (none)
                                if (curAll != null && curAll.moveToFirst()) {
                                    val datColum = curAll.getColumnIndex(MediaStore.Images.Media.DATA)
                                    do {
                                        listGallery.add(GalleryModelNew("", curAll.getString(datColum), "", false))
                                    } while (curAll.moveToNext())
                                    curAll.close()
                                } else {
                                    Toast.makeText(context, "No Pictures Found", Toast.LENGTH_SHORT).show()
                                }
                            }
                            else -> {
                                val cur = this@GalleryActivity.contentResolver.query(
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                        projection, // Which columns to return
                                        MediaStore.Images.Media.DATA + " like ? ", // Which rows to return (all rows)
                                        arrayOf("%" + arrAlbums[i] + "%"), null
                                )
                                val index = cur!!.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                                while (cur.moveToNext()) {
                                    listGallery.add(GalleryModelNew("", cur.getString(index), "", false))
                                }
                                cur.close()
                            }
                        }
                        adopterGallery.notifyItemRangeInserted(0, listGallery.size)
                        binding.mTvFilter.text = arrAlbums[i]
                    }
                }
                mAlertBuilder.setNegativeButton("Cancel", null)
                mAlertBuilder.show()
            }
        }
    }

    suspend fun loadGalleryData(): Boolean = withContext(Dispatchers.IO) {
        listGallery = ArrayList()
        listSelection = ArrayList()
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        // content:// style URI for the "primary" external storage volume
        // Make the query.
        val cur = this@GalleryActivity.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, null// Ordering
        )// Which columns to return
        // Which rows to return (all rows)
        // Selection arguments (none)
        if (cur != null && cur.moveToFirst()) {
            //                int bucketColumn = cur.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            //                int nameColumn = cur.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
            val datColum = cur.getColumnIndex(MediaStore.Images.Media.DATA)
            do {
                listGallery.add(GalleryModelNew("", cur.getString(datColum), "", false))
            } while (cur.moveToNext())
            cur.close()
            // query for albums
            val mAlbums = arrayOf("DISTINCT " + MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME)
            val mCurAlbum = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mAlbums, null, null, null)
            arrAlbums = arrayOfNulls<String?>(mCurAlbum!!.count + 1)
            arrAlbums[0] = "All Images"
            while (mCurAlbum.moveToNext()) {
                arrAlbums[mCurAlbum.position + 1] = mCurAlbum.getString(mCurAlbum.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME))
//                    mArAlbums[mCurAlbum.position] = mCurAlbum.getString(mCurAlbum.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME))
            }
            mCurAlbum.close()
        } else {
            return@withContext false
        }
        return@withContext true
    }


    companion object {
        @BindingAdapter("loadGaleryImage")
        @JvmStatic
        fun loadImage(view: ImageView, url: String) {
            Glide.with(view.context).load(File(url))
                    .apply(RequestOptions().centerCrop())
                    .apply(RequestOptions().override(200, 200))
                    .apply(RequestOptions.placeholderOf(R.drawable.placeholder_wait))
                    .apply(RequestOptions.errorOf(R.drawable.palceholer_error))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
                    .into(view)
        }
    }

}