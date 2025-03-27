package com.example.ktgk_23it144

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ktgk_23it144.ui.theme.KTGK_23IT144Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CustomerScreen(viewModel = CustomerViewModel(applicationContext))
        }
    }
}

@Composable
fun CustomerScreen(viewModel: CustomerViewModel) {
    LaunchedEffect(Unit) {
        viewModel.loadCustomers()
    }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }
    var editCustomerId by remember { mutableStateOf(-1) }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        Spacer(modifier = Modifier.height(8.dp))

        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        Spacer(modifier = Modifier.height(8.dp))

        TextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") })
        Spacer(modifier = Modifier.height(8.dp))

        if (isEditing) {
            Button(onClick = {
                if (editCustomerId != -1) {
                    val updatedCustomer = Customer(editCustomerId, name, email, phone)
                    viewModel.updateCustomer(updatedCustomer)
                    isEditing = false
                    editCustomerId = -1
                    name = ""; email = ""; phone = ""
                }
            }) {
                Text("Update Customer")
            }
        } else {
            Button(onClick = {
                val newCustomer = Customer(0, name, email, phone)
                viewModel.createCustomers(newCustomer)
                name = ""; email = ""; phone = ""
            }) {
                Text("Add Customer")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        CustomerList(viewModel, onEdit = { customer ->
            isEditing = true
            editCustomerId = customer.id
            name = customer.name
            email = customer.email
            phone = customer.phone_number
        })
    }
}

@Composable
fun CustomerList(viewModel: CustomerViewModel, onEdit: (Customer) -> Unit) {
    val customers by viewModel.customers.collectAsState()

    LazyColumn {
        items(customers.size) { index ->
            CustomerItem(
                customer = customers[index],
                onDelete = { viewModel.deleteCustomer(it) },
                onEdit = { onEdit(it) }
            )
        }
    }
}

@Composable
fun CustomerItem(customer: Customer, onDelete: (Int) -> Unit, onEdit: (Customer) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "ID: ${customer.id}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Name: ${customer.name}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Email: ${customer.email}", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "Phone: ${customer.phone_number}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Column {
                Button(onClick = { onDelete(customer.id) }) {
                    Text("Delete")
                }
                Spacer(modifier = Modifier.height(4.dp))
                Button(onClick = { onEdit(customer) }) {
                    Text("Edit")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KTGK_23IT144Theme {
        Text("Hello Android!")
    }
}
