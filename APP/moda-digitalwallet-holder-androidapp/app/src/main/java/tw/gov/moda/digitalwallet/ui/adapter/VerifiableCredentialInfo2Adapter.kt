package tw.gov.moda.digitalwallet.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tw.gov.moda.digitalwallet.common.AppConstants
import tw.gov.moda.digitalwallet.data.model.VerifiableCredentialDisplay
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.ItemCardInformationBinding

class VerifiableCredentialInfo2Adapter(val statusChange: (status: Boolean) -> Unit) : ListAdapter<VerifiableCredentialDisplay, VerifiableCredentialInfo2Adapter.ItemViewHolder>(object : DiffUtil.ItemCallback<VerifiableCredentialDisplay>() {
    override fun areContentsTheSame(oldItem: VerifiableCredentialDisplay, newItem: VerifiableCredentialDisplay): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: VerifiableCredentialDisplay, newItem: VerifiableCredentialDisplay): Boolean {
        return oldItem == newItem
    }
}) {

    private var isHidden = true
    private val visibility = mutableMapOf<Int, Boolean>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerifiableCredentialInfo2Adapter.ItemViewHolder {
        val binding = ItemCardInformationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VerifiableCredentialInfo2Adapter.ItemViewHolder, position: Int) {
        holder.setItem(getItem(position),position)

    }


    inner class ItemViewHolder(val binding: ItemCardInformationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setItem(item: VerifiableCredentialDisplay,position: Int) {
            binding.tvTitle.text = item.title
            binding.tvValue.text = item.value
            if (isHidden) {
                // 隱藏全部
                val isVisible = visibility[position] ?: true
                if (isVisible) {
                    binding.tvValue.text = item.value
                    binding.imgEye.setImageResource(R.drawable.ic_eye_on)
                } else {
                    binding.tvValue.text = AppConstants.Common.MARK_TEXT
                    binding.imgEye.setImageResource(R.drawable.ic_eye_off)
                }
            } else {
                // 顯示全部
                val isVisible = visibility[position] ?: false
                if (isVisible) {
                    binding.tvValue.text = item.value
                    binding.imgEye.setImageResource(R.drawable.ic_eye_on)
                } else {
                    binding.tvValue.text = AppConstants.Common.MARK_TEXT
                    binding.imgEye.setImageResource(R.drawable.ic_eye_off)
                }
            }
            binding.imgEye.setOnClickListener {
                val newVisibility = binding.imgEye.drawable.constantState != (ContextCompat.getDrawable(binding.root.context, R.drawable.ic_eye_on)?.constantState)
                visibility[position] = newVisibility
                var newIsHidden = true
                visibility.forEach { (_, visible) ->
                    if (!visible) {
                        newIsHidden = false
                    }
                }
                isHidden = newIsHidden
                statusChange.invoke(isHidden)
                notifyItemChanged(position)
            }
        }
    }
    fun hideAllTextViews() {
        isHidden = !isHidden
        visibility.clear()
        statusChange.invoke(isHidden)
        notifyDataSetChanged()
    }
}