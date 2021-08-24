package lnq.com.lnq.fragments.gallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import lnq.com.lnq.R
import lnq.com.lnq.databinding.ItemMediaBinding


class GalleryAdopter(var listData: List<GalleryModelNew>, val OnClick: onClickAdapterInterface) : RecyclerView.Adapter<GalleryAdopter.ViewHolder>() {
//    private var listDeals: MutableList<GalleryModel> = mutableListOf()
    init {
//        listDeals = List as MutableList<GalleryModel>
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val galeryItemBinding = DataBindingUtil.inflate<ItemMediaBinding>(LayoutInflater.from(parent.context),
            R.layout.item_media,
            parent,
            false
        )
        return ViewHolder(galeryItemBinding, OnClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.mFunBind(listData[position])

    override fun getItemCount(): Int {
        return listData.size
    }

    inner class ViewHolder(val binding: ItemMediaBinding, OnNoteClick: onClickAdapterInterface) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        var mOnNoteClick: onClickAdapterInterface = OnNoteClick
        fun mFunBind(item: GalleryModelNew) {
            binding.objGalery = item
            binding.executePendingBindings()
        }

        init {
//            itemView.mImg.setOnClickListener(this)
//            itemView.txtName.setOnClickListener(this)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            mOnNoteClick.onClickDealsItem(listData.get(adapterPosition))
//            when (v?.id) {
//                R.id.mImg ->{
//                    mOnNoteClick.onClick("imge",adapterPosition)
//                }
//                R.id.txtName ->{
//                    mOnNoteClick.onClick("name",adapterPosition)
//                }
//            }

        }
    }

    interface onClickAdapterInterface {
        fun onClickDealsItem(objGallery: GalleryModelNew)
    }

}
