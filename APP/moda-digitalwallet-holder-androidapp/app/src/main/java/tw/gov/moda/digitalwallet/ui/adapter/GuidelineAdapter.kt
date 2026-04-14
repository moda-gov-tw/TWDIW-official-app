package tw.gov.moda.digitalwallet.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.ItemGiudelineBinding

class GuidelineAdapter : ListAdapter<Int, GuidelineAdapter.ItemViewHolder>(object : DiffUtil.ItemCallback<Int>() {
    override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
        return false
    }

    override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
        return false
    }
}) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemGiudelineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.setItem(position)
    }


    inner class ItemViewHolder(val binding: ItemGiudelineBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setItem(position: Int) {
            when (position) {
                0 -> {
                    binding.imgHead.setImageResource(R.drawable.ic_guideline_background_1)
                    binding.tvTitle.text = itemView.context.getString(R.string.msg_guideline_title_1)
                    binding.tvContent.text = itemView.context.getString(R.string.msg_guideline_message_1)
                }

                1 -> {
                    binding.imgHead.setImageResource(R.drawable.ic_guideline_background_2)
                    binding.tvTitle.text = itemView.context.getString(R.string.msg_guideline_title_2)
                    binding.tvContent.text = itemView.context.getString(R.string.msg_guideline_message_2)
                }

                2 -> {
                    binding.imgHead.setImageResource(R.drawable.ic_guideline_background_3)
                    binding.tvTitle.text = itemView.context.getString(R.string.msg_guideline_title_3)
                    binding.tvContent.text = itemView.context.getString(R.string.msg_guideline_message_3)
                }
            }
        }
    }
}