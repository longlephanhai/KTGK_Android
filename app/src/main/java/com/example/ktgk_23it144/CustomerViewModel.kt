package com.example.ktgk_23it144

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CustomerViewModel(context: Context) : ViewModel() {
    private val dbHelper = DatabaseHelper(context)
    private val _customers = MutableStateFlow<List<Customer>>(emptyList())
    val customers: StateFlow<List<Customer>> = _customers


    fun loadCustomers() {
        viewModelScope.launch {
            _customers.value = dbHelper.getCustomers()
        }
    }

    fun createCustomers(customer: Customer) {
        viewModelScope.launch {
            if (dbHelper.addCustomer(customer)) {
                loadCustomers()
            }
        }
    }

    fun updateCustomer(customer: Customer) {
        viewModelScope.launch {
            if (dbHelper.updateCustomer(customer)) {
                loadCustomers()
            }
        }
    }

    fun deleteCustomer(id: Int) {
        viewModelScope.launch {
            if (dbHelper.deleteCustomer(id)) {
                loadCustomers()
            }
        }
    }
}