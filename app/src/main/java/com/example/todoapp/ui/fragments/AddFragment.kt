package com.example.todoapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.todoapp.R
import com.example.todoapp.data.model.ToDoData
import com.example.todoapp.databinding.FragmentAddBinding
import com.example.todoapp.databinding.FragmentUpdateBinding
import com.example.todoapp.ui.viewmodel.AddViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private val addViewModel: AddViewModel by viewModels()
    private lateinit var toolbarAdd: androidx.appcompat.widget.Toolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddBinding.inflate(inflater, container, false)


        setupToolbarAdd()


        changeSpinnerColor()


        binding.backIv2.setOnClickListener {
            findNavController().popBackStack()
        }

        setupMenuProvider()

        return binding.root
    }


    private fun setupToolbarAdd() {
        toolbarAdd = binding.toolbarAdd
        (activity as AppCompatActivity).setSupportActionBar(toolbarAdd)
    }


    private fun setupMenuProvider() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.add_fragment_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_add -> {
                        insertDataToDb()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner)
    }


    private fun insertDataToDb() {
        val title = binding.etTitle.text.toString()
        val description = binding.etDescription.text.toString()
        val priority = binding.prioritiesSpinner.selectedItem.toString()

        if (addViewModel.verifyAddedDataFromUser(title, description)) {
            val newData = ToDoData(0, title, addViewModel.parseAddedPriority(priority), description)
            addViewModel.insertData(newData)
            Snackbar.make(requireView(), "Successfully Added", Snackbar.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_addFragment_to_listFragment)
        } else {
            Snackbar.make(requireView(), "Please fill all fields.", Snackbar.LENGTH_SHORT).show()

        }
    }


    private fun changeSpinnerColor() {
        binding.prioritiesSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    val selectedPriority = parent?.getItemAtPosition(position).toString()
                    (view as? TextView)?.setTextColor(
                        ContextCompat.getColor(
                            requireContext(), getColorForPriority(selectedPriority)
                        )
                    )
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
    }


    private fun getColorForPriority(priority: String): Int {
        return when (priority) {
            "High Priority" -> R.color.red
            "Medium Priority" -> R.color.green
            "Low Priority" -> R.color.yellow
            else -> R.color.yellow
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
