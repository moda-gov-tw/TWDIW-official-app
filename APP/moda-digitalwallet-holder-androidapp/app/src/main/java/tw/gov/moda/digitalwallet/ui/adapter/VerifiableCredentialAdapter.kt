package tw.gov.moda.digitalwallet.ui.adapter

import android.graphics.Bitmap
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tw.gov.moda.digitalwallet.data.db.VerifiableCredential
import tw.gov.moda.digitalwallet.data.element.CardStatusEnum
import tw.gov.moda.digitalwallet.extension.sha256
import tw.gov.moda.digitalwallet.util.BitmapUtil
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.ItemVerifiableCredentialBinding

class VerifiableCredentialAdapter(private val mListener: ((VerifiableCredential) -> Unit)) : ListAdapter<VerifiableCredential, VerifiableCredentialAdapter.ItemViewHolder>(object : DiffUtil.ItemCallback<VerifiableCredential>() {
    override fun areContentsTheSame(oldItem: VerifiableCredential, newItem: VerifiableCredential): Boolean {
        return oldItem.status == newItem.status && oldItem.previewData == newItem.previewData && oldItem.issuingUnit == newItem.issuingUnit && oldItem.imageBase64 == newItem.imageBase64 && oldItem.trustBadge == newItem.trustBadge
    }

    override fun areItemsTheSame(oldItem: VerifiableCredential, newItem: VerifiableCredential): Boolean {
        return oldItem.uid == newItem.uid
    }
}) {
    private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    private val cacheSize = maxMemory / 4
    private var mMemoryCache: LruCache<String, Bitmap?> = object : LruCache<String, Bitmap?>(cacheSize) {
        override fun sizeOf(key: String, value: Bitmap?): Int {
            return if (value != null) {
                (value.rowBytes * value.height) / 1024
            } else {
                0
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerifiableCredentialAdapter.ItemViewHolder {
        val binding = ItemVerifiableCredentialBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VerifiableCredentialAdapter.ItemViewHolder, position: Int) {
        holder.setItem(getItem(position))
    }


    inner class ItemViewHolder(val binding: ItemVerifiableCredentialBinding) : RecyclerView.ViewHolder(binding.root), OnClickListener {
        private var mItem: VerifiableCredential? = null

        init {
            itemView.setOnClickListener(this)
        }

        fun setItem(item: VerifiableCredential) {
            mItem = item
            binding.tvTitle.text = item.display
            binding.tvIssuer.text = item.issuingUnit
            binding.tvCustom.text = item.previewData
            binding.tvCustom.isVisible = item.previewData.isNotBlank()
            MainScope().launch {
                val cacheKey = item.imageBase64.sha256()
                val bitmap = withContext(Dispatchers.Default) {
                    mMemoryCache.get(cacheKey) ?: BitmapUtil.createBitmap(item.imageBase64)
                }
                if (bitmap != null) {
                    val compressBitmap = withContext(Dispatchers.Default) {
                        BitmapUtil.compress(bitmap, 500 * 1024)
                    }
                    mMemoryCache.put(cacheKey, compressBitmap)
                    binding.imgHead.setImageBitmap(compressBitmap)
                } else {
                    binding.imgHead.setImageResource(R.drawable.ic_default_card)
                }
            }
            if (item.status == CardStatusEnum.Invalid || item.status == CardStatusEnum.Expired) {
                binding.imgInvalidMask.isVisible = true
                binding.tvInvalid.isVisible = true
            } else {
                binding.imgInvalidMask.isVisible = false
                binding.tvInvalid.isVisible = false
            }

            binding.imgTrustBadge.isVisible = item.trustBadge
        }

        override fun onClick(v: View?) {
            mItem?.also { mListener.invoke(it) }
        }
    }
}