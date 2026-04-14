package tw.gov.moda.digitalwallet.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tw.gov.moda.digitalwallet.data.db.FavoriteShowCredential
import tw.gov.moda.digitalwallet.data.model.dwmoda.DwModa401i
import tw.gov.moda.diw.databinding.ItemShowCredentialBinding

class QuickAuthorizationListAdapter(
    private val mOpenPageListener: (DwModa401i.Response.VPItem) -> Unit,
    private val mAddToFavoriteListener: (DwModa401i.Response.VPItem) -> Unit
) : ListAdapter<DwModa401i.Response.VPItem, QuickAuthorizationListAdapter.ItemViewHolder>(object : DiffUtil.ItemCallback<DwModa401i.Response.VPItem>() {
    override fun areContentsTheSame(oldItem: DwModa401i.Response.VPItem, newItem: DwModa401i.Response.VPItem): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: DwModa401i.Response.VPItem, newItem: DwModa401i.Response.VPItem): Boolean {
        return oldItem == newItem
    }
}) {

    private var favoriteList: List<DwModa401i.Response.VPItem> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuickAuthorizationListAdapter.ItemViewHolder {
        val binding = ItemShowCredentialBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuickAuthorizationListAdapter.ItemViewHolder, position: Int) {
        holder.setItem(getItem(position))
    }

    fun setFavorites(favorites: List<DwModa401i.Response.VPItem>) {
        favoriteList = favorites
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(val binding: ItemShowCredentialBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        private var mItem: DwModa401i.Response.VPItem? = null

        init {
            binding.tvTitle.setOnClickListener(this)
            binding.imgStar.setOnClickListener(this)
        }

        fun setItem(item: DwModa401i.Response.VPItem) {
            this.mItem = item
            binding.tvTitle.text = item.name
            val isFavorite = favoriteList.any { it.name == item.name }
            binding.imgStar.isSelected = isFavorite
        }


        override fun onClick(v: View?) {
            when (v) {
                binding.tvTitle -> {
                    mItem?.let { mOpenPageListener.invoke(it) }
                }

                binding.imgStar -> {
                    mItem?.let { mAddToFavoriteListener.invoke(it) }
                }
            }
        }
    }
}