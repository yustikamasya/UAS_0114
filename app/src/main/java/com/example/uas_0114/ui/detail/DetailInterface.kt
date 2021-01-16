package com.example.uas_0114.ui.detail

import android.app.Activity
import android.content.Context
import com.example.uas_0114.model.EventItem
import com.example.uas_0114.utils.ServerCallback

interface DetailInterface {
    fun isNetworkAvailable(context: Activity): Boolean
    fun geDetailTeam(context: Activity, id : String, callback: ServerCallback)
    fun isSuccess(response: String): Boolean
    fun parsingData(context: Activity, response: String): ArrayList<EventItem>
    fun addFavorites(context: Context, data: EventItem)
    fun removeFavorites(context: Context, data: EventItem)
    fun isFavorite(context: Context, data: EventItem): Boolean
}