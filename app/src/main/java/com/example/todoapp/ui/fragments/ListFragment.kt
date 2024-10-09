// ListFragment.kt
package com.example.todoapp.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.todoapp.R
import com.example.todoapp.data.model.ToDoData
import com.example.todoapp.databinding.FragmentListBinding
import com.example.todoapp.ui.adapter.ListAdapter
import com.example.todoapp.ui.swipetodelete.SwipeToDelete
import com.example.todoapp.ui.viewmodel.ListViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListFragment : Fragment(), SearchView.OnQueryTextListener {
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val listViewModel: ListViewModel by viewModels()
    private lateinit var toolbarList: androidx.appcompat.widget.Toolbar
    private lateinit var rv: RecyclerView
    private lateinit var builder: AlertDialog.Builder
    private lateinit var listAdapter: ListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_addFragment)
        }

        setUpRecyclerView()
        setUpToolbarList()
        setupMenuProvider()


        viewLifecycleOwner.lifecycleScope.launch {
            listViewModel.toDoList.collect { toDoList ->
                listAdapter = ListAdapter()
                listAdapter.setData(toDoList)
                rv.adapter = listAdapter
                rv.scheduleLayoutAnimation()

                binding.lottieAnim.visibility =
                    if (toDoList.isEmpty()) View.VISIBLE else View.INVISIBLE
            }
        }

        return view
    }

    private fun setupMenuProvider() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.list_fragment_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_delete_all -> {
                        confirmItemRemoveAll()
                        true
                    }

                    R.id.menu_search -> {
                        val searchView = menuItem.actionView as SearchView
                        searchView.isSubmitButtonEnabled = true
                        searchView.setOnQueryTextListener(this@ListFragment)
                        true
                    }

                    R.id.menu_priority_high -> {
                        listViewModel.sortByHighPriority()
                        true
                    }

                    R.id.menu_priority_low -> {
                        listViewModel.sortByLowPriority()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner)
    }

    private fun setUpRecyclerView() {
        rv = binding.rvList
        rv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        swipeToDelete(rv)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchThroughDataBase(query)
        }
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (query != null) {
            searchThroughDataBase(query)
        }
        return true
    }

    private fun searchThroughDataBase(query: String) {
        val searchQuery = "%$query%"
        listViewModel.searchDataByQuery(searchQuery)
        listViewModel.searchedList.observe(viewLifecycleOwner) { list ->
            list?.let {
                listAdapter.setData(list)
                rv.scheduleLayoutAnimation()
                binding.lottieAnim.visibility = if (list.isEmpty()) View.VISIBLE else View.INVISIBLE
            }
        }
    }

    private fun confirmItemRemoveAll() {
        builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            listViewModel.deleteAllData()
            Snackbar.make(
                requireView(), "Successfully Removed Everything!", Snackbar.LENGTH_SHORT
            ).show()
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle("Delete Everything ?")
        builder.setMessage("Are you sure you want to remove everything ?")
        builder.create().show()
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        val swipeToDeleteCallback = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedItem = listAdapter.dataList[viewHolder.adapterPosition]

                // Delete Item
                listViewModel.deleteData(deletedItem)
                listAdapter.notifyItemRemoved(viewHolder.adapterPosition)

                // Restore Deleted Item
                restoreDeletedData(viewHolder.itemView, deletedItem, viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun restoreDeletedData(view: View, deletedItem: ToDoData, position: Int) {
        Snackbar.make(view, "Deleted '${deletedItem.title}'", Snackbar.LENGTH_LONG)
            .setAction("Undo") {
                listViewModel.insertData(deletedItem)
                listAdapter.notifyItemChanged(position)
            }.show()
    }

    private fun setUpToolbarList() {
        toolbarList = binding.toolbarList
        (activity as AppCompatActivity).setSupportActionBar(toolbarList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
