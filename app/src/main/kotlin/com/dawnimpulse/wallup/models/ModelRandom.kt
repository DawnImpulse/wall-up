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
package com.dawnimpulse.wallup.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dawnimpulse.wallup.BuildConfig
import com.dawnimpulse.wallup.network.controller.CtrlImage
import com.dawnimpulse.wallup.objects.ObjectIssue
import com.dawnimpulse.wallup.objects.ObjectLoading
import com.dawnimpulse.wallup.objects.ObjectReload
import com.dawnimpulse.wallup.utils.reusables.ERROR
import com.dawnimpulse.wallup.utils.reusables.F
import com.dawnimpulse.wallup.utils.reusables.LIST_COUNT
import com.dawnimpulse.wallup.utils.reusables.RELOAD
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ModelRandom() : ViewModel() {
    private val randomList = mutableListOf<Any>()
    private val liveList = MutableLiveData<List<Any>>()
    private val errorHandler = MutableLiveData<ObjectIssue>()
    private var loaded = false

    init {
        fetchRandomImages()
    }


    /**
     * get list of images
     *
     * @return LiveData<List<Any>>
     */
    fun getList(): LiveData<List<Any>> {
        return liveList
    }

    /**
     * handling errors
     *
     */
    fun errors(): LiveData<ObjectIssue> {
        return errorHandler
    }

    /**
     * reload images
     */
    fun reload(){
        fetchRandomImages()
    }

    /**
     * load more images
     */
    fun loadMore() {
        if (!loaded) fetchMoreImages()
    }

    /**
     * fetch latest images
     */
    private fun fetchRandomImages() {
        viewModelScope.launch {
            if (BuildConfig.DEBUG) delay(1000)
            try {
                val contents = CtrlImage.random(LIST_COUNT)
                contents.forEach { it.height = F.getRandomHeight()}
                randomList.addAll(contents)
                randomList.add(ObjectLoading()) // loading obj
                liveList.postValue(randomList) // post list
            } catch (e: Exception) {
                errorHandler.postValue(F.handleException(e, ERROR.LIST.RANDOM, false))
                e.printStackTrace()
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    /**
     * fetch more images
     */
    private fun fetchMoreImages(){
        viewModelScope.launch {
            if (BuildConfig.DEBUG) delay(1000)
            try {
                // check if last item is reload
                if (randomList[randomList.size - 1] is ObjectReload){
                    randomList.removeAt(randomList.size - 1)
                    randomList.add(ObjectLoading())
                    liveList.postValue(randomList)
                }

                // fetch content
                val contents = CtrlImage.random(LIST_COUNT)
                randomList.removeAt(randomList.size - 1)
                contents.forEach { it.height = F.getRandomHeight() }
                randomList.addAll(contents)
                randomList.add(ObjectLoading()) // loading obj
                // handle exception
            } catch (e: Exception) {
                randomList.removeAt(randomList.size - 1)
                randomList.add(ObjectReload(RELOAD.MORE.RANDOM, F.handleException(e, ERROR.LIST.MORE.RANDOM, true)))
                e.printStackTrace()
                FirebaseCrashlytics.getInstance().recordException(e)
            }
            // post list
            liveList.postValue(randomList)
        }
    }
}