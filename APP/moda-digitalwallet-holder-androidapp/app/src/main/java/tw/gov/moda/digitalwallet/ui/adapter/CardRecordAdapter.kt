package tw.gov.moda.digitalwallet.ui.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tw.gov.moda.digitalwallet.data.db.CombinedCredentialOperationRecord
import tw.gov.moda.digitalwallet.data.element.OperationEnum
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.ItemCardRecordBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CardRecordAdapter : ListAdapter<CombinedCredentialOperationRecord, CardRecordAdapter.ItemViewHolder>(object : DiffUtil.ItemCallback<CombinedCredentialOperationRecord>() {
    override fun areContentsTheSame(oldItem: CombinedCredentialOperationRecord, newItem: CombinedCredentialOperationRecord): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: CombinedCredentialOperationRecord, newItem: CombinedCredentialOperationRecord): Boolean {
        return oldItem == newItem
    }
}) {

    private val mExpandSet: MutableSet<String> = mutableSetOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardRecordAdapter.ItemViewHolder {
        val binding = ItemCardRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardRecordAdapter.ItemViewHolder, position: Int) {
        holder.setItem(getItem(position))
    }


    inner class ItemViewHolder(val binding: ItemCardRecordBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        private val mDateFormater = SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN)
        private var mItem: CombinedCredentialOperationRecord? = null

        fun setItem(item: CombinedCredentialOperationRecord) {
            this.mItem = item
            binding.tvTitle.text = item.text
            binding.tvDatetime.text = mDateFormater.format(Date(item.datetime))

            when (item.status) {
                OperationEnum.ADD_CARD -> {
                    binding.tvDatetime.isVisible = true
                    binding.layStatus.isVisible = true
                    binding.imgToggleExpand.isVisible = false
                    binding.viewLine.isVisible = false
                    binding.layAuthorizedUnit.isVisible = false
                    binding.layAuthorizedPurpose.isVisible = false
                    binding.layAuthorizedField.isVisible = false

                    binding.layStatus.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.orange_01))
                    binding.imgStatus.setImageResource(R.drawable.ic_record_add_card)
                    binding.tvStatus.text = itemView.context.getString(R.string.add_card)
                    binding.tvStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.orange_06))
                }

                OperationEnum.INVAILD_CARD -> {
                    binding.tvDatetime.isVisible = true
                    binding.layStatus.isVisible = true
                    binding.imgToggleExpand.isVisible = false
                    binding.viewLine.isVisible = false
                    binding.layAuthorizedUnit.isVisible = false
                    binding.layAuthorizedPurpose.isVisible = false
                    binding.layAuthorizedField.isVisible = false

                    binding.layStatus.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.secondary_01))
                    binding.imgStatus.setImageResource(R.drawable.ic_record_invaild)
                    binding.tvStatus.text = itemView.context.getString(R.string.invalid_card)
                    binding.tvStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.secondary_06))
                }

                OperationEnum.AUTHORIZATION_CARD -> {
                    binding.tvDatetime.isVisible = true
                    binding.layStatus.isVisible = true
                    binding.imgToggleExpand.isVisible = true
                    binding.viewLine.isVisible = false
                    binding.layAuthorizedUnit.isVisible = false
                    binding.layAuthorizedPurpose.isVisible = false
                    binding.layAuthorizedField.isVisible = false

                    binding.tvAuthorizedUnit.text = item.authorizationUnit
                    binding.tvAuthorizedPurpose.text = item.authorizationPurpose
                    binding.tvAuthorizedField.text = item.authorizationField

                    if (mExpandSet.contains(item.uid)) {
                        binding.imgToggleExpand.isSelected = false
                        binding.viewLine.isVisible = true
                        binding.layAuthorizedUnit.isVisible = true
                        binding.layAuthorizedPurpose.isVisible = true
                        binding.layAuthorizedField.isVisible = true
                    } else {
                        binding.imgToggleExpand.isSelected = true
                        binding.viewLine.isVisible = false
                        binding.layAuthorizedUnit.isVisible = false
                        binding.layAuthorizedPurpose.isVisible = false
                        binding.layAuthorizedField.isVisible = false
                    }


                    binding.layStatus.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.primary_01))
                    binding.imgStatus.setImageResource(R.drawable.ic_record_authorization)
                    binding.tvStatus.text = itemView.context.getString(R.string.authorized_card)
                    binding.tvStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.primary_06))

                    binding.imgToggleExpand.setOnClickListener(this)
                }

                else -> {
                    binding.layStatus.isVisible = false
                    binding.imgToggleExpand.isVisible = false
                    binding.viewLine.isVisible = false
                    binding.layAuthorizedUnit.isVisible = false
                    binding.layAuthorizedPurpose.isVisible = false
                    binding.layAuthorizedField.isVisible = false
                }
            }
        }

        override fun onClick(v: View?) {
            if (v == binding.imgToggleExpand) {
                mItem?.also { cardRecord ->
                    if (mExpandSet.contains(cardRecord.uid)) {
                        mExpandSet.remove(cardRecord.uid)
                    } else {
                        mExpandSet.add(cardRecord.uid)
                    }
                    setItem(cardRecord)
                }
            }
        }
    }
}