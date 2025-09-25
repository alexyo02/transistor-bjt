/*
 * ImageHelper.kt
 * Implements the ImageHelper object
 * An ImageHelper provides helper methods for image related operations
 *
 * This file is part of
 * TRANSISTOR - Radio App for Android
 *
 * Copyright (c) 2015-25 - Y20K.org
 * Licensed under the MIT-License
 * http://opensource.org/licenses/MIT
 */


package org.y20k.transistor.helpers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.os.Build
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.toColorInt
import com.bumptech.glide.Glide
import org.y20k.transistor.R


/*
 * ImageHelper class
 */
object ImageHelper {

    /* Define log tag */
    private val TAG: String = ImageHelper::class.java.simpleName


    /* Create shortcut icon */
    fun createShortcutIcon(context: Context, stationImage: String, stationImageColor: Int): IconCompat {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // shortcut icon for Android 8+
            val iconSize: Int = (108 * UiHelper.getDensityScalingFactor(context)).toInt()
            val stationImageBitmap: Bitmap = Glide.with(context)
                .asBitmap()
                .load(stationImage)
                .error(R.drawable.ic_default_station_image_64dp)
                .override(iconSize, iconSize)
                .fitCenter()
                .submit()
                .get() // this blocks until the image is loaded - use only on background thread
            IconCompat.createWithAdaptiveBitmap(createSquareImage(stationImageBitmap, stationImageColor, iconSize, true))
        } else {
            // legacy shortcut icon
            val iconSize: Int = (48 * UiHelper.getDensityScalingFactor(context)).toInt()
            val stationImageBitmap: Bitmap = Glide.with(context)
                .asBitmap()
                .load(stationImage)
                .error(R.drawable.ic_default_station_image_64dp)
                .override(iconSize, iconSize)
                .fitCenter()
                .submit()
                .get() // this blocks until the image is loaded - use only on background thread
            IconCompat.createWithAdaptiveBitmap(createSquareImage(stationImageBitmap, stationImageColor, iconSize, true))
        }
    }


    /* Creates station image on a square background with the main station image color and option padding for adaptive icons */
    private fun createSquareImage(bitmap: Bitmap, backgroundColor: Int, size: Int, adaptivePadding: Boolean): Bitmap {
        // create background
        val background = Paint()
        background.style = Paint.Style.FILL
        if (backgroundColor != -1) {
            background.color = backgroundColor
        } else {
            background.color = "#ff595959".toColorInt() // color = system_neutral1_600
        }
        // create empty bitmap and canvas
        val outputImage: Bitmap = createBitmap(size, size)
        val imageCanvas: Canvas = Canvas(outputImage)
        // draw square background
        val right = size.toFloat()
        val bottom = size.toFloat()
        imageCanvas.drawRect(0f, 0f, right, bottom, background)
        // draw input image onto canvas using transformation matrix
        val paint = Paint()
        paint.isFilterBitmap = true
        imageCanvas.drawBitmap(bitmap, createTransformationMatrix(size, 0, bitmap.height.toFloat(), bitmap.width.toFloat(), adaptivePadding), paint)
        return outputImage
    }


    /* Creates a transformation matrix with the given size and optional padding  */
    private fun createTransformationMatrix(size: Int, yOffset: Int, inputImageHeight: Float, inputImageWidth: Float, scaled: Boolean): Matrix {
        val matrix = Matrix()
        // calculate padding
        var padding = 0f
        if (scaled) {
            padding = size.toFloat() / 4f
        }
        // define variables needed for transformation matrix
        var aspectRatio = 0.0f
        var xTranslation = 0.0f
        var yTranslation = 0.0f
        // landscape format and square
        if (inputImageWidth >= inputImageHeight) {
            aspectRatio = (size - padding * 2) / inputImageWidth
            xTranslation = 0.0f + padding
            yTranslation = (size - inputImageHeight * aspectRatio) / 2.0f + yOffset
        } else if (inputImageHeight > inputImageWidth) {
            aspectRatio = (size - padding * 2) / inputImageHeight
            yTranslation = 0.0f + padding + yOffset
            xTranslation = (size - inputImageWidth * aspectRatio) / 2.0f
        }
        // construct transformation matrix
        matrix.postTranslate(xTranslation, yTranslation)
        matrix.preScale(aspectRatio, aspectRatio)
        return matrix
    }

}
