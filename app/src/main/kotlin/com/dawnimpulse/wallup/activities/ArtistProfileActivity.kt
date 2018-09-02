package com.dawnimpulse.wallup.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.dawnimpulse.wallup.R
import com.dawnimpulse.wallup.adapters.ArtistPhotosAdapter
import com.dawnimpulse.wallup.handlers.ImageHandler
import com.dawnimpulse.wallup.models.UnsplashModel
import com.dawnimpulse.wallup.pojo.ImagePojo
import com.dawnimpulse.wallup.pojo.UserPojo
import com.dawnimpulse.wallup.utils.C
import com.dawnimpulse.wallup.utils.L
import com.dawnimpulse.wallup.utils.Toast
import kotlinx.android.synthetic.main.activity_artist_profile.*


/**
 * @author Saksham
 *
 * @note Last Branch Update - master
 * @note Created on 2018-09-31 by Saksham
 *
 * @note Updates :
 *  Saksham - 2018 09 31 - master - click listener
 */
class ArtistProfileActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var userPojo: UserPojo
    private val NAME = "ArtistProfileActivity"

    // on create
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_artist_profile)

        var model = UnsplashModel(lifecycle)
        model.userDetails(intent.extras.getString(C.USERNAME)) { error, details ->
            if (error != null) {
                L.d(NAME, error.toString())
                Toast.short(this, "Error Occurred")
            } else {
                userPojo = details as UserPojo
                details()
                artistLayout.visibility = View.VISIBLE
                artistUnsplash.visibility = View.VISIBLE
                artistProgress.visibility = View.GONE
            }
        }
        model.userPhotos(1, 8, intent.extras.getString(C.USERNAME)) { error, details ->
            if (error != null) {
                L.d(NAME, error.toString())
                Toast.short(this, "Error Occurred In Photos")
            } else {
                var adapter = ArtistPhotosAdapter(this@ArtistProfileActivity, lifecycle, details as List<ImagePojo?>)
                artistPhotos.layoutManager = LinearLayoutManager(this@ArtistProfileActivity, LinearLayoutManager.HORIZONTAL, false)
                artistPhotos.adapter = adapter
                artistPhotos.clipToPadding = false
            }
        }

        artistPhotosMore.setOnClickListener(this)
    }

    // on click
    override fun onClick(v: View) {
        when (v.id) {
            artistPhotosMore.id -> {
                var intent = Intent(this, GeneralImagesActivity::class.java)
                intent.putExtra(C.TYPE, C.ARTIST_IMAGES)
                intent.putExtra(C.USERNAME, userPojo.username)
                startActivity(intent)
            }
        }
    }

    private fun details() {
        artistUsername.text = "@ ${userPojo.username}"
        artistFirstName.text = userPojo.first_name
        artistLastName.text = userPojo.last_name
        artistPhotoNo.text = userPojo.total_photos.toString()
        artistCollectionNo.text = userPojo.total_collections.toString()
        artistLikesNo.text = userPojo.total_likes.toString()

        if (userPojo.bio != null)
            artistInfo.text = userPojo.bio
        else
            artistInfo.visibility = View.GONE

        if (userPojo.portfolio_url != null)
            artistUrl.text = userPojo.portfolio_url
        else
            artistUrlL.visibility = View.GONE

        if (userPojo.location != null)
            artistLocation.text = userPojo.location
        else
            artistLocationL.visibility = View.GONE

        ImageHandler.setImageInView(lifecycle, artistImage, userPojo.profile_image!!.large)
    }
}