package com.marin.fireexpense.ui

import android.app.AlertDialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.marin.fireexpense.MainActivity
import com.marin.fireexpense.R
import com.marin.fireexpense.data.MainDataSource
import com.marin.fireexpense.data.model.Expense
import com.marin.fireexpense.data.model.UpdateExpense
import com.marin.fireexpense.data.model.asDecrypted
import com.marin.fireexpense.data.model.asEncrypted
import com.marin.fireexpense.databinding.FragmentBaseBinding
import com.marin.fireexpense.databinding.InputItemBinding
import com.marin.fireexpense.databinding.InputYearChartBinding
import com.marin.fireexpense.databinding.InputYearMonthBinding
import com.marin.fireexpense.domain.MainRepoImpl
import com.marin.fireexpense.presentation.MainAdapter
import com.marin.fireexpense.presentation.MainViewModel
import com.marin.fireexpense.presentation.MainViewModelFactory
import com.marin.fireexpense.utils.*
import com.marin.fireexpense.vo.Result

/**
 * Created by Enrique Marín on 07-10-2020.
 */

abstract class BaseFragment : Fragment(R.layout.fragment_base), MainAdapter.OnExpenseClickListener {

    private val viewModel by viewModels<MainViewModel> {
        MainViewModelFactory(MainRepoImpl(MainDataSource()))
    }

    private lateinit var mainAdapter: MainAdapter
    private lateinit var baseBinding: FragmentBaseBinding
    protected abstract var type: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainAdapter = MainAdapter(requireContext(), this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFragmentUI(view)
    }

