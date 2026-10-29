package com.example.miniproyecto03.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FormularioPublicacion(
    fotoUri: String?,
    onSeleccionarFoto: () -> Unit,
    onPublicar: (titulo: String, descripcion: String, fotoUri: String, fecha: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var titulo by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var fecha by rememberSaveable { mutableStateOf(hoyComoString()) }

    val puedePublicar = titulo.isNotBlank() && descripcion.isNotBlank() && fotoUri != null

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Nueva publicación",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        // ── Título ────────────────────────────────────────────
        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título") },
            placeholder = { Text("Ej. Mi primer post") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // ── Descripción ───────────────────────────────────────
        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            placeholder = { Text("¿Qué estás pensando?") },
            minLines = 4,
            maxLines = 6,
            modifier = Modifier.fillMaxWidth()
        )

        // ── Fecha (generada automáticamente + seleccionable) ──
        FechaSelector(
            fecha = fecha,
            onFechaCambiada = { fecha = it }
        )

        // ── Foto (placeholder + vista previa) ─────────────────
        if (fotoUri != null) {
            AsyncImage(
                model = fotoUri,
                contentDescription = "Foto seleccionada",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }

        TextButton(
            onClick = onSeleccionarFoto,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(if (fotoUri == null) "Agregar foto" else "Cambiar foto")
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = {
                onPublicar(titulo.trim(), descripcion.trim(), fotoUri ?: "", fecha)
                titulo = ""
                descripcion = ""
                fecha = hoyComoString()
            },
            enabled = puedePublicar,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Publicar")
        }
    }
}

/**
 * Componente para visualizar la fecha. Se muestra generada automáticamente
 * (fecha del día) y ofrece un botón "Cambiar" que abre un DatePicker.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FechaSelector(
    fecha: String,
    onFechaCambiada: (String) -> Unit
) {
    var mostrarDatePicker by rememberSaveable { mutableStateOf(false) }

    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.DateRange,
                contentDescription = "Fecha",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Fecha de publicación",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = fecha,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
            TextButton(onClick = { mostrarDatePicker = true }) {
                Text("Cambiar")
            }
        }
    }

    if (mostrarDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { mostrarDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { ms ->
                        onFechaCambiada(
                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                .format(Date(ms))
                        )
                    }
                    mostrarDatePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

// Helpers
private fun hoyComoString(): String =
    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

@Preview(showBackground = true)
@Composable
private fun PreviewFormulario() {
    MaterialTheme {
        FormularioPublicacion(
            fotoUri = null,
            onSeleccionarFoto = {},
            onPublicar = { _, _, _, _ -> }
        )
    }
}