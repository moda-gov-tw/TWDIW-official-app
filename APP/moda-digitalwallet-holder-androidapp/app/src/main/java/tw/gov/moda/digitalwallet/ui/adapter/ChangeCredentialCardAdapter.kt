package tw.gov.moda.digitalwallet.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tw.gov.moda.digitalwallet.common.AppConstants
import tw.gov.moda.digitalwallet.data.model.MultiChangeVerifiableCredentialCard
import tw.gov.moda.digitalwallet.extension.addItemDecoration
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.ItemChangeMultiCredentialCardBinding

class ChangeCredentialCardAdapter(
    private val mItemCheckListener: (Int, Long, Boolean) -> Unit
) : ListAdapter<MultiChangeVerifiableCredentialCard, ChangeCredentialCardAdapter.ChangeCredentialCardViewHolder>(
    object : DiffUtil.ItemCallback<MultiChangeVerifiableCredentialCard>() {
        override fun areItemsTheSame(
            oldItem: MultiChangeVerifiableCredentialCard,
            newItem: MultiChangeVerifiableCredentialCard
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: MultiChangeVerifiableCredentialCard,
            newItem: MultiChangeVerifiableCredentialCard
        ): Boolean {
            return oldItem.uid == newItem.uid && oldItem.isChecked == newItem.isChecked
        }

    }
)  {

    private var mIsHidden: Boolean = false
    private var mIsExpand: Boolean = false

    fun setHidden(isHidden: Boolean) {
        mIsHidden = isHidden
    }

    fun setIsExpand(isExpand: Boolean) {
        mIsExpand = isExpand
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChangeCredentialCardViewHolder {
        return ChangeCredentialCardViewHolder(ItemChangeMultiCredentialCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ChangeCredentialCardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ChangeCredentialCardViewHolder(val binding: ItemChangeMultiCredentialCardBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        private var mItem: MultiChangeVerifiableCredentialCard? = null

        init {
            binding.recyclerView.itemAnimator = null
            binding.recyclerView.addItemDecoration(R.drawable.shape_line_gray02)
            binding.imgRadioBtn.setOnClickListener(this)
        }

        fun bind(item: MultiChangeVerifiableCredentialCard) {
            mItem = item
            if (item.isChecked) {
                binding.imgRadioBtn.setImageResource(R.drawable.ic_radio_button_selected)
            } else {
                binding.imgRadioBtn.setImageResource(R.drawable.ic_radio_button_enable)
            }
            binding.tvPreview.isVisible = mIsExpand.not()
            binding.tvPreview.text = if (mIsHidden) {
                AppConstants.Common.MARK_TEXT
            } else {
                item.fieldList.map { it.value }.filter { it.isNotEmpty() }.joinToString("、")
            }

            binding.recyclerView.isVisible = mIsExpand
            binding.recyclerView.layoutManager = LinearLayoutManager(binding.root.context, RecyclerView.VERTICAL, false)
            binding.recyclerView.adapter = ChangeCredentialFieldAdapter ()
            (binding.recyclerView.adapter as? ChangeCredentialFieldAdapter)?.setVisible(mIsHidden)
            (binding.recyclerView.adapter as? ChangeCredentialFieldAdapter)?.submitList(item.fieldList)
        }

        override fun onClick(v: View?) {
            mItem?.also { mItemCheckListener.invoke(adapterPosition, it.uid, it.isChecked.not()) }
        }
    }
}