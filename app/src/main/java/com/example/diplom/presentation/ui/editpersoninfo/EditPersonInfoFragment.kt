package com.example.diplom.presentation.ui.editpersoninfo

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.diplom.R
import com.example.diplom.data.network.categories.model.response.CategoryResponse
import com.example.diplom.data.network.personinfo.model.response.MyProfileResponse
import com.example.diplom.databinding.FragmentEditPersonInfoBinding
import com.example.diplom.presentation.MainActivity
import com.example.diplom.presentation.common.showToast
import com.example.diplom.presentation.common.toDateString
import com.example.diplom.presentation.common.toEditable
import com.example.diplom.presentation.common.toPhotoURL
import com.example.diplom.util.BASE_URL
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


class EditPersonInfoFragment : Fragment() {

    private var _binding: FragmentEditPersonInfoBinding? = null
    private val binding: FragmentEditPersonInfoBinding get() = _binding!!
    private val viewModel by viewModel<EditPersonInfoViewModel>()
    private var selectedCategory: CategoryResponse? = null
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {

                val file = File(requireContext().getCacheDir(), "image.png")
                try {
                    val instream: InputStream = requireContext().getContentResolver().openInputStream(uri)!!
                    val output = FileOutputStream(file)
                    val buffer = ByteArray(1024)
                    var size: Int
                    while (instream.read(buffer).also { size = it } != -1) {
                        output.write(buffer, 0, size)
                    }
                    instream.close()
                    output.close()
                } catch (e: IOException) {
                    Log.d("TAG1", "e: ${e}")
                }
                viewModel.uploadUserPhoto(file)

            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).showUpButton()
        setOnBackPressed()
        setMenuToolbar()
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
                menuInflater.inflate(R.menu.main_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    android.R.id.home -> findNavController().popBackStack()
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditPersonInfoBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setTitle(R.string.title_edit_profile)
        setOnBackPressed()
        viewModel.state.observe(viewLifecycleOwner){ state ->
            when(state){
                is EditPersonInfoStateUi.Error -> requireContext().showToast(state.error)
                is EditPersonInfoStateUi.PersonInfo -> handlePersonInfo(state.personInfo)
                is EditPersonInfoStateUi.Categories -> handleCategories(state.categories)
                is EditPersonInfoStateUi.UserPhoto -> loadPhoto(state.photo)
            }
        }
        viewModel.getPersonInfo()
        binding.imgUserProfileEdit.setOnClickListener{
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun loadPhoto(photo: String) {
        Glide
            .with(requireContext())
            .load(photo.toPhotoURL())
            .into(binding.imgUserProfileEdit)
    }

    private fun handlePersonInfo(personInfo: MyProfileResponse) = with(binding){
        name.text = personInfo.name.toEditable()
        surname.text = personInfo.surname.toEditable()
        categotiesTextView.text = personInfo.category.name.toEditable()
        selectedCategory = personInfo.category
        dateOfBirthday.text = personInfo.dateOfBirthday.toDateString().toEditable()
        if (!personInfo.icon.isNullOrEmpty()) {
            loadPhoto(personInfo.icon)
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


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}