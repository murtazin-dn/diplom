package com.example.diplom.presentation.createpost

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.example.diplom.R
import com.example.diplom.data.network.categories.model.response.CategoryResponse
import com.example.diplom.databinding.FragmentCreatePostBinding
import com.example.diplom.presentation.MainActivity
import com.example.diplom.presentation.common.showToast
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreatePostFragment : Fragment() {

    private var selectedCategory: CategoryResponse? = null
    private var _binding: FragmentCreatePostBinding? = null
    private val binding: FragmentCreatePostBinding get() = _binding!!

    private val viewModel by viewModel<CreatePostViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreatePostBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.setTitle(R.string.new_post)
        setOnBackPressed()
        setMenuToolbar()
        viewModel.fetchCategories()
        setObservers()
        setListeners()
        binding.categotiesTextView.setOnItemClickListener { adapterView, view, i, l ->
            selectedCategory = adapterView.getItemAtPosition(i) as? CategoryResponse
            binding.categoriesLayout.error = null
        }
    }

    private fun setListeners() = with(binding){
        etText.addTextChangedListener(getTextWatcherHideError(textLayout))
        etTitle.addTextChangedListener(getTextWatcherHideError(titleLayout))
        categotiesTextView.addTextChangedListener(getTextWatcherHideError(categoriesLayout))
    }

    private fun setObservers() {
        viewModel.state.observe(viewLifecycleOwner){ state ->
            when(state){
                is CreatePostStateUI.Categories -> handleCategories(state.categories)
                is CreatePostStateUI.Error -> requireActivity().showToast(state.error)
                is CreatePostStateUI.ErrorCategory -> state.error.let { error ->
                    binding.categoriesLayout.error = if (error != null) getString(error) else null
                }
                is CreatePostStateUI.ErrorText -> state.error.let { error ->
                    binding.textLayout.error = if (error != null) getString(error) else null
                }
                is CreatePostStateUI.ErrorTitle -> state.error.let { error ->
                    binding.titleLayout.error = if (error != null) getString(error) else null
                }
                is CreatePostStateUI.Success -> findNavController().popBackStack()
            }
        }
    }

    private fun handleCategories(categories: List<CategoryResponse>) {
        val adapter = ArrayAdapter(requireContext(), R.layout.item_category, categories)
        (binding.categoriesLayout.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        binding.categotiesTextView.setOnItemClickListener { adapterView, view, i, l ->
            selectedCategory = adapterView.getItemAtPosition(i) as? CategoryResponse
            binding.categoriesLayout.error = null
        }
    }


    private fun getTextWatcherHideError(textInput: TextInputLayout) = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable) {
            textInput.error = null
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).showUpButton()
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).hideUpButton()
    }


    private fun setOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            }
        )
    }

    private fun setMenuToolbar() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.create_post_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.create_post -> {
                        createPost()
                        true
                    }
                    android.R.id.home -> {
                        findNavController().popBackStack()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun createPost(){
        val post = CreatePostFields(
            title = binding.etTitle.text.toString(),
            text = binding.etText.text.toString(),
            category = selectedCategory
        )
        viewModel.createPost(post)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}