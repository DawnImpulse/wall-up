package com.dawnimpulse.wallup.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.core.widget.toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dawnimpulse.wallup.R
import com.dawnimpulse.wallup.adapters.CollectionsAdapter
import com.dawnimpulse.wallup.interfaces.OnLoadMoreListener
import com.dawnimpulse.wallup.models.UnsplashModel
import com.dawnimpulse.wallup.pojo.CollectionPojo
import com.dawnimpulse.wallup.utils.C
import com.dawnimpulse.wallup.utils.L
import kotlinx.android.synthetic.main.fragment_collection.*

/**
 * @author Saksham
 *
 * @note Last Branch Update - master
 * @note Created on 2018-09-09 by Saksham
 *
 * @note Updates :
 * Saksham - 2018 09 15 - master - adding wallup content
 * Saksham - 2018 12 04 - master - new reload / progress
 */
class CollectionFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, OnLoadMoreListener, View.OnClickListener {
    private val NAME = "CollectionFragment"
    private lateinit var model: UnsplashModel
    private lateinit var cols: MutableList<CollectionPojo?>
    private lateinit var adapter: CollectionsAdapter
    private lateinit var type: String
    private var nextPage = 2

    // on create view
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_collection, container, false)
    }

    // on view created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        type = arguments!!.getString(C.TYPE)
        model = UnsplashModel(lifecycle)

        when (type) {
            C.FEATURED -> model.featuredCollections(1, callback)
            C.CURATED -> model.curatedCollections(1, callback)
            C.WALLUP -> model.userCollections(C.DAWN_IMPULSE, 1, 30, callback)
        }

        colLSwipe.setOnRefreshListener(this)
        colLReload.setOnClickListener(this)
        colLProgressI.animation = AnimationUtils.loadAnimation(context, R.anim.rotation_progress)
    }

    // on click
    override fun onClick(v: View) {
        colLReload.isVisible = false
        colLProgress.isVisible = true
        when (type) {
            C.FEATURED -> model.featuredCollections(1, callback)
            C.CURATED -> model.curatedCollections(1, callback)
            C.WALLUP -> model.userCollections(C.DAWN_IMPULSE, 1, 30, callback)
        }
    }

    // on refresh
    override fun onRefresh() {
        when (type) {
            C.FEATURED -> model.featuredCollections(1, callback)
            C.CURATED -> model.curatedCollections(1, callback)
            C.WALLUP -> model.userCollections(C.DAWN_IMPULSE, 1, 30, callback)
        }
    }

    // on load more
    override fun onLoadMore() {
        cols.add(null)
        adapter.notifyItemInserted(cols.size)
        when (type) {
            C.FEATURED -> model.featuredCollections(nextPage, callbackPaginated)
            C.CURATED -> model.curatedCollections(nextPage, callbackPaginated)
            C.WALLUP -> model.userCollections(C.DAWN_IMPULSE, nextPage, 30, callbackPaginated)
        }
    }

    // callback for setting images in adapter
    private var callback = object : (Any?, Any?) -> Unit {
        override fun invoke(error: Any?, response: Any?) {
            error?.let {
                L.d(NAME, error)
                colLSwipe.isRefreshing = false
                colLProgress.isVisible = false
                colLReload.isVisible = true
                context!!.toast("error fetching $type collections")
            }
            response?.let {
                cols = (response as List<CollectionPojo>).toMutableList()
                adapter = CollectionsAdapter(lifecycle, cols, type, colLRecycler)
                colLRecycler.layoutManager = LinearLayoutManager(context)
                colLRecycler.adapter = adapter
                colLSwipe.isVisible = true
                colLRecycler.isVisible = true
                colLSwipe.isRefreshing = false
                colLProgress.isVisible = false
                colLReload.isVisible = false

                adapter.setOnLoadMoreListener(this@CollectionFragment)
            }
        }
    }

    // callback for setting images in adapter
    private var callbackPaginated = object : (Any?, Any?) -> Unit {
        override fun invoke(error: Any?, response: Any?) {
            error?.let {
                L.d(NAME, error)
                context!!.toast("unable to fetch curatedCollections")
            }
            response?.let {
                nextPage++
                cols.removeAt(cols.size - 1)
                adapter.notifyItemRemoved(cols.size - 1)
                cols.addAll(response as List<CollectionPojo>)
                adapter.notifyDataSetChanged()
                adapter.setLoaded()
            }
        }
    }

}
