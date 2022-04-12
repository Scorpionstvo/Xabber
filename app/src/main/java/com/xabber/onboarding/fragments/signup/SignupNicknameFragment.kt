package com.xabber.onboarding.fragments.signup

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.xabber.R
import com.xabber.databinding.FragmentSignupNicknameBinding
import com.xabber.onboarding.contract.navigator
import com.xabber.onboarding.contract.toolbarChanger


class SignupNicknameFragment : Fragment() {
    private var binding: FragmentSignupNicknameBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupNicknameBinding.inflate(inflater)
        return binding?.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbarChanger().setTitle(R.string.signup_toolbar_title_1)
        toolbarChanger().setShowBack(true)
        initEditText()
        initButton()
    }


    private fun initEditText() {
        binding?.nicknameEditText?.setOnFocusChangeListener { _, hasFocused ->
            if (hasFocused) {
                binding?.nicknameEditText?.background = resources.getDrawable(R.drawable.frame_blue)
            } else {
                binding?.nicknameEditText?.background =
                    resources.getDrawable(R.drawable.frame_normal)
            }
        }

        val textChangeListener = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(p0: Editable?) {
                binding?.nicknameBtnNext?.isEnabled = p0.toString().length > 1
            }

        }
        binding?.nicknameEditText?.addTextChangedListener(textChangeListener)
    }

    private fun initButton() {
        binding?.nicknameBtnNext?.setOnClickListener {
                    navigator().startSignupUserNameFragment()
                }
            }


}


