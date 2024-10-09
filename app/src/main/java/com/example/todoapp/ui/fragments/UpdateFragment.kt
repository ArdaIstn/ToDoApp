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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todoapp.R
import com.example.todoapp.data.model.Priority
import com.example.todoapp.data.model.ToDoData
import com.example.todoapp.databinding.FragmentUpdateBinding
import com.example.todoapp.ui.viewmodel.UpdateViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateFragment : Fragment() {
    private val args by navArgs<UpdateFragmentArgs>()
    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!!

    private val updateViewModel: UpdateViewModel by viewModels()
    private lateinit var toolbarUpdate: androidx.appcompat.widget.Toolbar
    private lateinit var title: String
    private lateinit var description: String
    private lateinit var priority: Priority
    private lateinit var builder: AlertDialog.Builder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateBinding.inflate(inflater, container, false)
        val view = binding.root

        initialize()

        binding.apply {
            currentEtTitle.setText(title)
            etDescription.setText(description)
            currentPrioritiesSpinner.setSelection(updateViewModel.parsePriorityToInt(priority))
        }

        changeSpinnerColor()

        setUpToolbarAdd()

        setupMenuProvider()

        binding.backIv.setOnClickListener {
            findNavController().popBackStack()
        }

        return view
    }


    private fun initialize() {
        title = args.currentItem.title
        description = args.currentItem.description
        priority = args.currentItem.priority
        builder = AlertDialog.Builder(requireContext())
    }


    private fun changeSpinnerColor() {
        binding.currentPrioritiesSpinner.onItemSelectedListener =
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

    private fun setUpToolbarAdd() {
        toolbarUpdate = binding.toolbarUpdate
        (activity as AppCompatActivity).setSupportActionBar(toolbarUpdate)
    }


    private fun getColorForPriority(priority: String): Int {
        return when (priority) {
            "High Priority" -> R.color.red
            "Medium Priority" -> R.color.green
            "Low Priority" -> R.color.yellow
            else -> R.color.yellow
        }
    }

    private fun setupMenuProvider() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.update_fragment_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_save -> {
                        updateDataToDb() // Veritabanına veri güncelleme işlemini başlat
                        true
                    }

                    R.id.menu_delete -> {
                        confirmItemRemoval()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner)
    }

    private fun updateDataToDb() {
        val newTitle = binding.currentEtTitle.text.toString()
        val newDescription = binding.etDescription.text.toString()
        val newPriority = binding.currentPrioritiesSpinner.selectedItem.toString()

        if (updateViewModel.verifyUpdatedDataFromUser(newTitle, newDescription)) {
            val updatedItem = ToDoData(
                args.currentItem.id,
                newTitle,
                updateViewModel.parseUpdatedPriority(newPriority),
                newDescription
            )
            updateViewModel.updateData(updatedItem)
            Snackbar.make(requireView(), "Successfully Updated", Snackbar.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        } else {
            Snackbar.make(requireView(), "Please fill all fields.", Snackbar.LENGTH_SHORT).show()

        }

    }


    private fun confirmItemRemoval() {
        builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            updateViewModel.deleteData(args.currentItem)
            Snackbar.make(
                requireView(),
                "Successfully Removed: ${args.currentItem.title}",
                Snackbar.LENGTH_SHORT
            ).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle("Delete '${args.currentItem.title}'?")
        builder.setMessage("Are you sure you want to remove '${args.currentItem.title}'?")
        builder.create().show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}