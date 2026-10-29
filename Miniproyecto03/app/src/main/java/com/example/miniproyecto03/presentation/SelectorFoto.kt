package com.example.miniproyecto03.presentation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun SelectorFoto(
    mostrar: Boolean,
    onCerrar: () -> Unit,
    onFotoLista: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var rutaCamaraPendiente by rememberSaveable { mutableStateOf<String?>(null) }
    var permisoDenegado by rememberSaveable { mutableStateOf<String?>(null) }

    //camara
    val camaraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { exito ->
        val ruta = rutaCamaraPendiente
        rutaCamaraPendiente = null
        if (ruta != null) {
            val archivo = File(ruta)
            if (exito) onFotoLista(Uri.fromFile(archivo).toString())
            else archivo.delete() // el usuario canceló: borramos el archivo vacío
        }
    }

    fun lanzarCamara() {
        val archivo = crearArchivoFoto(context)
        rutaCamaraPendiente = archivo.absolutePath
        val uri = FileProvider.getUriForFile(
            context, "${context.packageName}.fileprovider", archivo
        )
        camaraLauncher.launch(uri)
    }

    val permisoCamaraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) lanzarCamara() else permisoDenegado = "cámara"
    }

    fun abrirCamara() {
        if (context.tienePermiso(Manifest.permission.CAMERA)) lanzarCamara()
        else permisoCamaraLauncher.launch(Manifest.permission.CAMERA)
    }

    //galeria
    val galeriaLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val ruta = withContext(Dispatchers.IO) { copiarAlmacenamientoInterno(context, uri) }
                if (ruta != null) onFotoLista(ruta)
            }
        }
    }

    fun lanzarGaleria() {
        galeriaLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    val permisoGaleriaLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (context.tieneAccesoGaleria()) lanzarGaleria() else permisoDenegado = "galería"
    }

    fun abrirGaleria() {
        if (context.tieneAccesoGaleria()) lanzarGaleria()
        else permisoGaleriaLauncher.launch(permisosGaleria())
    }

    //dialogo camara o galeria?
    if (mostrar) {
        AlertDialog(
            onDismissRequest = onCerrar,
            title = { Text("Agregar foto") },
            text = {
                Column {
                    TextButton(
                        onClick = { onCerrar(); abrirCamara() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Tomar fotografía")
                    }
                    TextButton(
                        onClick = { onCerrar(); abrirGaleria() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Elegir de la galería")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = onCerrar) { Text("Cancelar") }
            }
        )
    }

    //dialogo permiso denegado
    permisoDenegado?.let { tipo ->
        AlertDialog(
            onDismissRequest = { permisoDenegado = null },
            title = { Text("Permiso necesario") },
            text = {
                Text(
                    "Para usar la $tipo necesitamos el permiso correspondiente. " +
                            "Puedes activarlo desde los ajustes de la aplicación."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    permisoDenegado = null
                    context.startActivity(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.packageName, null)
                        )
                    )
                }) { Text("Abrir ajustes") }
            },
            dismissButton = {
                TextButton(onClick = { permisoDenegado = null }) { Text("Cancelar") }
            }
        )
    }
}

//helpers permisos

private fun Context.tienePermiso(permiso: String): Boolean =
    ContextCompat.checkSelfPermission(this, permiso) == PackageManager.PERMISSION_GRANTED

private fun permisosGaleria(): Array<String> = when {
    Build.VERSION.SDK_INT >= 34 -> arrayOf(
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
    )
    Build.VERSION.SDK_INT >= 33 -> arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
    else -> arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
}

private fun Context.tieneAccesoGaleria(): Boolean = when {
    Build.VERSION.SDK_INT >= 34 ->
        tienePermiso(Manifest.permission.READ_MEDIA_IMAGES) ||
                tienePermiso(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
    Build.VERSION.SDK_INT >= 33 -> tienePermiso(Manifest.permission.READ_MEDIA_IMAGES)
    else -> tienePermiso(Manifest.permission.READ_EXTERNAL_STORAGE)
}

//helpers archivos

private fun crearArchivoFoto(context: Context): File {
    val carpeta = File(context.filesDir, "fotos").apply { mkdirs() }
    return File(carpeta, "foto_${System.currentTimeMillis()}.jpg")
}

private fun copiarAlmacenamientoInterno(context: Context, origen: Uri): String? = try {
    val destino = crearArchivoFoto(context)
    context.contentResolver.openInputStream(origen)?.use { entrada ->
        destino.outputStream().use { salida -> entrada.copyTo(salida) }
    }
    Uri.fromFile(destino).toString()
} catch (e: Exception) {
    null
}