/**
 * ISC License
 *
 * Copyright 2018-2019, Saksham (DawnImpulse)
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted,
 * provided that the above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
 * INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS,
 * WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE
 * OR PERFORMANCE OF THIS SOFTWARE.
 **/
package com.dawnimpulse.wallup.ui.holders

import android.graphics.Bitmap
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.dawnimpulse.wallup.R
import com.dawnimpulse.wallup.ui.objects.ImageObject
import com.dawnimpulse.wallup.utils.functions.*
import com.dawnimpulse.wallup.utils.handlers.DialogHandler
import com.dawnimpulse.wallup.utils.handlers.DownloadHandler
import com.dawnimpulse.wallup.utils.handlers.ImageHandler
import com.dawnimpulse.wallup.utils.handlers.WallpaperHandler
import com.dawnimpulse.wallup.utils.reusables.Config
import jp.wasabeef.blurry.Blurry
import kotlinx.android.synthetic.main.inflator_image_fullscreen.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.sourcei.android.permissions.Permissions

/**
 * @info -
 *
 * @author - Saksham
 * @note Last Branch Update - master
 *
 * @note Created on 2019-06-28 by Saksham
 * @note Updates :
 */
class CollectionVerticalHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val bg = view.previewImageBg
    private val image = view.previewImage
    private val authorL = view.previewImageAuthorL
    private val authorN = view.previewImageAuthorName
    private val link = view.previewImageAuthorLink
    private val buttons = view.previewImageButton
    private val info = view.previewImageInfo
    private val download = view.previewImageDownload
    private val wallpaper = view.previewImageWallpaper
    private val left = view.previewImageLeft
    private val right = view.previewImageRight

    private val context = view.context
    private val activity = context as AppCompatActivity

    /**
     * binding image
     * @param image
     */
    fun bind(item: ImageObject) {

        left.show()
        right.show()


        // on click handling
        val listener = View.OnClickListener {
            when (it.id) {
                // image link
                link.id -> F.startWeb(context, item.links.html)

                // download
                download.id -> {
                    Permissions.askWriteExternalStoragePermission(context) { e, r ->
                        e?.let {
                            context.toast("kindly provide storage permissions")
                        }
                        r?.let {
                            context.toast("downloading image, check notification")
                            F.mkdir()
                            DownloadHandler.downloadData(context, "${item.links.url}&fm=webp", "${item.iid}.webp")
                        }
                    }
                }

                // wallpaper
                wallpaper.id -> {

                    // check permissions
                    Permissions.askWriteExternalStoragePermission(context) { e, r ->
                        e?.let {
                            context.toast("kindly provide storage permissions")
                        }
                        r?.let {
                            val file = "${Config.DEFAULT_DOWNLOAD_PATH}/${item.iid}.webp"

                            // file already exists
                            if (file.toFile().exists())
                                WallpaperHandler.askToSetWallpaper(context, file)
                            else {
                                // download file with progress
                                DialogHandler.downloadProgress(context, "${item.links.url}&fm=webp", "${item.iid}.webp") {
                                    if (it) {
                                        // file downloaded set it
                                        WallpaperHandler.askToSetWallpaper(context, file)
                                    } else
                                        context.toast("failed setting wallpaper")
                                }
                            }
                        }
                    }
                }

                // info
                image.id, info.id -> showInfo()
            }
        }

        image.setOnClickListener(listener)
        info.setOnClickListener(listener)
        wallpaper.setOnClickListener(listener)
        download.setOnClickListener(listener)
        link.setOnClickListener(listener)
    }

    /**
     * set image & details
     */
    fun details(image: ImageObject) {

        authorN.text = image.author

        ImageHandler.getBitmapImageFullscreen(context, image.links.url) {
            setBlurZoom(it,image)
        }
    }


    /**
     * set background blur & start zoom animation
     * @param bitmap
     */
    private fun setBlurZoom(bitmap: Bitmap?, item:ImageObject) {
        activity.runOnUiThread {
            bitmap?.let {

                image.setImageBitmap(it)
                Blurry.with(context)
                        .async()
                        .sampling(4)
                        .from(bitmap)
                        .into(bg)
            }

            if (item.width > item.height)
                image.animation = AnimationUtils.loadAnimation(context, R.anim.image_zoom_out)
            else
                image.animation = AnimationUtils.loadAnimation(context, R.anim.image_zoom_in_out)
        }
    }

    /**
     * hide information bars
     */
    fun hideInfo() {
        info.show()
        authorL.animate().translationY(-buttons.height.toFloat())
        buttons.animate().translationY(buttons.height.toFloat())
        info.animate().alpha(1f)
    }

    /**
     * show information bars
     */
    private fun showInfo() {
        authorL.animate().translationY(0f)
        buttons.animate().translationY(0f)
        info.animate().alpha(0f)
        info.gone()

        GlobalScope.launch {
            delay(3000)
            activity.runOnUiThread {
                hideInfo()
            }
        }
    }
}