package com.example.watertracker.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RegisterTagDialog(uid: String, onDismiss: () -> Unit, onRegister: (Float) -> Unit) {
    val capacityState = remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Register New Bottle") },
        text = {
            Column {
                Text("Tag UID: $uid")
                OutlinedTextField(
                    value = capacityState.value,
                    onValueChange = { capacityState.value = it },
                    label = { Text("Capacity (L)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                capacityState.value.toFloatOrNull()?.let { onRegister(it) }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Cancel") }
        },
        modifier = Modifier.padding(16.dp)
    )
}
