package com.xabber.presentation.application.fragments.calls

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.xabber.R
import com.xabber.databinding.FragmentCallsBinding
import com.xabber.presentation.BaseFragment
import com.xabber.presentation.application.activity.MaskChanger

class CallsFragment : BaseFragment(R.layout.fragment_calls) {
    private val binding by viewBinding(FragmentCallsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imAvatar.setBackgroundResource(MaskChanger.getMask().size32)
       //  Glide.with(binding.imAvatar).load(R.drawable.img).into(binding.imAvatar)
        binding.tvAdt.movementMethod = LinkMovementMethod.getInstance()
    }

}