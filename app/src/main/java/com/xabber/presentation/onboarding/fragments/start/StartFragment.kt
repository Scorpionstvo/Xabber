package com.xabber.presentation.onboarding.fragments.start

import android.graphics.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.xabber.R
import com.xabber.databinding.FragmentStartBinding
import com.xabber.presentation.onboarding.contract.navigator
import com.xabber.presentation.onboarding.contract.toolbarChanger
import com.xabber.presentation.onboarding.fragments.BaseFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class StartFragment : BaseFragment() {
    private var binding: FragmentStartBinding? = null
    private val viewModel = StartViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStartBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButton()
    }



    private fun initButton() {
        with(binding!!) {
            btnSkip.setOnClickListener {
             findNavController().navigate(R.id.action_startFragment_to_applicationActivity)
            }

            btnLogin.setOnClickListener {
                findNavController().navigate(R.id.action_startFragment_to_signinFragment)
            }

            btnSignup.setOnClickListener {
                progressBar.isVisible = true
                btnLogin.visibility = View.GONE
                btnSignup.isVisible = false
                compositeDisposable.add(
                    viewModel.getHost()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ host ->
                            progressBar.isVisible = false
                            findNavController().navigate(R.id.action_startFragment_to_signupNicknameFragment)
                        }, this@StartFragment::logError)
                )
            }


        }

    }
}