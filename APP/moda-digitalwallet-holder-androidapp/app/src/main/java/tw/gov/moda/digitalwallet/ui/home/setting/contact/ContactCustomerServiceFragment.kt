package tw.gov.moda.digitalwallet.ui.home.setting.contact

import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tw.gov.moda.digitalwallet.ui.base.BaseFragment
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.digitalwallet.ui.dialog.MyDialog
import tw.gov.moda.digitalwallet.ui.main.MainViewModel
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.FragmentContactCustomerServiceBinding


@AndroidEntryPoint
class ContactCustomerServiceFragment : BaseFragment<FragmentContactCustomerServiceBinding>() {

    companion object {
        fun newInstance() = ContactCustomerServiceFragment()

        private const val DIALOG_WITHOUT_EMAIL = "DIALOG_WITHOUT_EMAIL"
    }

    override fun initViewBinding(container: ViewGroup?): FragmentContactCustomerServiceBinding = FragmentContactCustomerServiceBinding.inflate(layoutInflater, container, false)

    override fun getViewModel(): BaseViewModel = mViewModel

    private val mViewModel: ContactCustomerServiceViewModel by viewModels()

    private val mActivityViewModel: MainViewModel by activityViewModels<MainViewModel>()

    override fun initView() {
        super.initView()
        setInsets(mActivityViewModel, binding.viewTop, binding.viewBottom)
        setToolbar(getString(R.string.contact_customer_service), { imageView ->
            imageView.isVisible = true
            imageView.setImageResource(R.drawable.selector_button_arrow2_left)
            imageView.setOnClickListener {
                mActivityViewModel.pageController.popBackStack()
            }
        })

        binding.tvCustomerServiceMail.setOnClickListener {
            activity?.apply {
                try {
                    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + getString(R.string.email_customer_service)))
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(Intent.createChooser(intent, ""))
                    }
                } catch (e: Exception) {
                    MyDialog.Builder(this)
                        .setTitle(R.string.error_message)
                        .setMessage(getString(R.string.msg_error_send_email))
                        .setRightButtonText(R.string.confirm)
                        .show(supportFragmentManager, DIALOG_WITHOUT_EMAIL)
                }
            }
        }
    }

    override fun subscribeObservers() {
        super.subscribeObservers()

    }


}