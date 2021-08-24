package lnq.com.lnq.fragments.gallery

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_gallery_new.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lnq.com.lnq.R
import lnq.com.lnq.databinding.FragmentGalleryNewBinding
import java.io.File
import java.util.*


class GalleryFragmentFunctions  {
    lateinit var binding: FragmentGalleryNewBinding
    var listGallery: MutableList<GalleryModelNew> = ArrayList()
    lateinit var adopterGallery: GalleryAdopter
    var listSelection: MutableList<String> = ArrayList()

    //    lateinit var context: Context
    lateinit var arrAlbums: Array<String?>
    private var posOld: Int = 0
    var flag: String = "single"

//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
//                              savedInstanceState: Bundle?): View? {
////        (dialog as? BottomSheetDialog)?.let {
////            it.behavior.peekHeight = PEEK_HEIGHT
////        }
//        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gallery_new, container, false)
//        return binding.getRoot()
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        init()
//    }
//
//    private fun init() {
//        flag = arguments?.getString("mFlag", "").toString()
//        binding.mImgDone.setOnClickListener(this)
//        binding.mTvFilter.setOnClickListener(this)
//        binding.mTvTitle.setOnClickListener(this)
//        binding.imageViewArrowDown.setOnClickListener(this)
//        GlobalScope.launch(Dispatchers.Main) {
////            delay(5000)
//            val result = loadGalleryData()
//            if (result) {
//                binding.mRv.layoutManager = GridLayoutManager(context, 4)
//                listGallery.reverse()
//                adopterGallery = GalleryAdopter(listGallery, this@GalleryFragmentFunctions)
//                binding.mRv.adapter = adopterGallery
//                if (flag.contentEquals("single")) {
//                    listSelection.add("")
//                }
//            } else {
//                Toast.makeText(context, "No Pictures Found", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
////    private val mBottomSheetBehaviorCallback: BottomSheetCallback = object : BottomSheetCallback() {
////        override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {
////            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
////                dismiss()
////            }
////        }
////
////        override fun onSlide(@NonNull bottomSheet: View, slideOffset: Float) {}
////    }
////
////    @SuppressLint("RestrictedApi")
////    override fun setupDialog(dialog: Dialog, style: Int) {
////        super.setupDialog(dialog, style)
////        val contentView = View.inflate(context, R.layout.fragment_gallery_new, null)
////        dialog.setContentView(contentView)
////        val mBottomSheetBehavior = BottomSheetBehavior.from(contentView.parent as View)
////        if (mBottomSheetBehavior != null) {
////            mBottomSheetBehavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
////            mBottomSheetBehavior.setPeekHeight(200)
////            contentView.requestLayout()
////        }
////    }
//
//    public override fun onStart() {
//        super.onStart()
//    }
//
//    public override fun onStop() {
//        super.onStop()
//    }
//
//    override fun onClickDealsItem(objGallery: GalleryModelNew) {
//        val pos = listGallery.indexOf(objGallery)
//        when (flag) {
//            "multiple" -> if (listSelection.contains(objGallery.path)) {
//                objGallery.isSelected = false
//                listSelection.remove(objGallery.path)
//                adopterGallery.notifyItemChanged(pos)
//            } else {
//                listGallery[pos].isSelected = true
//                listSelection.add(listGallery[pos].path.toString())
//                adopterGallery.notifyItemChanged(pos)
//            }
//            "single" -> {
//                if (listSelection.contains(listGallery[pos].path)) {
//                    return
//                }
//                listGallery[posOld].isSelected = false
//                adopterGallery.notifyItemChanged(posOld)
//                listSelection.set(0, listGallery[pos].path.toString())
//                listGallery[pos].isSelected = true
//                adopterGallery.notifyItemChanged(pos)
//                posOld = pos
//            }
//        }
////        binding.mTvTitle.text = "" + listSelection!!.size
//    }
//
//    override fun onClick(view: View) {
//        when (view.id) {
//            R.id.mImgDone -> when (flag) {
//                "multiple" -> if (listSelection.size == 0) {
//                    Toast.makeText(context, "Please Select Atleast One Picture", Toast.LENGTH_SHORT).show()
//                } else {
//                    setFragmentResult("requestKey", bundleOf("bundleKey" to listSelection))
//                }
//                "single" -> if (listSelection.size == 0) {
//                    Toast.makeText(context, "Please Select Atleast One Picture", Toast.LENGTH_SHORT).show()
//                } else {
////                    val mGson = Gson()
////                    val returnIntent = Intent()
////                    returnIntent.putExtra("result", mGson.toJson(listSelection))
////                    setResult(Activity.RESULT_OK, returnIntent)
////                    finish()
////                    EventBus.getDefault().post(EventBusOpenGalleryNew())
//
//                    setFragmentResult("requestKey", bundleOf("bundleKey" to listSelection))
//                }
//            }
//            R.id.mTvTitle,
//            R.id.imageViewArrowDown -> {
//                val mAlertBuilder: AlertDialog.Builder
//                mAlertBuilder = AlertDialog.Builder(requireActivity(), R.style.MyAlertDialogStyle)
//                mAlertBuilder.setItems(arrAlbums) { dialogInterface, i ->
//                    if (!binding.mTvTitle.text.toString().contentEquals(arrAlbums[i].toString())) {
//                        listSelection.clear()
//                        if (flag.contentEquals("single")) {
//                            listSelection.add("")
//                        }
////                        binding.mTvTitle.text = "0"
//                        val mSizeLst = listGallery.size
//                        listGallery.clear()
//                        adopterGallery.notifyItemRangeRemoved(0, mSizeLst)
//                        val projection = arrayOf(MediaStore.Images.Media.DATA)
//                        when (arrAlbums[i]) {
//                            "All Images" -> {
//                                val curAll = requireActivity().contentResolver.query(
//                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                                        projection, // Selection arguments (none)
//                                        null        // Ordering
//                                        , null, null// Ordering
//                                )// Which columns to return
//                                // Which rows to return (all rows)
//                                // Selection arguments (none)
//                                if (curAll != null && curAll.moveToFirst()) {
//                                    val datColum = curAll.getColumnIndex(MediaStore.Images.Media.DATA)
//                                    do {
//                                        listGallery.add(GalleryModelNew("", curAll.getString(datColum), "", false))
//                                    } while (curAll.moveToNext())
//                                    curAll.close()
//                                } else {
//                                    Toast.makeText(context, "No Pictures Found", Toast.LENGTH_SHORT).show()
//                                }
//                            }
//                            else -> {
//                                val cur = requireActivity().contentResolver.query(
//                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                                        projection, // Which columns to return
//                                        MediaStore.Images.Media.DATA + " like ? ", // Which rows to return (all rows)
//                                        arrayOf("%" + arrAlbums[i] + "%"), null
//                                )
//                                val index = cur!!.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
//                                while (cur.moveToNext()) {
//                                    listGallery.add(GalleryModelNew("", cur.getString(index), "", false))
//                                }
//                                cur.close()
//                            }
//                        }
//                        listGallery.reverse()
//                        adopterGallery.notifyItemRangeInserted(0, listGallery.size)
//                        binding.mTvTitle.text = arrAlbums[i]
//                    }
//                }
//                mAlertBuilder.setNegativeButton("Cancel", null)
//                mAlertBuilder.show()
//            }
//            R.id.mTvFilter -> {
//                dismiss()
//            }
//        }
//    }

