package com.artisan.un.ui.home.fragment

import com.artisan.un.R
import com.artisan.un.apiModel.ProductBundle
import com.artisan.un.baseClasses.BaseFragment
import com.artisan.un.databinding.FragmentDraftBinding
import com.artisan.un.ui.common.dialog.CustomAlertDialog
import com.artisan.un.ui.home.viewModel.DraftViewModel
import com.artisan.un.ui.product.AddProductBasicActivity
import com.artisan.un.utils.DRAFT_PRODUCT
import com.artisan.un.utils.getVideoThumbnail
import com.artisan.un.utils.navigateTo

class DraftFragment : BaseFragment<FragmentDraftBinding, DraftViewModel>(R.layout.fragment_draft, DraftViewModel::class) {
    override fun onCreateView() {
        observeData()
        initClickEvent()
    }

    override fun onResume() {
        super.onResume()
        viewDataBinding.message = null
        viewDataBinding.isLoading = true
        mViewModel.getDraftProduct()
    }

    private fun observeData() {
        mViewModel.product.observe(requireActivity(), {
            viewDataBinding.isLoading = false
            viewDataBinding.draftInfo = it

            it?.let {
                viewDataBinding.image = it.video_url?.run { getVideoThumbnail(this) } ?: run { it.image_1 }
            } ?: run {
                viewDataBinding.message = getString(R.string.draft_cleared)
            }
        })


        mViewModel.errorMessage.observe(requireActivity(), {
            it.message?.let { message ->
                viewDataBinding.isLoading = false
                viewDataBinding.message = message
            }
        })
    }

    private fun initClickEvent() {
        viewDataBinding.actionRemove.setOnClickListener {
            CustomAlertDialog(
                context = this@DraftFragment.requireContext(),
                message = getString(R.string.clear_draft_message),
                primaryKey = getString(R.string.delete),
                secondaryKey = getString(R.string.cancel),
                primaryKeyAction = {
                    progressBar.setMessage(getString(R.string.clearing_draft))
                    mViewModel.removeDraftProduct(mViewModel.product.value?.id)
                }
            ).show()
        }

        viewDataBinding.actionEdit.setOnClickListener {
            mViewModel.product.value?.let {
                requireContext().navigateTo(AddProductBasicActivity::class.java, arrayListOf(
                    Pair(DRAFT_PRODUCT, ProductBundle().parseData(it))
                ))
            }
        }
    }
}