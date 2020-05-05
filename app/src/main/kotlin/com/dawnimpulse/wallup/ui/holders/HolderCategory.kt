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
package com.dawnimpulse.wallup.ui.holders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.dawnimpulse.wallup.objects.ObjectCategory
import com.dawnimpulse.wallup.utils.reusables.fetchAndSetImage
import com.dawnimpulse.wallup.utils.reusables.gone
import com.dawnimpulse.wallup.utils.reusables.show
import kotlinx.android.synthetic.main.adapter_category.view.*

/**
 * @info - holder for category on homescreen
 *
 * @author - Saksham
 * @note Last Branch Update - master
 *
 * @note Created on 2020-05-02 by Saksham
 * @note Updates :
 */
class HolderCategory(view: View) : RecyclerView.ViewHolder(view) {
    private val image = view.adapter_category_image
    private val name = view.adapter_category_name
    private val catName = view.adapter_category_parent_name

    /**
     * bind category to view
     */
    fun bind(category: ObjectCategory, position: Int) {
        if (position == 3) catName.show() else catName.gone()
        name.text = category.name
        image.fetchAndSetImage(category.cover)
    }
}