    override fun onExpenseClick(expense: Expense, position: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Deseas actualizar/eliminar el item seleccionado")
            .setCancelable(false)
            .setPositiveButton("Eliminar") { _, _ ->
                removeElement(expense)
            }
            .setNegativeButton("Actualizar") { _, _ ->
                updateElement(expense)
            }
            .setNeutralButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    protected abstract fun openChart(itemsList: Array<Expense>): NavDirections

    private fun setupFragmentUI(view: View) {

        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        baseBinding = FragmentBaseBinding.bind(view)

        if (MainActivity.dataList.isEmpty()) {
            viewModel.setExpenseData(type)
        } else {
            setDataToDisplay()
        }

        baseBinding.apply {
            rvExpense.layoutManager = LinearLayoutManager(requireContext())
            rvExpense.adapter = mainAdapter

            fabFilter.setOnClickListener { addFilter() }

            fabFilter.setOnLongClickListener {
                setDataToDisplay()
                return@setOnLongClickListener true
            }

            fabAdd.setOnClickListener { addExpense() }

            fabChart.setOnClickListener { getItemsListForChart() }
        }

        updateFragmentUI()
    }

    private fun updateFragmentUI() {
        viewModel.getExpenseData.observe(viewLifecycleOwner) { result ->
            baseBinding.progressBar.showIf { result is Result.Loading }
            when (result) {
                is Result.Success -> {
                    MainActivity.dataList = result.data
                        .asDecrypted(requireContext().getPassword()!!)
                        .sortedBy { it.timestamp }
                    setDataToDisplay()
                }
                is Result.Failure -> {
                    requireContext().showToast(
                        "Ocurrió un error al traer los datos ${result.exception}"
                    )
                }
            }
        }
    }

    private fun setDataToDisplay(year: String = currentYear(), month: String = currentMonth()) {
        mainAdapter.expenseList = MainActivity.dataList.filterBy(type, year, month)
        baseBinding.emptyContainer.root.showIf { mainAdapter.expenseList.isEmpty() }
        baseBinding.txtTotalAmount.text = totalAmount(mainAdapter.expenseList)
    }

    private fun addFilter() {
        val binding = InputYearMonthBinding.inflate(layoutInflater)
        binding.editTextYear.text = currentYear().toEditable()
        AlertDialog.Builder(requireContext())
            .setTitle("Filtrar por año y mes")
            .setView(binding.root)
            .setCancelable(false)
            .setPositiveButton("Filtrar") { _, _ ->
                val year = binding.editTextYear.text.toString()
                val month = setMonthFormat(binding.editTextMonth.text.toString())
                when {
                    checkYear(year) -> {
                        setDataToDisplay(year, month)
                    }
                    else -> {
                        requireContext().showToast(getString(R.string.wrong_year_value))
                    }
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun addExpense() {
        val binding = InputItemBinding.inflate(layoutInflater)
        val editTextAmount = binding.editTextAmount
        editTextAmount.addDecimalLimiter()
        AlertDialog.Builder(requireContext())
            .setTitle("Añade concepto e importe")
            .setView(binding.root)
            .setCancelable(false)
            .setPositiveButton("Añadir") { _, _ ->
                val concept = binding.editTextConcept.text.toString()
                val amount = editTextAmount.text.toString()
                if (concept.isNotBlank() && amount.isNotBlank()) {
                    val expense =
                        Expense(type = type, concept = concept, amount = amount.toDouble())
                            .asEncrypted(requireContext().getPassword()!!)
                    viewModel.createExpense(expense)
                    viewModel.setExpenseData(type)
                    requireContext().showToast("Item añadido satisfactoriamente")
                } else {
                    requireContext().showToast("Algún campo está vacío")
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun removeElement(expense: Expense) {
        AlertDialog.Builder(requireContext())
            .setTitle("¿Deseas eliminar el elemento seleccionado")
            .setCancelable(false)
            .setPositiveButton("Sí") { _, _ ->
                viewModel.eliminateExpense(expense.expenseId)
                viewModel.setExpenseData(type)
                requireContext().showToast("Item eliminado satisfactoriamente")
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun updateElement(expense: Expense) {
        val binding = InputItemBinding.inflate(layoutInflater)
        val editTextAmount = binding.editTextAmount
        editTextAmount.addDecimalLimiter()
        AlertDialog.Builder(requireContext())
            .setTitle("Actualiza concepto y/o importe")
            .setView(binding.root)
            .setCancelable(false)
            .setPositiveButton("Actualizar") { _, _ ->
                val concept = binding.editTextConcept.text.toString()
                val amount = editTextAmount.text.toString()
                var updateExpense: UpdateExpense? = null
                when {
                    concept.isNotBlank() && amount.isNotBlank() -> {
                        updateExpense = UpdateExpense(
                            concept = concept.cipherEncrypt(requireContext().getPassword()!!)!!,
                            amount = amount.cipherEncrypt(requireContext().getPassword()!!)!!,
                            expenseId = expense.expenseId
                        )
                    }
                    concept.isNotBlank() && amount.isBlank() -> {
                        updateExpense = UpdateExpense(
                            concept = concept.cipherEncrypt(requireContext().getPassword()!!)!!,
                            amount = expense.amount.toString().cipherEncrypt(requireContext().getPassword()!!)!!,
                            expenseId = expense.expenseId
                        )
                    }
                    concept.isBlank() && amount.isNotBlank() -> {
                        updateExpense = UpdateExpense(
                            concept = expense.concept.cipherEncrypt(requireContext().getPassword()!!)!!,
                            amount = amount.cipherEncrypt(requireContext().getPassword()!!)!!,
                            expenseId = expense.expenseId
                        )
                    }
                    else -> requireContext().showToast("Los dos campos están vacíos")
                }

                if (updateExpense != null) {
                    viewModel.updateExpenseItem(updateExpense)
                    viewModel.setExpenseData(type)
                    requireContext().showToast("Item actualizado satisfactoriamente")
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun getItemsListForChart() {
        val binding = InputYearChartBinding.inflate(layoutInflater)
        binding.editTextYearChart.text = currentYear().toEditable()
        AlertDialog.Builder(requireContext())
            .setTitle("Elige año para mostrar en la gráfica")
            .setView(binding.root)
            .setCancelable(false)
            .setPositiveButton("Aceptar") { _, _ ->
                val year = binding.editTextYearChart.text.toString()

                when {
                    checkYear(year) -> {
                        val itemList = if (binding.checkBox.isChecked) {
                            MainActivity.dataList
                                .filter { it.year == binding.editTextYearChart.text.toString() }
                                .toTypedArray()
                        } else {
                            MainActivity.dataList
                                .filter { it.type == type && it.year == binding.editTextYearChart.text.toString() }
                                .toTypedArray()
                        }

                        if (itemList.isNotEmpty()) {
                            findNavController().navigate(openChart(itemList))
                        } else {
                            requireContext().showToast("No hay datos para mostrar en la gráfica")
                        }
                    }
                    else -> {
                        requireContext().showToast(getString(R.string.wrong_year_value))
                    }
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}