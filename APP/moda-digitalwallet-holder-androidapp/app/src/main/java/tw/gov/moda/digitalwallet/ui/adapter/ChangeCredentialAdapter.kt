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
import tw.gov.moda.digitalwallet.data.element.SelectVerifiableCredentialStatusEnum
import tw.gov.moda.digitalwallet.data.model.ChangeVerifiableCredentialCardGroup
import tw.gov.moda.digitalwallet.extension.addItemDecoration
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.ItemChangeCredentialCardBinding
import tw.gov.moda.diw.databinding.ItemChangeMultiCredentialCardGroupBinding

class ChangeCredentialAdapter(
    private val mItemCheckListener: (Int, Long, Boolean) -> Unit,
    private val mItemExpandListener: (Int, Boolean) -> Unit
) : ListAdapter<ChangeVerifiableCredentialCardGroup, RecyclerView.ViewHolder>(object : DiffUtil.ItemCallback<ChangeVerifiableCredentialCardGroup>() {
    override fun areItemsTheSame(
        oldItem: ChangeVerifiableCredentialCardGroup,
        newItem: ChangeVerifiableCredentialCardGroup
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ChangeVerifiableCredentialCardGroup, newItem: ChangeVerifiableCredentialCardGroup): Boolean {
        return false
    }

}) {
    private var mContentVisible = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            SINGLE -> ChangeSingleCredentialGroupViewHolder(ItemChangeCredentialCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            MULTI -> ChangeMultiCredentialGroupViewHolder(ItemChangeMultiCredentialCardGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> throw IllegalArgumentException("viewType error")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if (holder is ChangeMultiCredentialGroupViewHolder) {
            holder.bind(item)
        } else if (holder is ChangeSingleCredentialGroupViewHolder) {
            holder.bind(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            getItem(position).cardList.size == 1 -> SINGLE
            else -> MULTI
        }
    }

    fun setVisible(isVisible: Boolean) {
        mContentVisible = isVisible
        notifyDataSetChanged()
    }

    private companion object {
        private const val SINGLE = 0
        private const val MULTI = 1
    }

    inner class ChangeSingleCredentialGroupViewHolder(val binding: ItemChangeCredentialCardBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        private var mItem: ChangeVerifiableCredentialCardGroup? = null

        init {
            binding.recyclerView.itemAnimator = null
            binding.recyclerView.addItemDecoration(R.drawable.shape_line_gray02)
            binding.imgRadioBtn.setOnClickListener(this)
            binding.imgToggleExpand.setOnClickListener(this)
        }

        fun bind(item: ChangeVerifiableCredentialCardGroup) {
            mItem = item

            val card = item.cardList.first()
            binding.viewMask.isVisible = item.clickStatus != SelectVerifiableCredentialStatusEnum.ReadyToClick
            binding.tvCardName.text = item.title
            binding.tvRequireIssuer.text = item.issuer

            if (card.isChecked) {
                binding.imgRadioBtn.setImageResource(R.drawable.ic_radio_button_selected)
            } else {
                binding.imgRadioBtn.setImageResource(R.drawable.ic_radio_button_enable)
            }

            if (item.isExpand) {
                binding.imgToggleExpand.isSelected = false
                binding.tvPreview.isVisible = false
                binding.recyclerView.isVisible = true
            } else {
                binding.imgToggleExpand.isSelected = true
                binding.tvPreview.isVisible = true
                binding.recyclerView.isVisible = false
            }

            binding.tvPreview.text = if (mContentVisible) {
                AppConstants.Common.MARK_TEXT
            } else {
                card.fieldList.map { it.value }.filter { it.isNotEmpty() }.joinToString("、")
            }

            binding.recyclerView.layoutManager = LinearLayoutManager(binding.root.context, RecyclerView.VERTICAL, false)
            binding.recyclerView.adapter = ChangeCredentialFieldAdapter()
            (binding.recyclerView.adapter as? ChangeCredentialFieldAdapter)?.setVisible(mContentVisible)
            (binding.recyclerView.adapter as? ChangeCredentialFieldAdapter)?.submitList(card.fieldList)

        }

        override fun onClick(v: View?) {
            when(v) {
                binding.imgRadioBtn -> {
                    mItem?.also { mItemCheckListener.invoke(adapterPosition, it.cardList.first().uid, it.cardList.first().isChecked.not()) }
                }
                binding.imgToggleExpand -> {
                    mItem?.also { mItemExpandListener.invoke(it.id, it.isExpand.not()) }
                }
            }
        }

    }

    inner class ChangeMultiCredentialGroupViewHolder(val binding: ItemChangeMultiCredentialCardGroupBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        private var mItem: ChangeVerifiableCredentialCardGroup? = null

        init {
            binding.recyclerView.itemAnimator = null
            binding.recyclerView.addItemDecoration(R.drawable.shape_line_gray02)
            binding.layExpand.setOnClickListener(this)
        }

        fun bind(item: ChangeVerifiableCredentialCardGroup) {
            mItem = item
            binding.viewMask.isVisible = (item.clickStatus != SelectVerifiableCredentialStatusEnum.ReadyToClick)
            binding.tvCardName.text = item.title
            binding.tvRequireIssuer.text = item.issuer

            if (item.isExpand) {
                binding.tvToggleExpand.text = binding.root.context.getString(R.string.collapse)
                binding.imgToggleExpand.isSelected = false
            } else {
                binding.tvToggleExpand.text = binding.root.context.getString(R.string.expand)
                binding.imgToggleExpand.isSelected = true
            }

            binding.recyclerView.layoutManager = LinearLayoutManager(binding.root.context, RecyclerView.VERTICAL, false)
            binding.recyclerView.adapter = ChangeCredentialCardAdapter (
                mItemCheckListener = { index, uid, isChecked ->
                    mItemCheckListener.invoke(adapterPosition, uid, isChecked)
                }
            )
            (binding.recyclerView.adapter as? ChangeCredentialCardAdapter)?.setHidden(mContentVisible)
            (binding.recyclerView.adapter as? ChangeCredentialCardAdapter)?.setIsExpand(item.isExpand)
            (binding.recyclerView.adapter as? ChangeCredentialCardAdapter)?.submitList(item.cardList)

        }

        override fun onClick(v: View?) {
            mItem?.also { mItemExpandListener.invoke(it.id, it.isExpand.not()) }
        }
    }

}