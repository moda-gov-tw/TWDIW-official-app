package tw.gov.moda.digitalwallet.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import tw.gov.moda.digitalwallet.core.verifiable.VerifiableManager
import tw.gov.moda.digitalwallet.data.model.AddCredential
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.ItemAddCredentialBinding

class AddCredentialListAdapter(
    private val mListener: (AddCredential.VCItem) -> Unit,
    private val coroutineScope: CoroutineScope,
    private val verifiableManager: VerifiableManager
) : ListAdapter<AddCredential.VCItem, AddCredentialListAdapter.ItemViewHolder>(object : DiffUtil.ItemCallback<AddCredential.VCItem>() {
    override fun areContentsTheSame(oldItem: AddCredential.VCItem, newItem: AddCredential.VCItem): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: AddCredential.VCItem, newItem: AddCredential.VCItem): Boolean {
        return oldItem == newItem
    }
}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddCredentialListAdapter.ItemViewHolder {
        val binding = ItemAddCredentialBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddCredentialListAdapter.ItemViewHolder, position: Int) {
        holder.setItem(getItem(position))
    }

    inner class ItemViewHolder(val binding: ItemAddCredentialBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        private var mItem: AddCredential.VCItem? = null

        init {
            binding.root.setOnClickListener(this)
        }

        fun setItem(item: AddCredential.VCItem) {
            this.mItem = item
            binding.tvTitle.text = item.name

            val currentVcUid = item.vcUid
            item.logoUrl?.let { url ->
                coroutineScope.launch(CoroutineExceptionHandler { coroutineContext, throwable ->
                    binding.imgLogo.setImageResource(R.drawable.ic_logo)
                }) {
                    val bitmap = verifiableManager.loadImageBitmapForList(url)
                    if (mItem?.vcUid == currentVcUid && bitmap != null) {
                        binding.imgLogo.setImageBitmap(bitmap)
                    } else {
                        binding.imgLogo.setImageResource(R.drawable.ic_logo)
                    }
                }
            }
        }


        override fun onClick(v: View?) {
            mItem?.let { mListener.invoke(it) }
        }
    }
}