     fun loadGalleryData(context: Context) {
//        listGallery = ArrayList()
        listSelection = ArrayList()
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        // content:// style URI for the "primary" external storage volume
        // Make the query.
        val cur = context.contentResolver.query(
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
                listGallery.reverse()
            } while (cur.moveToNext())
            cur.close()
            // query for albums
            val mAlbums = arrayOf("DISTINCT " + MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME)
            val mCurAlbum = context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mAlbums, null, null, null)
            arrAlbums = arrayOfNulls<String?>(mCurAlbum!!.count + 1)
            arrAlbums[0] = "All Images"
            while (mCurAlbum.moveToNext()) {
                arrAlbums[mCurAlbum.position + 1] = mCurAlbum.getString(mCurAlbum.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME))
//                    mArAlbums[mCurAlbum.position] = mCurAlbum.getString(mCurAlbum.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME))
            }
            mCurAlbum.close()
        }
    }

    fun onUploadClick(context: Activity): MutableList<String> {
        if (listSelection.size == 0) {
            Toast.makeText(context, "Please Select Atleast One Picture", Toast.LENGTH_SHORT).show()
        } else {
            val mGson = Gson()
            val returnIntent = Intent()
            returnIntent.putExtra("result", mGson.toJson(listSelection))
            context.setResult(Activity.RESULT_OK, returnIntent)
        }
        return listSelection
    }

    fun onFoldersClick(context: Context, text: TextView, adopter: GalleryAdopter): TextView {
        val mAlertBuilder: AlertDialog.Builder
        mAlertBuilder = AlertDialog.Builder(context, R.style.MyAlertDialogStyle)
        mAlertBuilder.setItems(arrAlbums) { dialogInterface, i ->
            if (!text.text.toString().contentEquals(arrAlbums[i].toString())) {
                listSelection.clear()
                if (flag.contentEquals("single")) {
                    listSelection.add("")
                }
//                        binding.mTvTitle.text = "0"
                val mSizeLst = listGallery.size
                listGallery.clear()
                adopter.notifyItemRangeRemoved(0, mSizeLst)
                val projection = arrayOf(MediaStore.Images.Media.DATA)
                when (arrAlbums[i]) {
                    "All Images" -> {
                        val curAll = context.contentResolver.query(
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
                        val cur = context.contentResolver.query(
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
                listGallery.reverse()
                adopter.notifyItemRangeInserted(0, listGallery.size)
                text.text = arrAlbums[i]
            }
        }
        mAlertBuilder.setNegativeButton("Cancel", null)
        mAlertBuilder.show()

        return text
    }

//    companion object {
//        @BindingAdapter("loadGaleryImage")
//        @JvmStatic
//        fun loadImage(view: ImageView, url: String) {
//            Glide.with(view.context).load(File(url))
//                    .apply(RequestOptions().centerCrop())
//                    .apply(RequestOptions().override(200, 200))
//                    .apply(RequestOptions.placeholderOf(R.drawable.placeholder_wait))
//                    .apply(RequestOptions.errorOf(R.drawable.palceholer_error))
//                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
//                    .into(view)
//        }
//
//        @JvmStatic
//        fun newInstance(retrieveString: String?): GalleryFragmentFunctions? {
//            val f = GalleryFragmentFunctions()
//            val args = Bundle()
//            args.putString("mFlag", retrieveString)
//            f.setArguments(args)
//            return f
//        }
//    }
}