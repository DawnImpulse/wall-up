/*
ISC License

Copyright 2018, Saksham (DawnImpulse)

Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted,
provided that the above copyright notice and this permission notice appear in all copies.

THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS,
WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE
OR PERFORMANCE OF THIS SOFTWARE.*/
package com.dawnimpulse.wallup.sheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.dawnimpulse.permissions.android.Permissions
import com.dawnimpulse.wallup.R
import com.dawnimpulse.wallup.activities.CropActivity
import com.dawnimpulse.wallup.handlers.DialogHandler
import com.dawnimpulse.wallup.models.UnsplashModel
import com.dawnimpulse.wallup.pojo.ImagePojo
import com.dawnimpulse.wallup.utils.*
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.bottom_sheet_image.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * @author Saksham
 *
 * @note Last Branch Update - master
 * @note Created on 2018-12 by Saksham
 *
 * @note Updates :
 * Saksham - 2018 12 20 - master - complete handling
 */
class ModalSheetImage : RoundedBottomSheetDialogFragment(), View.OnClickListener {
    private var dialogOpen = false
    private var isWallpaper = false
    private lateinit var details: ImagePojo
    private lateinit var model: UnsplashModel
    private lateinit var colSheet: ModalSheetCollection
    private lateinit var loginSheet: ModalSheetUnsplash

    // on create
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_image, container, false);
    }

    // view created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        details = Gson().fromJson(arguments!!.getString(C.IMAGE_POJO), ImagePojo::class.java)
        model = UnsplashModel(lifecycle)
        colSheet = ModalSheetCollection()
        loginSheet = ModalSheetUnsplash()

        sheetImageDownload.setOnClickListener(this)
        sheetImageCol.setOnClickListener(this)
        sheetImageUnsplash.setOnClickListener(this)
        sheetImageWall.setOnClickListener(this)
    }

    // on start
    override fun onStart() {
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
        super.onStart()
    }

    // on resume for dialog only
    override fun onResume() {
        super.onResume()

        if (dialogOpen)
            DialogHandler.download(context!!, details!!.id, details!!.urls!!.full, isWallpaper) {
                model.downloadedPhoto(details!!.links!!.download_location)
            }
    }

    // on destroy
    override fun onDestroy() {
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    // on click
    override fun onClick(v: View) {
        when (v.id) {
            sheetImageDownload.id -> {
                isWallpaper = false

                Permissions.askWriteExternalStoragePermission(context!!) { no, _ ->
                    if (no != null)
                        context?.toast("Kindly provide external storage permission in Settings")
                    else
                        DialogHandler.download(context!!, details!!.id, details!!.urls!!.raw) {
                            model.downloadedPhoto(details!!.links!!.download_location)
                        }
                }
            }
            sheetImageCol.id -> {
                if (Config.USER_API_KEY.isNotEmpty()) {
                    var bundle = Bundle()
                    details?.let {
                        bundle.putString(C.IMAGE_POJO, Gson().toJson(details!!))
                        it.current_user_collections?.let { cols ->
                            if (cols.isNotEmpty())
                                bundle.putString(C.COLLECTIONS, Gson().toJson(cols))
                        }
                    }
                    colSheet.arguments = bundle
                    colSheet.show((context as AppCompatActivity).supportFragmentManager)
                } else
                    loginSheet.show((context as AppCompatActivity).supportFragmentManager)
            }
            sheetImageUnsplash.id -> context!!.startWeb(F.unsplashImage(details!!.id))
            sheetImageWall.id -> {
                isWallpaper = true
                Permissions.askWriteExternalStoragePermission(context!!) { no, _ ->
                    if (no != null)
                        Toast.short(context!!, "Kindly provide external storage permission in Settings")
                    else
                        DialogHandler.download(context!!, details!!.id, details!!.urls!!.raw, true) {
                            model.downloadedPhoto(details!!.links!!.download_location)
                        }

                }
            }
        }
    }

    // on message event
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: Event) {
        if (event.obj.has(C.TYPE)) {
            if (event.obj.getString(C.TYPE) == C.DOWNLOAD_PATH) {
                dialogOpen = true
            }
            if (event.obj.getString(C.TYPE) == C.WALLPAPER) {
                val quality = Prefs.getString(C.IMAGE_DOWNLOAD_QUALITY, Config.IMAGE_DOWNLOAD_QUALITY)
                var newUrl = details!!.urls!!.raw
                if (quality != C.O)
                    newUrl = "$newUrl&q=$quality"

                context!!.openActivity(CropActivity::class.java) {
                    putString(C.IMAGE, newUrl)
                    putString(C.ID, "${details!!.id}_${quality.replace("&h=", "")}")
                }
                model.downloadedPhoto(details!!.links!!.download_location)
            }
        }
    }

}