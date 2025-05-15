package com.example.goplanify.ui.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object ImageUtils {
    private const val TAG = "ImageUtils"
    private const val IMAGE_DIRECTORY = "GoPlanify/Images"

    /**
     * Copia una imagen desde una URI externa a la carpeta privada de la aplicación
     * y devuelve la ruta del archivo local
     */
    fun saveImageToAppStorage(context: Context, imageUri: Uri): String? {
        try {
            // Crear directorio de imágenes si no existe
            val directory = File(context.filesDir, IMAGE_DIRECTORY)
            if (!directory.exists()) {
                directory.mkdirs()
            }

            // Generar nombre único para el archivo
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "IMG_${timestamp}_${UUID.randomUUID().toString().substring(0, 8)}.jpg"
            val destinationFile = File(directory, fileName)

            // Copiar la imagen
            context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                FileOutputStream(destinationFile).use { outputStream ->
                    val buffer = ByteArray(4 * 1024) // 4k buffer
                    var read: Int
                    while (inputStream.read(buffer).also { read = it } != -1) {
                        outputStream.write(buffer, 0, read)
                    }
                    outputStream.flush()
                }
            }

            return destinationFile.absolutePath
        } catch (e: IOException) {
            Log.e(TAG, "Error saving image", e)
            return null
        }
    }

    /**
     * Obtener información sobre una imagen a partir de su URI
     */
    fun getImageInfo(context: Context, imageUri: Uri): ImageInfo {
        val projection = arrayOf(
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.DATE_TAKEN
        )

        val cursor = context.contentResolver.query(
            imageUri,
            projection,
            null,
            null,
            null
        )

        val fileName = cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
                if (nameIndex >= 0) it.getString(nameIndex) else "Unknown"
            } else {
                "Unknown"
            }
        } ?: "Unknown"

        return ImageInfo(fileName, imageUri)
    }

    /**
     * Elimina un archivo de imagen dado su path
     */
    fun deleteImage(imagePath: String): Boolean {
        val file = File(imagePath)
        return if (file.exists()) {
            file.delete()
        } else {
            false
        }
    }

    /**
     * Clase auxiliar para almacenar información básica de una imagen
     */
    data class ImageInfo(
        val name: String,
        val uri: Uri
    )
}