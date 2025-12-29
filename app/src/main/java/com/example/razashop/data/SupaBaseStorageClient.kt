package com.example.razashop.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.razashop.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import java.io.ByteArrayOutputStream

class SupaBaseStorageClient(@param:ApplicationContext private val context: Context) {

    val supabase = createSupabaseClient(
        BuildConfig.SUPABASE_URL, BuildConfig.SUPABASE_KEY
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
            supabase.storage.from("chatter_images").upload(fileName, imageByteArray)
            return supabase.storage.from("chatter_images").publicUrl(fileName)
        } catch (e: Exception) {
            return null
        }

    }


}