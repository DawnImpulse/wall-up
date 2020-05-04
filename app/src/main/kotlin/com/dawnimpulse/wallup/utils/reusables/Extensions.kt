/**
 * ISC License
 *
 * Copyright 2020, Saksham (DawnImpulse)
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
package com.dawnimpulse.wallup.utils.reusables

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.dawnimpulse.wallup.BuildConfig
import com.dawnimpulse.wallup.utils.handlers.HandlerImage

/**
 * @info -
 *
 * @author - Saksham
 * @note Last Branch Update - master
 *
 * @note Created on 2020-03-06 by Saksham
 * @note Updates :
 */

/**
 * view gone
 */
fun View.gone() {
    visibility = View.GONE
}

/**
 * view hide
 */
fun View.hide() {
    visibility = View.INVISIBLE
}

/**
 * view show
 */
fun View.show() {
    visibility = View.VISIBLE
}

/**
 * toast on fragment
 */
fun Fragment.toast(message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(activity, message, length).show()
}

/**
 * debug toast on fragment
 */
fun Fragment.toastd(message: String, length: Int = Toast.LENGTH_SHORT) {
    if (BuildConfig.DEBUG)
        Toast.makeText(activity, message, length).show()
}

/**
 * toast
 */
fun Context.toast(message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, length).show()
}

/**
 * debug toast
 */
fun Context.toastd(message: String, length: Int = Toast.LENGTH_SHORT) {
    if (BuildConfig.DEBUG)
        Toast.makeText(this, message, length).show()
}

/**
 * convert dp to px
 *
 * @param value
 */
fun Context.dpToPx(value:Int): Int{
    return F.dpToPx(value, this)
}

/**
 * set the string to image
 */
fun String.setImage(view: ImageView) {
    HandlerImage.fetchAndSetImage(view, this)
}

/**
 * fetch and set image on view using Glide
 */
fun ImageView.fetchAndSetImage(url: String) {
    HandlerImage.fetchAndSetImage(this, url)
}

// log messages
fun logd(message: Any) {
    if (BuildConfig.DEBUG)
        Log.d("wallup", "${Exception().stackTrace[1].className.replace("${BuildConfig.APPLICATION_ID}.", "")} :: $message")
}

fun loge(message: Any) {
    if (BuildConfig.DEBUG)
        Log.e("wallup", "${Exception().stackTrace[1].className.replace("${BuildConfig.APPLICATION_ID}.", "")} :: $message")
}