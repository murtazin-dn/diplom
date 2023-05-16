package com.example.diplom.presentation.ui.auth.signin

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.diplom.R
import com.example.diplom.data.network.auth.model.request.SignInRequest
import com.example.diplom.databinding.FragmentSignInBinding
import com.example.diplom.presentation.common.showToast
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignInFragment : Fragment() {

    private var isPasswordError = true
    private var isEmailError = true
    private var _binding: FragmentSignInBinding? = null
    private val binding: FragmentSignInBinding get() = _binding!!

    private val viewModel by viewModel<SignInViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignInBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setTitle(R.string.title_signin)
        setListeners()
        setObservers()
    }

    private fun setObservers() {
        viewModel.state
            .flowWithLifecycle(lifecycle,  Lifecycle.State.STARTED)
            .onEach { state -> handleStateChange(state) }
            .launchIn(lifecycleScope)
    }

    private fun handleStateChange(state: SignInFragmentState) {
        when(state){
            is SignInFragmentState.Init -> Unit
            is SignInFragmentState.ErrorPassword -> handlePasswordError(state.errorFields)
            is SignInFragmentState.ErrorEmail -> handleEmailError(state.errorFields)
            is SignInFragmentState.IsLoading -> handleLoading(state.isLoading)
            is SignInFragmentState.SuccessSignIn -> goToMainFragment()
            is SignInFragmentState.ErrorSignIn -> requireContext().showToast(state.errorSignIn)
            is SignInFragmentState.ErrorSignInStr -> requireContext().showToast(state.errorSignIn)
        }
    }

    private fun handleEmailError(errorFields: Int?) {
        if (errorFields != null) {
            binding.emailLayout.error = getString(errorFields)
            isEmailError = true
        } else {
            binding.emailLayout.error = null
            isEmailError = false
        }
    }

    private fun handlePasswordError(errorFields: Int?) {
        if (errorFields != null) {
            binding.passwordLayout.error = getString(errorFields)
            isPasswordError = true
        } else {
            binding.passwordLayout.error = null
            isPasswordError = false
        }
    }


    private fun handleLoading(isLoading: Boolean) {
        binding.loading.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setListeners() = with(binding){
        password.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) login()
            false
        }
        login.setOnClickListener{login()}
        email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                emailLayout.error = null
            }
        })
        password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                passwordLayout.error = null
            }
        })
        signup.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }
    }

    private fun goToMainFragment(){
        val result = findNavController().popBackStack(R.id.auth_navigation, true)
        if (result.not()) {
            findNavController().navigate(R.id.mainFragment)
        }
    }

    private fun login(){
        val request = SignInRequest(
            email = binding.email.text.toString(),
            password = binding.password.text.toString()
        )
        viewModel.validateFields(request)
        if (isPasswordError || isEmailError) return
        viewModel.signIn(request)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}