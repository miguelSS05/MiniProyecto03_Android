package com.example.miniproyecto03.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.miniproyecto03.data.Publicacion

@Composable
fun ListaPublicaciones(
    publicaciones: List<Publicacion>,
    modifier: Modifier = Modifier
) {
    if (publicaciones.isEmpty()) {
        EstadoVacio(modifier = modifier)
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(publicaciones, key = { it.id }) { pub ->
            PublicacionCard(publicacion = pub)
        }
    }
}

@Composable
private fun PublicacionCard(publicacion: Publicacion) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            // ── Foto ────────────────────────────────────────
            if (publicacion.fotoUri.isNotBlank()) {
                AsyncImage(
                    model = publicacion.fotoUri,
                    contentDescription = publicacion.titulo,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sin foto",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // ── Datos ───────────────────────────────────────
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = publicacion.titulo,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = publicacion.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = publicacion.fecha,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

@Composable
private fun EstadoVacio(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Aún no hay publicaciones",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Crea la primera desde el formulario",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ── Preview con datos de ejemplo ─────────────────────────────
private val previewPublicaciones = listOf(
    Publicacion(
        id = 1,
        titulo = "Mi primer post",
        descripcion = "Explorando Jetpack Compose y Room. ¡Funciona!",
        fotoUri = "",
        fecha = "15/03/2025"
    ),
    Publicacion(
        id = 2,
        titulo = "Tarde de café",
        descripcion = "Probando la cámara del emulador para la red social local.",
        fotoUri = "",
        fecha = "16/03/2025"
    ),
    Publicacion(
        id = 3,
        titulo = "Paseo por el parque",
        descripcion = "Un día perfecto para tomar fotos y publicarlas.",
        fotoUri = "",
        fecha = "17/03/2025"
    )
)

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun PreviewLista() {
    MaterialTheme {
        ListaPublicaciones(publicaciones = previewPublicaciones)
    }
}