package com.creacionesnormita.mobile.core.network

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import com.creacionesnormita.mobile.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream
import kotlin.math.max

@Serializable
private data class CloudinaryRespuesta(
    @SerialName("secure_url") val secureUrl: String? = null,
    val error: CloudinaryError? = null,
)

@Serializable
private data class CloudinaryError(val message: String? = null)

/**
 * Sube imágenes a Cloudinary con un "unsigned upload preset" (no lleva ningún secreto en la app).
 * Cloudinary responde con la URL final (`secure_url`), que es la que se guarda en producto.imagenes.
 */
object CloudinaryUploader {

    private const val LADO_MAXIMO_PX = 1600
    private const val CALIDAD_JPEG = 85

    private val json = Json { ignoreUnknownKeys = true }
    private val http by lazy { HttpClient(Android) }

    fun estaConfigurado(): Boolean {
        val nube = BuildConfig.CLOUDINARY_CLOUD_NAME
        val preset = BuildConfig.CLOUDINARY_UPLOAD_PRESET
        return nube.isNotBlank() && nube != "null" && preset.isNotBlank() && preset != "null"
    }

    /** Devuelve la URL segura de la imagen subida, o falla con un mensaje legible. */
    suspend fun subirImagen(context: Context, uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (!estaConfigurado()) {
                return@withContext Result.failure(
                    IllegalStateException("Faltan CLOUDINARY_CLOUD_NAME o CLOUDINARY_UPLOAD_PRESET en local.properties")
                )
            }

            val bytes = prepararJpeg(context, uri)
                ?: return@withContext Result.failure(IllegalStateException("No se pudo leer la imagen"))

            val respuesta = http.submitFormWithBinaryData(
                url = "https://api.cloudinary.com/v1_1/${BuildConfig.CLOUDINARY_CLOUD_NAME}/image/upload",
                formData = formData {
                    append("upload_preset", BuildConfig.CLOUDINARY_UPLOAD_PRESET)
                    append(
                        key = "file",
                        value = bytes,
                        headers = Headers.build {
                            append(HttpHeaders.ContentType, "image/jpeg")
                            append(HttpHeaders.ContentDisposition, "filename=\"vestido.jpg\"")
                        }
                    )
                }
            )

            val cuerpo = respuesta.bodyAsText()
            val parseada = runCatching { json.decodeFromString<CloudinaryRespuesta>(cuerpo) }.getOrNull()

            if (respuesta.status.isSuccess() && !parseada?.secureUrl.isNullOrBlank()) {
                Result.success(parseada!!.secureUrl!!)
            } else {
                Result.failure(
                    IllegalStateException(parseada?.error?.message ?: "Cloudinary respondió ${respuesta.status.value}")
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Las fotos del celular pesan varios MB. Se reducen a [LADO_MAXIMO_PX] y se comprimen a JPEG
     * antes de subir, respetando la rotación original (EXIF).
     */
    private fun prepararJpeg(context: Context, uri: Uri): ByteArray? {
        val resolver = context.contentResolver

        // 1) Solo leer dimensiones
        // OJO: con inJustDecodeBounds = true decodeStream SIEMPRE devuelve null (no decodifica nada),
        // por eso no se usa su resultado: las dimensiones quedan guardadas en `limites`.
        val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        val flujo = resolver.openInputStream(uri) ?: return null
        flujo.use { BitmapFactory.decodeStream(it, null, limites) }
        if (limites.outWidth <= 0 || limites.outHeight <= 0) return null

        // 2) Decodificar ya reducido (potencia de 2) para no gastar memoria de más
        var muestreo = 1
        while (max(limites.outWidth, limites.outHeight) / (muestreo * 2) >= LADO_MAXIMO_PX) muestreo *= 2
        val opciones = BitmapFactory.Options().apply { inSampleSize = muestreo }
        var bitmap = resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opciones) } ?: return null

        // 3) Ajuste fino al lado máximo
        val ladoMayor = max(bitmap.width, bitmap.height)
        if (ladoMayor > LADO_MAXIMO_PX) {
            val escala = LADO_MAXIMO_PX.toFloat() / ladoMayor
            val reducido = Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width * escala).toInt().coerceAtLeast(1),
                (bitmap.height * escala).toInt().coerceAtLeast(1),
                true
            )
            if (reducido !== bitmap) bitmap.recycle()
            bitmap = reducido
        }

        // 4) Corregir rotación según EXIF
        val grados = resolver.openInputStream(uri)?.use { entrada ->
            when (ExifInterface(entrada).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }
        } ?: 0f
        if (grados != 0f) {
            val rotado = Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width, bitmap.height,
                Matrix().apply { postRotate(grados) }, true
            )
            if (rotado !== bitmap) bitmap.recycle()
            bitmap = rotado
        }

        val salida = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, CALIDAD_JPEG, salida)
        bitmap.recycle()
        return salida.toByteArray()
    }
}