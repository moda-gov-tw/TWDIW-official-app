package tw.gov.moda.digitalwallet.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tw.gov.moda.digitalwallet.data.db.SearchRecord
import tw.gov.moda.diw.databinding.ItemSearchRecordBinding

class SearchRecordAdapter(
    private val mSearchListener: (SearchRecord) -> Unit,
    private val mDeleteRecordListener: (SearchRecord) -> Unit
) : ListAdapter<SearchRecord, SearchRecordAdapter.ItemViewHolder>(object : DiffUtil.ItemCallback<SearchRecord>() {
    override fun areContentsTheSame(oldItem: SearchRecord, newItem: SearchRecord): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: SearchRecord, newItem: SearchRecord): Boolean {
        return oldItem == newItem
    }
}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchRecordAdapter.ItemViewHolder {
        val binding = ItemSearchRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchRecordAdapter.ItemViewHolder, position: Int) {
        holder.setItem(getItem(position))
    }

    inner class ItemViewHolder(val binding: ItemSearchRecordBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        private var mItem: SearchRecord? = null

        init {
            binding.tvContent.setOnClickListener(this)
            binding.imgDelete.setOnClickListener(this)
        }

        fun setItem(item: SearchRecord) {
            this.mItem = item
            binding.tvContent.text = item.keyword
        }


        override fun onClick(v: View?) {
            when (v) {
                binding.tvContent -> {
                    mItem?.let { mSearchListener.invoke(it) }
                }

                binding.imgDelete -> {
                    mItem?.let { mDeleteRecordListener.invoke(it) }
                }
            }
        }
    }
}