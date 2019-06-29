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

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.dawnimpulse.wallup.ui.activities.CollectionVerticalActivity
import com.dawnimpulse.wallup.ui.objects.CollectionObject
import com.dawnimpulse.wallup.ui.objects.CollectionTransferObject
import com.dawnimpulse.wallup.utils.functions.gone
import com.dawnimpulse.wallup.utils.functions.openActivity
import com.dawnimpulse.wallup.utils.functions.show
import com.dawnimpulse.wallup.utils.functions.toJson
import com.dawnimpulse.wallup.utils.handlers.ImageHandler
import com.dawnimpulse.wallup.utils.reusables.COLLECTION
import kotlinx.android.synthetic.main.inflator_featured.view.*

/**
 * @info -
 *
 * @author - Saksham
 * @note Last Branch Update - master
 *
 * @note Created on 2019-06-27 by Saksham
 * @note Updates :
 */
class FeaturedHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val name = view.featuredText
    private val image = view.featuredBg
    private val heading = view.featuredHeading
    private val layout = view.featuredLayout

    private val context = view.context

    /**
     * binding data to view
     */
    fun bind(item: CollectionObject) {

        name.text = item.name
        ImageHandler.setImageOnVerticalCols(image, item.images[0].links.url)

        if (adapterPosition == 3)
            heading.show()
        else
            heading.gone()

        // click handling
        layout.setOnClickListener {
            val col = CollectionTransferObject(item.cid, item.images[0].links.url, item.name, item.description)
            context.openActivity(CollectionVerticalActivity::class.java){
                putString(COLLECTION, toJson(col))
            }
        }
    }
}