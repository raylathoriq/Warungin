package com.example.warungin

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide

object ImageHelper {
    fun loadImage(context: Context, imagePath: String?, imageView: ImageView) {
        if (imagePath.isNullOrEmpty()) {
            imageView.setImageResource(R.drawable.ic_placeholder)
            return
        }

        // Cek apakah data ini adalah nama resource drawable lokal (seperti "gula" atau "madu")
        val resId = context.resources.getIdentifier(imagePath, "drawable", context.packageName)
        if (resId != 0) {
            // Jika ada di drawable, muat dari resource drawable lokal
            Glide.with(context)
                .load(resId)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .into(imageView)
        } else {
            // Jika berupa file path lokal (file://...) atau web URL, Glide akan memuatnya secara langsung
            Glide.with(context)
                .load(imagePath)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .into(imageView)
        }
    }
}
