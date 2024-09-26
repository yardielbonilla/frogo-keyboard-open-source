package com.frogobox.appkeyboard.ui.autotext

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.viewModels
import com.frogobox.appkeyboard.common.base.BaseActivity
import com.frogobox.appkeyboard.databinding.ActivityAutotextBinding
import com.frogobox.appkeyboard.databinding.ItemAutotextBinding
import com.frogobox.appkeyboard.model.AutoTextEntity
import com.frogobox.coresdk.source.FrogoResult
import com.frogobox.recycler.core.FrogoRecyclerNotifyListener
import com.frogobox.recycler.core.IFrogoBindingAdapter
import com.frogobox.recycler.ext.injectorBinding
import com.frogobox.sdk.ext.gone
import com.frogobox.sdk.ext.showLogD
import com.frogobox.sdk.ext.visible
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Faisal Amir on 11/03/23
 * https://github.com/amirisback
 */


@AndroidEntryPoint
class AutoTextActivity : BaseActivity<ActivityAutotextBinding>() {

    private val viewModel: AutoTextViewModel by viewModels()

    override fun setupViewBinding(): ActivityAutotextBinding {
        return ActivityAutotextBinding.inflate(layoutInflater)
    }

    override fun setupViewModel() {
        viewModel.apply {
            autoText.observe(this@AutoTextActivity) {

                showLogD("${it}", "AutoText")

                when (it) {
                    is FrogoResult.Error -> {}
                    is FrogoResult.Finish -> {}
                    is FrogoResult.Loading -> {}
                    is FrogoResult.Success -> {
                        if (it.result.isEmpty()) {
                            binding.emptyView.root.visible()
                            binding.rvAutotext.gone()
                        } else {
                            binding.emptyView.root.gone()
                            binding.rvAutotext.visible()
                            setupRvAutoText(it.result)
                        }
                    }
                }
            }
        }
    }

    override fun onCreateExt(savedInstanceState: Bundle?) {
        super.onCreateExt(savedInstanceState)
        setupDetailActivity("Auto Text")
        setupUI()
        viewModel.getAutoText()
    }

    override fun setupActivityResultExt(result: ActivityResult) {
        super.setupActivityResultExt(result)

        when (result.resultCode) {

            AutoTextDetailActivity.RESULT_CODE_DELETE,
            AutoTextEditorActivity.RESULT_CODE_UPDATE,
            AutoTextEditorActivity.RESULT_CODE_ADD,
            -> {
                viewModel.getAutoText()
            }

        }
    }

    private fun setupUI() {
        binding.apply {
            btnAdd.setOnClickListener {
                startActivityResultExt(Intent(this@AutoTextActivity, AutoTextEditorActivity::class.java))
            }
        }
    }

    private fun setupRvAutoText(data: List<AutoTextEntity>) {
        binding.rvAutotext.injectorBinding<AutoTextEntity, ItemAutotextBinding>()
            .addData(data)
            .addCallback(object : IFrogoBindingAdapter<AutoTextEntity, ItemAutotextBinding> {
                override fun areContentsTheSame(
                    oldItem: AutoTextEntity,
                    newItem: AutoTextEntity,
                ): Boolean {
                    return oldItem == newItem
                }

                override fun areItemsTheSame(
                    oldItem: AutoTextEntity,
                    newItem: AutoTextEntity,
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun setViewBinding(parent: ViewGroup): ItemAutotextBinding {
                    return ItemAutotextBinding.inflate(layoutInflater, parent, false)
                }

                override fun setupInitComponent(
                    binding: ItemAutotextBinding,
                    data: AutoTextEntity,
                    position: Int,
                    notifyListener: FrogoRecyclerNotifyListener<AutoTextEntity>,
                ) {
                    binding.tvAutotextTitle.text = data.title
                    binding.tvAutotextContent.text = data.body
                }

                override fun onItemClicked(
                    binding: ItemAutotextBinding,
                    data: AutoTextEntity,
                    position: Int,
                    notifyListener: FrogoRecyclerNotifyListener<AutoTextEntity>,
                ) {
                    val extra = Gson().toJson(data)
                    startActivityResultExt(
                        Intent(this@AutoTextActivity, AutoTextDetailActivity::class.java).apply {
                            putExtra(AutoTextDetailActivity.EXTRA_AUTO_TEXT, extra)
                        }
                    )
                }

            })
            .createLayoutLinearVertical(false)
            .build()
    }

}