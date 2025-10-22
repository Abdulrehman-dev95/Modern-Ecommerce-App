package com.example.razashop.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import java.io.ByteArrayOutputStream

class SupaBaseStorageClient(@ApplicationContext private val context: Context) {
    companion object {
        const val BUCKET_NAME = "chatter_images"
    }

    private val url = "https://nnmunfptcaijujubpdif.supabase.co"
    private val key =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5ubXVuZnB0Y2FpanVqdWJwZGlmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjA1NDU5MTYsImV4cCI6MjA3NjEyMTkxNn0.CTzrGw-K6sLbRc1uJCy5H_4Kr0KGUgrw2r6xKoNCbAM"

    val supabase = createSupabaseClient(
        url, key
    ) {
        install(Storage)
    }

    suspend fun uploadImage(uri: Uri): String? {
        try {
            val extension = uri.path?.substringAfterLast(".") ?: "jpg"
            val fileName = "${System.currentTimeMillis()}.$extension"

            val imageBitmap = BitmapFactory.decodeStream(
                context.contentResolver.openInputStream(uri)
            )
            val byteArrayOutputStream = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 96, byteArrayOutputStream)
            val imageByteArray = byteArrayOutputStream.toByteArray()
            supabase.storage.from(BUCKET_NAME).upload(fileName, imageByteArray)
            return supabase.storage.from(BUCKET_NAME).publicUrl(fileName)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }


}