package com.example.diplom.presentation.ui.auth.signup

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.diplom.R
import com.example.diplom.data.network.auth.model.request.SignUpRequest
import com.example.diplom.data.network.categories.model.response.CategoryResponse
import com.example.diplom.databinding.FragmentSignupBinding
import com.example.diplom.presentation.common.showToast
import com.example.diplom.presentation.common.toEditable
import com.example.diplom.presentation.common.toEpochSeconds
import com.example.diplom.presentation.ui.auth.signup.model.SignUpFields
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.Long
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*


class SignUpFragment: Fragment(R.layout.fragment_signup) {

    private var selectedCategory: CategoryResponse? = null
    private var calendar = Calendar.getInstance()

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<SignUpViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setTitle(R.string.title_signup)
        setListeners()
        setObservers()
        setDatePicker()
        viewModel.fetchCategories()
    }

    private fun setDatePicker() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = "dd.MM.yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            binding.dateOfBirthday.text = sdf.format(calendar.time).toEditable()
        }

        binding.dateOfBirthgayLayout.setEndIconOnClickListener {
            val picker = DatePickerDialog(requireContext(), dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
            picker.datePicker.maxDate = System.currentTimeMillis()
            picker.show()

        }
    }

    private fun setObservers() {
        viewModel.state.observe(viewLifecycleOwner){ state ->
            handleStateChange(state)
        }
    }

    private fun handleStateChange(state: SignUpFragmentState) {
        when(state){
            is SignUpFragmentState.Init -> Unit
            is SignUpFragmentState.IsLoading -> handleLoading(state.isLoading)
            is SignUpFragmentState.SuccessSignUp -> goToMainFragment()
            is SignUpFragmentState.ErrorSignUp -> requireContext().showToast(state.errorSignUp)
            is SignUpFragmentState.ErrorSignUpStr -> requireContext().showToast(state.errorSignUp)
            is SignUpFragmentState.SuccessFetchCategories -> handleCategories(state.categories)
            is SignUpFragmentState.ErrorCategory -> state.errorCategory.let { error ->
                binding.categoriesLayout.error = if (error != null) getString(error) else null
            }
            is SignUpFragmentState.ErrorPassword -> state.errorPassword.let { error ->
                binding.passwordLayout.error = if (error != null) getString(error) else null
            }
            is SignUpFragmentState.ErrorConfirmPassword -> state.errorConfirmPassword.let { error ->
                binding.confirmPasswordLayout.error = if (error != null) getString(error) else null
            }
            is SignUpFragmentState.ErrorDateOfBirthday -> state.errorDateOfBirthday.let { error ->
                binding.dateOfBirthgayLayout.error = if (error != null) getString(error) else null
            }
            is SignUpFragmentState.ErrorEmail -> state.errorEmail.let { error ->
                binding.emailLayout.error = if (error != null) getString(error) else null
            }
            is SignUpFragmentState.ErrorName -> state.errorName.let { error ->
                binding.nameLayout.error = if (error != null) getString(error) else null
            }
            is SignUpFragmentState.ErrorSurname -> state.errorSurname.let { error ->
                binding.surnameLayout.error = if (error != null) getString(error) else null
            }
            is SignUpFragmentState.SuccessIsTakenEmail -> handleTakenEmail(state.isTakenEmail)
        }
    }

    private fun handleTakenEmail(isTakenEmail: Boolean) {
        if (isTakenEmail) binding.emailLayout.error = getString(R.string.taken_email)
        else viewModel.signUp(
            SignUpRequest(
                name = binding.name.text.toString(),
                surname = binding.surname.text.toString(),
                email = binding.email.text.toString(),
                password = binding.password.text.toString(),
                confirmPassword = binding.confirmPassword.text.toString(),
                dateOfBirthday = binding.dateOfBirthday.text.toString().toEpochSeconds(),
                categoryId = selectedCategory!!.id
            )
        )
    }

    private fun goToMainFragment() {
        val result = findNavController().popBackStack(R.id.auth_navigation, true)
        if (result.not()) {
            findNavController().navigate(R.id.mainFragment)
        }
    }

    private fun handleCategories(categories: List<CategoryResponse>) {
        Log.e("categories", categories.toString())
        val adapter = ArrayAdapter(requireContext(), R.layout.item_category, categories)
        (binding.categoriesLayout.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        binding.categotiesTextView.setOnItemClickListener { adapterView, view, i, l ->
            selectedCategory = adapterView.getItemAtPosition(i) as? CategoryResponse
            binding.categoriesLayout.error = null
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        binding.loading.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setListeners() = with(binding){
        login.setOnClickListener { signUp() }
        name.addTextChangedListener(getTextWatcherHideError(nameLayout))
        surname.addTextChangedListener(getTextWatcherHideError(surnameLayout))
        dateOfBirthday.addTextChangedListener(getTextWatcherHideError(dateOfBirthgayLayout))
        email.addTextChangedListener(getTextWatcherHideError(emailLayout))
        password.addTextChangedListener(getTextWatcherHideError(passwordLayout))
        confirmPassword.addTextChangedListener(getTextWatcherHideError(confirmPasswordLayout))
        confirmPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) signUp()
            false
        }
    }

    private fun signUp() = with(binding){
        val signUpFields = SignUpFields(
            name = name.text.toString(),
            surname = surname.text.toString(),
            email = email.text.toString(),
            password = password.text.toString(),
            confirmPassword = confirmPassword.text.toString(),
            dateOfBirthday = dateOfBirthday.text.toString(),
            categoryId = selectedCategory?.id
        )
        if(viewModel.validateFields(signUpFields)) viewModel.isTakenEmail(email.text.toString())
    }

    private fun getTextWatcherHideError(textInput: TextInputLayout) = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {
            textInput.error = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
