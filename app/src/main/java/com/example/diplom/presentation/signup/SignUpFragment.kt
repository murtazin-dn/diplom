package com.example.diplom.presentation.signup

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.diplom.R
import com.example.diplom.data.network.auth.model.request.SignUpRequest
import com.example.diplom.data.network.categories.model.response.CategoriesResponse
import com.example.diplom.data.network.categories.model.response.CategoryResponse
import com.example.diplom.databinding.FragmentSignupBinding
import com.example.diplom.presentation.common.toEditable
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*


class SignUpFragment: Fragment(R.layout.fragment_signup) {

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
            DatePickerDialog(requireContext(), dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun setObservers() {
        viewModel.state
            .flowWithLifecycle(lifecycle,  Lifecycle.State.STARTED)
            .onEach { state -> handleStateChange(state) }
            .launchIn(lifecycleScope)
    }

    private fun handleStateChange(state: SignUpFragmentState) {
        when(state){
            is SignUpFragmentState.Init -> Unit
            is SignUpFragmentState.ErrorLogin -> showToast("er")
            is SignUpFragmentState.ErrorPassword -> showToast("er")
            is SignUpFragmentState.IsLoading -> handleLoading(state.isLoading)
            is SignUpFragmentState.SuccessSignUp -> showToast("succes")
            is SignUpFragmentState.ErrorSignUp -> showToast(state.errorSignUp)
            is SignUpFragmentState.SuccessFetchCategories -> handleCategories(state.categories)

        }
    }

    private fun handleCategories(categories: CategoriesResponse) {
        Log.e("categories", categories.toString())
        val adapter = CategoryAdapter(requireContext(), categories.list)
        (binding.categoriesLayout.editText as? AutoCompleteTextView)?.setAdapter(adapter)
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun handleLoading(isLoading: Boolean) {
        binding.loading.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setListeners() {
        binding.login.setOnClickListener {
            viewModel.signUp(
                SignUpRequest(
                    email = "sfgggghahsffjks@mail.ru",
                    password = "5465655hfskdhfksjd",
                    confirmPassword = "5465655hfskdhfksjd",
                    name = "Ivan",
                    surname = "Zolo",
                    dateOfBirthday = 3468765746,
                    categoryId = 9
                )
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

class CategoryAdapter(context: Context, categories: List<CategoryResponse>) :
    ArrayAdapter<CategoryResponse>(context, 0, categories) {
    override fun getView(position: Int, _convertView: View?, parent: ViewGroup): View {
        var convertView = _convertView
        val category: CategoryResponse = getItem(position)!!
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false)
        }
        val tvName = convertView!!.findViewById<View>(R.id.tv_category) as TextView
        tvName.text = category.name
        return convertView
    }
}