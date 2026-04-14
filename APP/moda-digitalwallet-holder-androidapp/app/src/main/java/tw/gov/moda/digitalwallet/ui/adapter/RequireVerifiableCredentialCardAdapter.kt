package tw.gov.moda.digitalwallet.ui.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tw.gov.moda.digitalwallet.common.AppConstants
import tw.gov.moda.digitalwallet.data.element.VerifiableCredentialGroupRuleEnum
import tw.gov.moda.digitalwallet.data.model.RequireVerifiableCredentialCard
import tw.gov.moda.digitalwallet.data.model.RequireVerifiableCredentialField
import tw.gov.moda.digitalwallet.extension.addItemDecoration
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.ItemRequireVerifiableCredentialCardBinding

class RequireVerifiableCredentialCardAdapter(
    private val mCheckAllListener: (RequireVerifiableCredentialCard, Boolean) -> Unit,
    private val mCheckItemListener: (RequireVerifiableCredentialCard, RequireVerifiableCredentialField, Boolean) -> Unit,
    private val mExpandItemListener: (RequireVerifiableCredentialCard, Boolean) -> Unit,
    private val mChildCommitCallback: () -> Unit,
    private val mIsHidden: Boolean,
    private val mRule: String?,
) : ListAdapter<RequireVerifiableCredentialCard, RequireVerifiableCredentialCardAdapter.RequireVerifiableCredentialNameViewHolder>(
    object : DiffUtil.ItemCallback<RequireVerifiableCredentialCard> () {
        override fun areItemsTheSame(
            oldItem: RequireVerifiableCredentialCard,
            newItem: RequireVerifiableCredentialCard
        ): Boolean {
            return false
        }

        override fun areContentsTheSame(
            oldItem: RequireVerifiableCredentialCard,
            newItem: RequireVerifiableCredentialCard
        ): Boolean {
            return oldItem == newItem
        }

    }
) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RequireVerifiableCredentialNameViewHolder {
        return RequireVerifiableCredentialNameViewHolder(ItemRequireVerifiableCredentialCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(
        holder: RequireVerifiableCredentialNameViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    inner class RequireVerifiableCredentialNameViewHolder(val binding: ItemRequireVerifiableCredentialCardBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        private var mItem: RequireVerifiableCredentialCard? = null

        init {
            binding.imgToggleExpand.setOnClickListener(this)
            binding.tvSelectAll.setOnClickListener(this)
            binding.recyclerView.addItemDecoration(R.drawable.shape_line_gray02)
        }

        fun bind(item: RequireVerifiableCredentialCard) {
            mItem = item
            if (item.isExpand) {
                binding.tvRequireIssuer.isVisible = true
                binding.tvSelectAll.isVisible = true
                binding.tvPreview.isVisible = false
                binding.recyclerView.isVisible = true
                binding.imgToggleExpand.isSelected = false
            } else {
                binding.tvRequireIssuer.isVisible = false
                binding.tvSelectAll.isVisible = false
                binding.tvPreview.isVisible = true
                binding.recyclerView.isVisible = false
                binding.imgToggleExpand.isSelected = true
            }

            binding.tvCardName.text = item.title

            binding.tvRequireIssuer.text = item.issuer
            binding.recyclerView.layoutManager = LinearLayoutManager(binding.root.context, RecyclerView.VERTICAL, false)
            binding.recyclerView.adapter = RequireVerifiableCredentialFieldAdapter(mRule = mRule, mIsHidden = mIsHidden, mCheckItemListener = { field, isCheck ->
                mItem?.also { mCheckItemListener.invoke(it, field, isCheck) }
            })
            (binding.recyclerView.adapter as? RequireVerifiableCredentialFieldAdapter)?.submitList(item.fields){
                mChildCommitCallback.invoke()
            }

            if (mIsHidden) {
                binding.tvPreview.text = AppConstants.Common.MARK_TEXT
            } else {
                binding.tvPreview.text = item.fields.map { it.value }.filter { it.isNotEmpty() }.joinToString("、")
            }

            if (mRule == VerifiableCredentialGroupRuleEnum.ALL.code) {
                binding.tvSelectAll.isVisible = false
            } else {
                if (item.isCheckAll) {
                    binding.tvSelectAll.text = ContextCompat.getString(binding.root.context, R.string.unselect_all)
                } else {
                    binding.tvSelectAll.text = ContextCompat.getString(binding.root.context, R.string.select_all)
                }
            }
        }

        override fun onClick(v: View?) {
            when(v) {
                binding.imgToggleExpand -> {
                    mItem?.also { mExpandItemListener.invoke(it, it.isExpand.not()) }
                }
                binding.tvSelectAll -> {
                    mItem?.also { mCheckAllListener.invoke(it, it.isCheckAll.not()) }
                }
            }
        }
    }
}
