package com.example.newbiechen.nbreader.uilts.mediastore

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader

/**
 *  author : newbiechen
 *  date : 2019-08-17 16:34
 *  description :
 */

typealias OnMediaResultCallback<T> = (value: T) -> Unit

object MediaStoreHelper {
    const val ID_LOCAL_BOOKS = 1

    fun cursorLocalBooks(
        activity: FragmentActivity,
        resultCallback: OnMediaResultCallback<List<LocalBookInfo>>
    ) {
        val loaderManager: LoaderManager = LoaderManager.getInstance(activity)
        loaderManager.initLoader(
            ID_LOCAL_BOOKS,
            null,
            MediaLoaderCallback(activity.applicationContext, resultCallback)
        )
    }
}

internal class MediaLoaderCallback<T>(
    private var context: Context,
    private var resultCallback: OnMediaResultCallback<T>
) : LoaderManager.LoaderCallbacks<Cursor> {

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        // 当前只有 LocalBookLoader 一种，默认返回它
        return when (id) {
            MediaStoreHelper.ID_LOCAL_BOOKS -> LocalBookLoader(context)
            else -> LocalBookLoader(context)
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        if (loader is LocalBookLoader) {
            var books = loader.parseData(data) as T
            resultCallback(books)
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {

    }
}