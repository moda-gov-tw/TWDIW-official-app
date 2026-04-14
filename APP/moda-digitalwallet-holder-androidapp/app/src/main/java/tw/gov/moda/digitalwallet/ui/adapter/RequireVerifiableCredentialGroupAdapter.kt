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
import tw.gov.moda.digitalwallet.data.element.VerifiableCredentialGroupEnum
import tw.gov.moda.digitalwallet.data.element.VerifiableCredentialGroupRuleEnum
import tw.gov.moda.digitalwallet.data.model.RequireVerifiableCredentialField
import tw.gov.moda.digitalwallet.data.model.RequireVerifiableCredentialGroup
import tw.gov.moda.digitalwallet.data.model.RequireVerifiableCredentialCard
import tw.gov.moda.digitalwallet.extension.addItemDecoration
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.ItemRequireVerifiableCredentialGroupBinding

class RequireVerifiableCredentialGroupAdapter(
    private val mCheckAllListener: (RequireVerifiableCredentialGroup, RequireVerifiableCredentialCard, Boolean) -> Unit,
    private val mCheckItemListener: (RequireVerifiableCredentialGroup, RequireVerifiableCredentialCard, RequireVerifiableCredentialField, Boolean) -> Unit,
    private val mChangeCardListener: (RequireVerifiableCredentialGroup, RequireVerifiableCredentialCard?) -> Unit,
    private val mExpandListener: (RequireVerifiableCredentialGroup, RequireVerifiableCredentialCard, Boolean) -> Unit,
    private val mShowAuthorizedListListener: (RequireVerifiableCredentialGroup) -> Unit,
    private val mChildCommitCallback: () -> Unit
) : ListAdapter<RequireVerifiableCredentialGroup, RequireVerifiableCredentialGroupAdapter.ItemViewHolder>(object : DiffUtil.ItemCallback<RequireVerifiableCredentialGroup>() {
    override fun areContentsTheSame(oldItem: RequireVerifiableCredentialGroup, newItem: RequireVerifiableCredentialGroup): Boolean {
        return false
    }

    override fun areItemsTheSame(oldItem: RequireVerifiableCredentialGroup, newItem: RequireVerifiableCredentialGroup): Boolean {
        return oldItem == newItem
    }
}) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequireVerifiableCredentialGroupAdapter.ItemViewHolder {
        val binding = ItemRequireVerifiableCredentialGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RequireVerifiableCredentialGroupAdapter.ItemViewHolder, position: Int) {
        holder.setItem(getItem(position))
    }

    inner class ItemViewHolder(val binding: ItemRequireVerifiableCredentialGroupBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        private var mItem: RequireVerifiableCredentialGroup? = null

        init {
            binding.tvShowAuthorizedVerificationCard.setOnClickListener(this)
            binding.tvChangeVerificationCard.setOnClickListener(this)
            binding.recyclerView.addItemDecoration(R.drawable.shape_line_gray02)
        }

        fun setItem(item: RequireVerifiableCredentialGroup) {
            mItem = item

            binding.tvCardGroup.text = item.title
            binding.viewBottomLine.isVisible = item.isShowChangeVC
            binding.layBottomSelect.isVisible = item.isShowChangeVC
            binding.viewBottomLine2.isVisible = item.isShowAuthorizedVCTitle
            if (item.isShowChangeVC) {
                binding.layBottomSelect.isVisible = true
                if (item.rule == VerifiableCredentialGroupRuleEnum.ALL.code) {
                    binding.imgNotSelectVerificationCard.isVisible = true
                    binding.tvNotSelectVerificationCard.setTextColor(ContextCompat.getColor(binding.root.context, R.color.secondary_06))
                } else {
                    binding.imgNotSelectVerificationCard.isVisible = false
                    binding.tvNotSelectVerificationCard.setTextColor(ContextCompat.getColor(binding.root.context, R.color.gray_06))
                }

            } else {
                binding.layBottomSelect.isVisible = false
                binding.imgNotSelectVerificationCard.isVisible = false
            }
            if (item.isShowAuthorizedVCTitle) {
                binding.layBottom.isVisible = true
                binding.tvShowAuthorizedVerificationCard.isVisible = item.isShowAuthorizedVC
                if (item.rule == VerifiableCredentialGroupRuleEnum.ALL.code) {
                    binding.tvNonVerification.setTextColor(ContextCompat.getColor(binding.root.context, R.color.secondary_06))
                    binding.imgNonVerification.isVisible = true
                } else {
                    binding.tvNonVerification.setTextColor(ContextCompat.getColor(binding.root.context, R.color.gray_06))
                    binding.imgNonVerification.isVisible = false
                }
            } else {
                binding.layBottom.isVisible = false
                binding.tvShowAuthorizedVerificationCard.isVisible = false
            }

            when (item.type) {
                VerifiableCredentialGroupEnum.Default -> {
                    binding.viewContainer.background = ContextCompat.getDrawable(binding.root.context, R.drawable.shape_r12_item_background_selected_2)
                    binding.recyclerView.isVisible = true
                    binding.recyclerView.layoutManager = LinearLayoutManager(binding.root.context, RecyclerView.VERTICAL, false)
                    binding.recyclerView.adapter = RequireVerifiableCredentialCardAdapter(
                        mIsHidden = item.isHidden,
                        mRule = item.rule,
                        mCheckItemListener = { name, field, isCheck ->
                            mItem?.also { mCheckItemListener.invoke(it, name, field, isCheck) }
                        },
                        mCheckAllListener = { name, isCheck ->
                            mItem?.also { mCheckAllListener.invoke(it, name, isCheck) }
                        },
                        mExpandItemListener = { name, isCheck ->
                            mItem?.also { mExpandListener.invoke(it, name, isCheck) }
                        },
                        mChildCommitCallback = {
                            mChildCommitCallback.invoke()
                        }
                    )
                    (binding.recyclerView.adapter as? RequireVerifiableCredentialCardAdapter)?.submitList(item.cardList)
                }
                VerifiableCredentialGroupEnum.Empty -> {
                    binding.viewContainer.background = ContextCompat.getDrawable(binding.root.context, R.drawable.shape_r12_item_background_selected_4)
                    binding.recyclerView.isVisible = false
                    binding.viewBottomLine.isVisible = false
                    if (item.isShowChangeVC) {
                        binding.viewBottomLine2.isVisible = true
                    } else {
                        binding.viewBottomLine2.isVisible = false
                    }
                    mChildCommitCallback.invoke()
                }
            }

        }

        override fun onClick(v: View?) {
            when(v) {
                binding.tvShowAuthorizedVerificationCard -> {
                    mItem?.also { mShowAuthorizedListListener.invoke(it) }
                }
                binding.tvChangeVerificationCard -> {
                    mItem?.also { mChangeCardListener.invoke(it, null) }
                }
            }
        }
    }
}