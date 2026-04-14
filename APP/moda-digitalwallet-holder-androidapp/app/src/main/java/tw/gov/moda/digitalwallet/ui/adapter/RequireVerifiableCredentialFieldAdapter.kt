package tw.gov.moda.digitalwallet.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tw.gov.moda.digitalwallet.common.AppConstants
import tw.gov.moda.digitalwallet.data.element.VerifiableCredentialGroupRuleEnum
import tw.gov.moda.digitalwallet.data.model.RequireVerifiableCredentialField
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.ItemRequireVerifiableCredentialFieldBinding

class RequireVerifiableCredentialFieldAdapter(
    private val mCheckItemListener: (RequireVerifiableCredentialField, Boolean) -> Unit,
    private val mIsHidden: Boolean,
    private val mRule: String?,
) : ListAdapter<RequireVerifiableCredentialField, RequireVerifiableCredentialFieldAdapter.RequireVerifiableCredentialFieldViewHolder>(
    object : DiffUtil.ItemCallback<RequireVerifiableCredentialField>() {
        override fun areItemsTheSame(
            oldItem: RequireVerifiableCredentialField,
            newItem: RequireVerifiableCredentialField
        ): Boolean {
            return false
        }

        override fun areContentsTheSame(
            oldItem: RequireVerifiableCredentialField,
            newItem: RequireVerifiableCredentialField
        ): Boolean {
            return oldItem == newItem
        }

    }
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RequireVerifiableCredentialFieldViewHolder {
        return RequireVerifiableCredentialFieldViewHolder(
            ItemRequireVerifiableCredentialFieldBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: RequireVerifiableCredentialFieldViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    inner class RequireVerifiableCredentialFieldViewHolder(val binding: ItemRequireVerifiableCredentialFieldBinding) :
        RecyclerView.ViewHolder(binding.root), CompoundButton.OnCheckedChangeListener {

        private var mItem: RequireVerifiableCredentialField? = null

        init {
            binding.chkCardField.setOnCheckedChangeListener(this)
        }

        fun bind(item: RequireVerifiableCredentialField) {
            mItem = item

            binding.tvCardField.text = item.title
            if (mIsHidden) {
                binding.tvCardValue.text = AppConstants.Common.MARK_TEXT
            } else {
                binding.tvCardValue.text = item.value
            }
            if (mRule == VerifiableCredentialGroupRuleEnum.ALL.code) {
                binding.chkCardField.setButtonDrawable(R.drawable.ic_checkbox_select_disable)
                binding.chkCardField.isEnabled = false
            } else {
                binding.chkCardField.setOnCheckedChangeListener(null)
                binding.chkCardField.isEnabled = true
                binding.chkCardField.isChecked = item.isCheck
                binding.chkCardField.setOnCheckedChangeListener(this)
            }
        }

        override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
            mItem?.also {
                mCheckItemListener.invoke(it, isChecked)
            }
        }
    }
}
