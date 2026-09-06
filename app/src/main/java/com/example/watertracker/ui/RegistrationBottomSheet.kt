package com.example.watertracker.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RegistrationBottomSheet(
    uid: String,
    onDismiss: () -> Unit,
    onRegister: (capacityLiters: Float) -> Unit
) {
    var capacityText = remember { mutableStateOf("") }
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        Text(text = "Register new bottle (UID: $uid)")
        TextField(
            value = capacityText.value,
            onValueChange = { capacityText.value = it },
            label = { Text("Capacity (Liters)") },
            placeholder = { Text("e.g., 0.5") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                capacityText.value.toFloatOrNull()?.let { onRegister(it) }
                onDismiss()
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Save")
        }
        Button(onClick = onDismiss, modifier = Modifier.padding(top = 4.dp)) {
            Text("Cancel")
        }
    }
}
